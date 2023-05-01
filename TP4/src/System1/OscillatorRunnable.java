package System1;

import IntegrationMethods.Beeman;
import utils.Pair;
import utils.Particle;

public class OscillatorRunnable {
    private static final double delta_t = Math.pow(10, -2);
    public static void main(String[] args) {
        DampedOscillator osc = new DampedOscillator();
        Beeman beeman = new Beeman(delta_t);
        Pair<Double> out =
                beeman.updateParams(new Particle(0,0,0,0,0,0,0,0));
        System.out.println(out);
    }
}
