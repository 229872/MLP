public class Network {
    //wyjscia, wagi i bias przedstawione sa jako macierze, pierwszy wymiar mowi o
    //przynaleznosci do warstwy, 2 identyfikuje neuron w danej warstwie
    //3 wymiar w wagach identyfikuje neuron z poprzedniej warstwy
    private double[][] output;
    private double[][][] weights;
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

        for (int i = 0; i < NETWORK_SIZE; i++) {
            this.output[i] = new double[NETWORK_LAYER_SIZES[i]];
            this.bias[i] = new double[NETWORK_LAYER_SIZES[i]];
            //wagi nie obejmuja 0 warstwy wejsciowej
            if(i > 0) {
                weights[i] = new double[NETWORK_LAYER_SIZES[i]][NETWORK_LAYER_SIZES[i-1]];
            }
        }
    }
}
