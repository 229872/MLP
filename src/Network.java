import java.util.Random;

public class Network {
    //wyjscia, wagi i bias przedstawione sa jako macierze, pierwszy wymiar mowi o
    //przynaleznosci do warstwy, 2 identyfikuje neuron w danej warstwie
    //3 wymiar w wagach identyfikuje neuron z poprzedniej warstwy

    //output[indexOfLayer][indexOfNeuron]
    private double[][] output;
    //weights[indexOfLayer][indexOfNeuron][indexOfprevNeuron]
    private double[][][] weights;
    //bias[indexOfLayer][indexOfNeuron]
    private double[][] bias;

    private double[][] error_signal;
    private double[][] output_derivative;

    public final int[] NETWORK_LAYER_SIZES;
    public final int INPUT_SIZE;
    public final int OUTPUT_SIZE;
    public final int NETWORK_SIZE;

    public Network(int ... NETWORK_LAYER_SIZES) {
        //argumenty konstruktora to kolejne wielkosci kazdej warstwy
        this.NETWORK_LAYER_SIZES = NETWORK_LAYER_SIZES;
        this.INPUT_SIZE = NETWORK_LAYER_SIZES[0];
        this.NETWORK_SIZE = NETWORK_LAYER_SIZES.length;
        this.OUTPUT_SIZE = NETWORK_LAYER_SIZES[NETWORK_SIZE - 1];


        this.output = new double[NETWORK_SIZE][];
        this.weights = new double[NETWORK_SIZE][][];
        this.bias = new double[NETWORK_SIZE][];

        this.error_signal = new double[NETWORK_SIZE][];
        this.output_derivative = new double[NETWORK_SIZE][];

        for (int layer = 0; layer < NETWORK_SIZE; layer++) {
            this.output[layer] = new double[NETWORK_LAYER_SIZES[layer]];
            this.error_signal[layer] = new double[NETWORK_LAYER_SIZES[layer]];
            this.output_derivative[layer] = new double[NETWORK_LAYER_SIZES[layer]];
            this.bias[layer] = new double[NETWORK_LAYER_SIZES[layer]];
            for (int neuron = 0; neuron < NETWORK_LAYER_SIZES[layer]; neuron++) {
                this.bias[layer][neuron] = 1;
            }
            //wagi nie obejmuja 0 warstwy wejsciowej
            if(layer > 0) {
                Random random = new Random();
                weights[layer] = new double[NETWORK_LAYER_SIZES[layer]][];
                for (int neuron = 0; neuron < NETWORK_LAYER_SIZES[layer]; neuron++) {
                    weights[layer][neuron] = new double[NETWORK_LAYER_SIZES[layer - 1]];
                    for (int prevLayerNeuron = 0; prevLayerNeuron < NETWORK_LAYER_SIZES[layer - 1]; prevLayerNeuron++) {
                        weights[layer][neuron][prevLayerNeuron] = random.nextDouble(-0.5,0.5);
                    }
                }
            }
        }
    }

    public double sigmoid(double x) {
        return (double) 1 / (1 + Math.exp(-x));
    }

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
                sum += bias[layer][neuron];
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
                delta = -eta * error_signal[layer][neuron];
                bias[layer][neuron] += delta;
            }
        }
    }
}
