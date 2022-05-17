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

        for (int layer = 0; layer < NETWORK_SIZE; layer++) {
            this.output[layer] = new double[NETWORK_LAYER_SIZES[layer]];
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
            }
        }
        return output[NETWORK_SIZE - 1];
    }
}
