import java.lang.reflect.Array;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        Network network = new Network(4,1,3,4);
        double[] output = network.calculate(0.2,0.9,0.3,0.4);
        System.out.println(Arrays.toString(output));
    }
}