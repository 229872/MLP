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
}