import java.io.*;
import java.util.Scanner;

public class DataManager {
    
    public static int[] getCountOfData(String path) throws IOException {
        int rows = 0, cols = 0;
        String row;
        BufferedReader reader = new BufferedReader(new FileReader(path));
        while((row = reader.readLine()) != null) {
            if(rows == 0) {
                cols = row.split(",").length;
            }
            rows++;
        }
        int[] output = new int[2];
        output[0] = rows;
        output[1] = cols;
        return output;
    }
    
    public static double[][][] readFromFile(String path) throws IOException {
        int[] dataDimensions = getCountOfData(path);
        int rows = dataDimensions[0];
        int cols = dataDimensions[1];
        String[] values;
        
        double[][][] data = new double[2][][];
        data[0] = new double[rows][cols - 1];
        data[1] = new double[rows][3];
        BufferedReader newReader = new BufferedReader(new FileReader(path));
        
        for (int row = 0; row < rows; row++) {
            values = newReader.readLine().split(",");
            for(int col = 0; col < cols - 1; col++) {
                data[0][row][col] = Double.parseDouble(values[col]);
            }
            if(values[cols - 1].equals("Iris-setosa")) {
                data[1][row][0] = 1;
                data[1][row][1] = 0;
                data[1][row][2]= 0;
            } else if (values[cols - 1].equals("Iris-versicolor")) {
                data[1][row][0] = 0;
                data[1][row][1] = 1;
                data[1][row][2]= 0;
            } else if (values[cols - 1].equals("Iris-virginica")) {
                data[1][row][0] = 0;
                data[1][row][1] = 0;
                data[1][row][2]= 1;
            }
        }
        newReader.close();

        return data;
    }

    public static void showData(double[][] data) {
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                System.out.print(data[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void addDataTofile(double data) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("data.txt",true));
        PrintWriter out = new PrintWriter(writer);
        writer.write(Double.toString(data));
        writer.write("\n");
        writer.close();
    }

    public static void sendTestData(double[] input, double networkError, double[] answer, double[] errors,
                                    double[] outputs, double[][] outputWages, double[][] neuronsOutputs, double[][][] weights) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("testData.txt",true));
        writer.write("-------------------------------------------------------------------------------------------------------\n");
        writer.write("Wzorzec wejsciowy: ");
        for (double in : input) {
            writer.write(Double.toString(in) + " ");
        }
        writer.write("\nBłąd sieci: ");
        writer.write(Double.toString(networkError));
        writer.write("\nPożądany wzorzec odpowiedzi: ");
        for (double ans : answer) {
            writer.write(Double.toString(ans) + " ");
        }
        writer.write("\nBłędy na poszczególnych wyjściach: ");
        for (double err : errors) {
            writer.write(Double.toString(err) + " ");
        }
        writer.write("\nWartości wyjściowych neuronów wyjściowych: ");
        for (double out : outputs) {
            writer.write(Double.toString(out) + " ");
        }
        writer.write("\n\nWagi neuronów wyjściowych: \n");
        for (double[] outputWage : outputWages) {
            for (double v : outputWage) {
                writer.write(Double.toString(v) + " ");
            }
            writer.write("\n");
        }
        writer.write("\nWartości wyjściowe neuronów ukrytych: \n");
        for (int layer = 1; layer < neuronsOutputs.length - 1; layer++) {
            for (int neuronValue = 0; neuronValue < neuronsOutputs[layer].length; neuronValue++) {
                writer.write(Double.toString(neuronsOutputs[layer][neuronValue]) + " ");
            }
            writer.write("\n");
        }
        writer.write("\nWartości wag neuronów ukrytych od warstw dalszych względem wejść sieci do bliższych: \n");
        for (int layer = weights.length - 2; layer > 0; layer--) {
            writer.write("Warstwa : " + layer + "\n");
            for (int indexOfNeuron = 0; indexOfNeuron < weights[layer].length; indexOfNeuron++) {
                for (int indexOfPrevNeuron = 0; indexOfPrevNeuron < weights[layer][indexOfNeuron].length; indexOfPrevNeuron++) {
                    writer.write(Double.toString(weights[layer][indexOfNeuron][indexOfPrevNeuron]) + " ");
                }
                writer.write("\n");
            }
            writer.write("\n");
        }
        writer.write("-------------------------------------------------------------------------------------------------------\n");
        writer.close();
    }

    public static void sendTestData(double[][] outputs) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("autonekoderData.txt",true));
        writer.write(Double.toString(outputs[1][0]) + " ");
        writer.write(Double.toString(outputs[1][1]) + " ");
        writer.close();
    }
}