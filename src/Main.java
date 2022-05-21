import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
        double[][][] fullData = DataManager.readFromFile("train.csv");
        double[][] inputs = fullData[0];
        double[][] outputs = fullData[1];

        Network network = new Network(4,1,3,3);
        for (int i = 0; i < 1000; i++) {
            for (int j = 0; j < inputs.length; j++) {
                network.train(inputs[j],outputs[j],0.3);
            }
        }
        double[] output = network.calculate(5.0,3.2,1.2,0.2);
        System.out.println(Arrays.toString(output));
        System.out.println(checkOutput(output));
    }

    public static String checkOutput(double[] output) {
        int maxAt = 0;
        for (int i = 1; i < output.length; i++) {
            maxAt = output[i] > output[maxAt] ? i : maxAt;
        }
        if(maxAt == 0) {
            return "Iris-setosa";
        } else if(maxAt == 1) {
            return "Iris-versicolor";
        } else  {
            return "Iris-virginica";
        }
    }
}