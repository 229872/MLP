import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        String ch;
        int choice;
        System.out.print("Enter Y if you wan iris data or sth else for autoenkoder: ");
        ch = scanner.nextLine();
        if(ch.equals("Y")) {
            //tworzenie sieci

            System.out.print("Enter path with data: ");
            String path = scanner.nextLine();

            //pobieranie danych z pliku
            double[][][] fullData = DataManager.readFromFile(path);
            double[][] inputs = fullData[0];
            double[][] outputs = fullData[1];

            menu1();
            choice = scanner.nextInt();
            Network network = createNetwork(choice,true);
            //--------------------------------------------------------------
            //trenowanie, zapisywanie, testowanie
            do {
                menu2();
                choice = scanner.nextInt();
            } while (networkOperations(network,choice,inputs,outputs,true));
        } else {
            menu1();
            choice = scanner.nextInt();
            scanner.nextLine();
            Network network = createNetwork(choice,false);
            double[][] inputs = {
                    {1,0,0,0},
                    {0,1,0,0},
                    {0,0,1,0},
                    {0,0,0,1}
            };
            double[][] outputs = {
                    {1,0,0,0},
                    {0,1,0,0},
                    {0,0,1,0},
                    {0,0,0,1}
            };
            do {
                menu2();
                choice = scanner.nextInt();
            } while (networkOperations(network,choice,inputs,outputs,false));
        }
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
        return testOutput.equals(dataTestOutput);
    }

    public static void titlesMenu() {
        System.out.println("                            Network Output                       Network answer          Real answer       Is true?");
    }

    public static void menu1() {
        System.out.println("1) Create new network");
        System.out.println("2) Load network");
    }

    public static void menu2() {
        System.out.println("1) Learn network");
        System.out.println("2) Test network");
        System.out.println("3) Save network");
        System.out.println("4) Exit program");
    }

    public static Network createNetwork(int choice, boolean variant) throws IOException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        switch (choice) {
            case 1 -> {
                if(variant) {
                    String[] line;
                    System.out.println("Enter sizes of each layer in network seperated by coma (e.g 4,4,3");
                    line = scanner.nextLine().split(",");
                    int[] numbers = new int[line.length];
                    for (int i = 0; i < line.length; i++) {
                        numbers[i] = Integer.parseInt(line[i]);
                    }

                    //tworzenie sieci
                    return new Network(numbers);
                } else {
                    return new Network(4,2,4);
                }
            }
            case 2 -> {
                System.out.println("Enter path for file you want load network");
                return Network.load(scanner.nextLine());
            }
        }
        return null;
    }

    public static boolean networkOperations(Network network, int choice, double[][] inputs, double[][] outputs, boolean variant) throws IOException {
        Scanner scanner = new Scanner(System.in);
        switch (choice) {
            case 1:
                double learningRate, error;
                int iterations;
                System.out.print("Enter learning rate: ");
                learningRate = scanner.nextDouble();
                scanner.nextLine();
                System.out.print("Enter number of iterations: ");
                iterations = scanner.nextInt();
                scanner.nextLine();
                System.out.print("Enter error: ");
                error = scanner.nextDouble();
                scanner.nextLine();
                System.out.print("Do you want randomised data? (Y,N): ");
                if(scanner.nextLine().equals("Y")) {
                    randomizeMatrix(inputs,outputs);
                }
                System.out.print("Do you want momentum? (Y,N): ");
                if(scanner.nextLine().equals("Y")) {
                    System.out.print("Enter alfa value (pref: α∈(0,1): ");
                    double alfa = scanner.nextDouble();
                    System.out.println("Starting learning");
                    //uczenie
                    network.trainWithMomentum(inputs,outputs,learningRate,alfa,iterations,error);
                    break;
                }
                System.out.println("Starting learning");
                //uczenie
                network.train(inputs,outputs,learningRate,iterations, error);
                break;
            case 2:
                if(variant) {
                    //testowanie
                    System.out.println("Enter name of file to read test data");
                    double [][][] testData = DataManager.readFromFile(scanner.nextLine());
                    double[][] inputTestData = testData[0];
                    double[][] answers = testData[1];
                    int[][] confusionMatrix = network.test(inputTestData,answers);
                    showMatrix(confusionMatrix);
                    double[][] metrics = calculateMetrics(confusionMatrix);
                    showMetrics(metrics);
                } else {
                    network.test(inputs,outputs,variant);
                }

                break;
            case 3:
                System.out.println("Enter name of file to save network");
                network.save(scanner.nextLine());
                break;
            case 4:
                return false;
        }
        return true;
    }

    public static void randomizeMatrix(double[][] inputData, double[][] outputData) {
        Random random = new Random();
        double[] inputBuff, outputBuff;
        int randIndex1, randIndex2;
        for (int i = 0; i < random.nextInt(0,inputData.length); i++) {
            randIndex1 = random.nextInt(0,inputData.length);
            randIndex2 = random.nextInt(0,inputData.length);
            //przeniesienie wylosowanego wiersza z wejsc i wyjsc do bufora
            inputBuff = inputData[randIndex1];
            outputBuff = outputData[randIndex1];

            inputData[randIndex1] = inputData[randIndex2];
            outputData[randIndex1] = outputData[randIndex2];

            inputData[randIndex2] = inputBuff;
            outputData[randIndex2] = outputBuff;

        }
    }

    public static void showMatrix(int[][] matrix) {
        System.out.println();
        for (int[] row : matrix) {
            for (int value : row) {
                System.out.print(value + " ");
            }
            System.out.println();
        }
    }

    public static double[][] calculateMetrics(int[][] matrix) {
        double[][] metrics = new double[3][3];
        //      Metrics matrics                                             Confusion Matrix
        //          recall     precision      f-measure                     setosa      versicolor      virginica
        //setosa                                                setosa
        //versicolor                                            versicolor
        //virginica                                             virginica


        //Recall = TP/(TP + FN)
        metrics[0][0] = (double) matrix[0][0] / (matrix[0][0] + matrix[0][1] + matrix[0][2]);
        metrics[1][0] = (double) matrix[1][1] / (matrix[1][1] + matrix[1][0] + matrix[1][2]);
        metrics[2][0] = (double) matrix[2][2] / (matrix[2][2] + matrix[2][0] + matrix[2][1]);

        //Precision = TP/(TP + FP)
        metrics[0][1] = (double) matrix[0][0] / (matrix[0][0] + matrix[1][0] + matrix[2][0]);
        metrics[1][1] = (double) matrix[1][1] / (matrix[1][1] + matrix[0][1] + matrix[2][1]);
        metrics[2][1] = (double) matrix[2][2] / (matrix[2][2] + matrix[0][2] + matrix[1][2]);

        //F1 = 2 * precision * recall / (precision + recall)
        metrics[0][2] = 2 * metrics[0][1] * metrics[0][0] / (metrics[0][1] + metrics[0][0]);
        metrics[1][2] = 2 * metrics[1][1] * metrics[1][0] / (metrics[1][1] + metrics[1][0]);
        metrics[2][2] = 2 * metrics[2][1] * metrics[2][0] / (metrics[2][1] + metrics[2][0]);
        return metrics;
    }

    public static void showMetrics(double[][] metrics) {
        System.out.println("                Recall   Precision   F1-score");
        for (int i = 0; i < metrics.length; i++) {
            if(i == 0) {
                System.out.print("For setosa:      ");
            } else if (i == 1) {
                System.out.print("For versicolor:  ");
            } else {
                System.out.print("For virginica:   ");
            }
            for (double value : metrics[i]) {
                System.out.print(value + "      ");
            }
            System.out.println();
        }
    }
}