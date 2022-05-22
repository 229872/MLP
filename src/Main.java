import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
        //pobieranie danych z pliku
        double[][][] fullData = DataManager.readFromFile("train.csv");
        double[][] inputs = fullData[0];
        double[][] outputs = fullData[1];

        //tworzenie sieci
        Network network = new Network(4,2,3,3);

        //uczenie
        network.train(inputs,outputs,0.4,10000);

        //testowanie
        double [][][] testData = DataManager.readFromFile("test.csv");
        double[][] inputTestData = testData[0];
        double[][] answers = testData[1];


        network.test(inputTestData,answers);
    }

    public static String showOutput(double[] output) {
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

    public static boolean checkOutput(String testOutput, String dataTestOutput) {
        if(testOutput.equals(dataTestOutput)) {
            return true;
        } else {
            return false;
        }
    }

    public static void titlesMenu() {
        System.out.println("                            Network Output                       Network answer          Real answer       Is true?");
    }
}