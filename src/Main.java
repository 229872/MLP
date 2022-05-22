import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        //tworzenie sieci
        Scanner scanner = new Scanner(System.in);
        int choice;

        System.out.print("Enter path with data: ");
        String path = scanner.nextLine();

        //pobieranie danych z pliku
        double[][][] fullData = DataManager.readFromFile(path);
        double[][] inputs = fullData[0];
        double[][] outputs = fullData[1];

        menu1();
        choice = scanner.nextInt();
        Network network = createNetwork(choice);
        //--------------------------------------------------------------
        //trenowanie, zapisywanie, testowanie
        do {
            menu2();
            choice = scanner.nextInt();
        } while (networkOperations(network,choice,inputs,outputs));
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

    public static Network createNetwork(int choice) throws IOException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        switch (choice) {
            case 1 -> {
                String[] line;
                System.out.println("Enter sizes of each layer in network seperated by coma (e.g 4,4,3");
                line = scanner.nextLine().split(",");
                int[] numbers = new int[line.length];
                for (int i = 0; i < line.length; i++) {
                    numbers[i] = Integer.parseInt(line[i]);
                }

                //tworzenie sieci
                return new Network(numbers);
            }
            case 2 -> {
                System.out.println("Enter path for file you want load network");
                return Network.load(scanner.nextLine());
            }
        }
        return null;
    }

    public static boolean networkOperations(Network network, int choice, double[][] inputs, double[][] outputs) throws IOException {
        Scanner scanner = new Scanner(System.in);
        switch (choice) {
            case 1:
                double learningRate;
                int iterations;
                System.out.print("Enter learning rate: ");
                learningRate = scanner.nextDouble();
                System.out.print("Enter number of iterations: ");
                iterations = scanner.nextInt();
                System.out.println("Starting learning");

                //uczenie
                network.train(inputs,outputs,learningRate,iterations);
                break;
            case 2:
                //testowanie
                System.out.println("Enter name of file to read test data");
                double [][][] testData = DataManager.readFromFile(scanner.nextLine());
                double[][] inputTestData = testData[0];
                double[][] answers = testData[1];
                network.test(inputTestData,answers);
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

}