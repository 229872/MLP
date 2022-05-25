import java.io.*;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Network implements Serializable {
    //wyjscia, wagi i bias przedstawione sa jako macierze, pierwszy wymiar mowi o
    //przynaleznosci do warstwy, 2 identyfikuje neuron w danej warstwie
    //3 wymiar w wagach identyfikuje neuron z poprzedniej warstwy

    //outputs[indexOfLayer][indexOfNeuron]
    private final double[][] output;
    //weights[indexOfLayer][indexOfNeuron][indexOfprevNeuron]
    private final double[][][] weights;
    //bias[indexOfLayer][indexOfNeuron]
    private double[][] bias;
    private final boolean isBias;

    private final double[][] error_signal;
    private final double[][] output_derivative;

    public final int[] NETWORK_LAYER_SIZES;
    public final int INPUT_SIZE;
    public final int OUTPUT_SIZE;
    public final int NETWORK_SIZE;
    //pomocnicze pole uzywane przy momentum
    private final double[][][] prevWeights;

    public Network(int ... NETWORK_LAYER_SIZES) {
        //czy chcemy bias
        System.out.print("Do you want bias? (Y/N): ");
        Scanner scanner = new Scanner(System.in);
        String decision = scanner.nextLine();
        this.isBias = decision.equals("Y");
        if(isBias) {
            System.out.println("Bias included");
        } else {
            System.out.println("Bias not included");
        }

        //argumenty konstruktora to kolejne wielkosci kazdej warstwy
        this.NETWORK_LAYER_SIZES = NETWORK_LAYER_SIZES;
        this.INPUT_SIZE = NETWORK_LAYER_SIZES[0];
        this.NETWORK_SIZE = NETWORK_LAYER_SIZES.length;
        this.OUTPUT_SIZE = NETWORK_LAYER_SIZES[NETWORK_SIZE - 1];


        this.output = new double[NETWORK_SIZE][];
        this.weights = new double[NETWORK_SIZE][][];
        this.prevWeights = new double[NETWORK_SIZE][][];
        if(isBias) {
            this.bias = new double[NETWORK_SIZE][];
        }
        this.error_signal = new double[NETWORK_SIZE][];
        this.output_derivative = new double[NETWORK_SIZE][];

        for (int layer = 0; layer < NETWORK_SIZE; layer++) {
            this.output[layer] = new double[NETWORK_LAYER_SIZES[layer]];
            this.error_signal[layer] = new double[NETWORK_LAYER_SIZES[layer]];
            this.output_derivative[layer] = new double[NETWORK_LAYER_SIZES[layer]];
            if(isBias) {
                this.bias[layer] = new double[NETWORK_LAYER_SIZES[layer]];
                for (int neuron = 0; neuron < NETWORK_LAYER_SIZES[layer]; neuron++) {
                    this.bias[layer][neuron] = 1;
                }
            }

            //wagi nie obejmuja 0 warstwy wejsciowej
            if(layer > 0) {
                Random random = new Random();
                double randomValue;
                weights[layer] = new double[NETWORK_LAYER_SIZES[layer]][];
                prevWeights[layer] = new double[NETWORK_LAYER_SIZES[layer]][];
                for (int neuron = 0; neuron < NETWORK_LAYER_SIZES[layer]; neuron++) {
                    weights[layer][neuron] = new double[NETWORK_LAYER_SIZES[layer - 1]];
                    prevWeights[layer][neuron] = new double[NETWORK_LAYER_SIZES[layer - 1]];
                    for (int prevLayerNeuron = 0; prevLayerNeuron < NETWORK_LAYER_SIZES[layer - 1]; prevLayerNeuron++) {
                        //losowanie wag z przedzialu
                        do {
                            randomValue = random.nextDouble(-0.5,0.5);
                        } while (randomValue == 0.0);
                        weights[layer][neuron][prevLayerNeuron] = randomValue;
                        prevWeights[layer][neuron][prevLayerNeuron] = 0.0;
                    }
                }
            }
        }
    }

    public double sigmoid(double x) {
        return (double) 1 / (1 + Math.exp(-x));
    }

    //propagacja w przód
    public double[] calculate(double... input) {
        //inicjowanie warstwy wejsciowej
        this.output[0] = input;
        double sum;
        for (int layer = 1; layer < NETWORK_SIZE; layer++) {
            for (int neuron = 0; neuron < NETWORK_LAYER_SIZES[layer]; neuron++) {
                sum = 0.0;
                for (int prevLayerNeuron = 0; prevLayerNeuron < NETWORK_LAYER_SIZES[layer - 1]; prevLayerNeuron++) {
                    //sumujemy wejscia do neuronu czyli wyjscia z warstwy poprzedniej pomnozone przez wagi pomiedzy neuronami
                    sum += output[layer - 1][prevLayerNeuron] * weights[layer][neuron][prevLayerNeuron];
                }
                if(isBias) {
                    sum += bias[layer][neuron];
                }
                output[layer][neuron] = sigmoid(sum);
                output_derivative[layer][neuron] = (output[layer][neuron] * (1 - output[layer][neuron]));
            }
        }
        return output[NETWORK_SIZE - 1];
    }

    public void train(double[] input, double[] target, double eta) {
        calculate(input);
        backpropError(target);
        updateWeights(eta);
    }

    public void trainWithMomentum(double[] input, double[] target, double eta, double alfa) {
        calculate(input);
        backpropError(target);
        updateWeights(eta,alfa);
    }

    public void train(double[][] inputs, double[][] targets, double eta, int iterations, double error) throws IOException {
        int i = 0;
        while (i < iterations && calculateError(inputs,targets) > error){
            for (int j = 0; j < inputs.length; j++) {
                train(inputs[j],targets[j],eta);
            }
            if(i % 50 == 0) {
                DataManager.addDataTofile(calculateError(inputs,targets));
            }
            i++;
        }
    }

    public void trainWithMomentum(double[][] inputs, double[][] targets, double eta, double alfa, int iterations, double error) throws IOException {
        int i = 0;
        while (i < iterations && calculateError(inputs,targets) > error) {
            for (int j = 0; j < inputs.length; j++) {
                trainWithMomentum(inputs[j],targets[j],eta,alfa);
            }
            if(i % 50 == 0) {
                DataManager.addDataTofile(calculateError(inputs,targets));
            }
            i++;
        }
    }

    public void test(double[][] input, double[][] answers, boolean choice) throws IOException {
        for (int i = 0; i < input.length; i++) {
            double[] output = calculate(input[i]);
            System.out.println(Arrays.toString(output));
            DataManager.sendTestData(input[i],calculateError(input[i],answers[i]),answers[i],error_signal[NETWORK_SIZE-1],output,
                    weights[NETWORK_SIZE-1],this.output,this.weights);
        }
    }

    public int[][] test(double[][] input, double[][] answers) throws IOException {
        int[][] confusionMatrix = new int[3][3];
        int setosa,versicolor,virginica,setosaAll,versicolorAll,virginicaAll,all;
        setosa = versicolor = virginica = setosaAll = versicolorAll = virginicaAll = 0;
        String type;
        for (int i = 0; i < input.length; i++) {
            double[] output = calculate(input[i]);
            DataManager.sendTestData(input[i],calculateError(input[i],answers[i]),answers[i],error_signal[NETWORK_SIZE-1],output,
                    weights[NETWORK_SIZE-1],this.output,this.weights);


            Main.titlesMenu();
            System.out.print(Arrays.toString(output) + "  ");
            System.out.print(Main.showOutput(output) + "          ");
            System.out.print(Main.showOutput(answers[i]) + "          ");
            type = Main.showOutput(answers[i]);
            //confusion Matrix  setosa  versicolor  virginica
            //           setosa
            //          versicolor
            //          virginica

            switch (type) {
                case "Iris-setosa":
                    setosaAll++;
                    if(Main.checkOutput(Main.showOutput(output),Main.showOutput(answers[i]))) {
                        setosa++;
                        confusionMatrix[0][0]++;
                    } else if(Main.showOutput(output).equals("Iris-versicolor")){
                        confusionMatrix[0][1]++;
                    } else if(Main.showOutput(output).equals("Iris-virginica")) {
                        confusionMatrix[0][2]++;
                    }
                    break;
                case "Iris-versicolor":
                    versicolorAll++;
                    if(Main.checkOutput(Main.showOutput(output),Main.showOutput(answers[i]))) {
                        versicolor++;
                        confusionMatrix[1][1]++;
                    } else if(Main.showOutput(output).equals("Iris-setosa")){
                        confusionMatrix[1][0]++;
                    } else if(Main.showOutput(output).equals("Iris-virginica")) {
                        confusionMatrix[1][2]++;
                    }
                    break;
                case "Iris-virginica":
                    virginicaAll++;
                    if(Main.checkOutput(Main.showOutput(output),Main.showOutput(answers[i]))) {
                        virginica++;
                        confusionMatrix[2][2]++;
                    } else if(Main.showOutput(output).equals("Iris-setosa")){
                        confusionMatrix[2][0]++;
                    } else if(Main.showOutput(output).equals("Iris-versicolor")) {
                        confusionMatrix[2][1]++;
                    }
                    break;
            }
            System.out.println(Main.checkOutput(Main.showOutput(output),Main.showOutput(answers[i])));
            System.out.println();
        }
        all = versicolorAll + virginicaAll + setosaAll;
        int allTrue = setosa + versicolor + virginica;
        System.out.println("Result: All: " + allTrue + "/" + all + " Setosa: " + setosa + "/" + setosaAll +
                " Versicolor: " + versicolor + "/" + versicolorAll + " Virginica: " + virginica + "/" + virginicaAll);
        return confusionMatrix;
    }

    public void backpropError(double[] target) {
        for (int neuron = 0; neuron < NETWORK_LAYER_SIZES[NETWORK_SIZE - 1]; neuron++) {
            error_signal[NETWORK_SIZE - 1][neuron] =
                    (output[NETWORK_SIZE - 1][neuron] - target[neuron]) * output_derivative[NETWORK_SIZE - 1][neuron];
        }
        for(int layer = NETWORK_SIZE - 2; layer > 0; layer--) {
            for(int neuron = 0; neuron < NETWORK_LAYER_SIZES[layer]; neuron++) {
                double sum = 0.0;
                for (int nextNeuron = 0; nextNeuron < NETWORK_LAYER_SIZES[layer+1]; nextNeuron++) {
                    sum += weights[layer + 1][nextNeuron][neuron] * error_signal[layer + 1][nextNeuron];
                }
                this.error_signal[layer][neuron] = sum * output_derivative[layer][neuron];
            }
        }
    }

    public void updateWeights(double eta) {
        double delta;
        for(int layer = 1; layer < NETWORK_SIZE; layer++) {
            for(int neuron = 0; neuron < NETWORK_LAYER_SIZES[layer]; neuron++) {
                for(int prevNeuron = 0; prevNeuron < NETWORK_LAYER_SIZES[layer - 1]; prevNeuron++) {
                    delta = -eta * output[layer-1][prevNeuron] * error_signal[layer][neuron];
                    weights[layer][neuron][prevNeuron] += delta;
                }
                if(isBias) {
                    delta = -eta * error_signal[layer][neuron];
                    bias[layer][neuron] += delta;
                }
            }
        }
    }

    public void updateWeights(double eta, double alfa) {
        double delta;
        for(int layer = 1; layer < NETWORK_SIZE; layer++) {
            for(int neuron = 0; neuron < NETWORK_LAYER_SIZES[layer]; neuron++) {
                for(int prevNeuron = 0; prevNeuron < NETWORK_LAYER_SIZES[layer - 1]; prevNeuron++) {
                    delta = -eta * output[layer-1][prevNeuron] * error_signal[layer][neuron] +
                            alfa * (weights[layer][neuron][prevNeuron] - prevWeights[layer][neuron][prevNeuron]);
                    weights[layer][neuron][prevNeuron] += delta;
                    prevWeights[layer][neuron][prevNeuron] = weights[layer][neuron][prevNeuron];
                }
                if(isBias) {
                    delta = -eta * error_signal[layer][neuron];
                    bias[layer][neuron] += delta;
                }
            }
        }
    }

    public void save(String path) throws IOException {
        File file = new File(path);
        ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file));
        output.writeObject(this);
        output.flush();
        output.close();
    }

    public static Network load(String path) throws IOException, ClassNotFoundException {
        File file = new File(path);
        ObjectInputStream input = new ObjectInputStream(new FileInputStream(file));
        Network network = (Network) input.readObject();
        input.close();
        return network;
    }

    public double calculateError(double[][] inputs, double[][] targets) {
        double v = 0.0;
        for (int i = 0; i < inputs.length; i++) {
            calculate(inputs[i]);
            for (int j = 0; j < targets[i].length; j++) {
                v += (targets[i][j] - output[NETWORK_SIZE-1][j]) * (targets[i][j] - output[NETWORK_SIZE-1][j]);
            }
        }

        return v / (2.0d * targets.length * targets[0].length);
    }

    public double calculateError(double[] input, double[] target) {
        calculate(input);
        double v = 0.0;
        for (int i = 0; i < target.length; i++) {
            v += (target[i] - output[NETWORK_SIZE-1][i]) * (target[i] - output[NETWORK_SIZE-1][i]);
        }
        return v / (2.0d * target.length);
    }
}
