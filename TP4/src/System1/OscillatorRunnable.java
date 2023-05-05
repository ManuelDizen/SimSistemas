package System1;

import IntegrationMethods.Beeman;
import IntegrationMethods.GearPredictorCorrector;
import utils.FileUtils;
import utils.Particle;

import java.io.File;
import static IntegrationMethods.Euler.calculateEulerR;

public class OscillatorRunnable {

    private static final double amplitude = 1.0;
    private static double delta_t = Math.pow(10, -2);
    private static final double total_time = 5; // s
    private static final FileUtils fileUtils = new FileUtils();
    private static final DampedOscillator osc = new DampedOscillator();
    public static void main(String[] args) {
        FileUtils.purgeDirectory(new File("src/output"));
        int mass = osc.getMass();
        double initial_vx = (-amplitude * osc.getGamma())/(2.0*mass);
        double[] delta_ts = {-2, -3, -4, -5};
        for(double t : delta_ts) {
            delta_t = Math.pow(10, t);
            Particle p1 = new Particle(1, 0, initial_vx,
                    0, 1, 1, mass);
            Particle p2 = new Particle(1, 0, initial_vx,
                    0, 1, 1, mass);
            Particle p3 = new Particle(1, 0, initial_vx,
                    0, 1, 1, mass);
            runBeeman(p1);
            runVerlet(p2);
            runGear(p3, osc.getK());
        }
    }

    public static void runBeeman(Particle p){
        Beeman beeman = new Beeman(delta_t);
        double t = 0;
        int ctr = 0;
        while(t < total_time){
            fileUtils.takePositionSnapshot(p, "beeman", delta_t);
            beeman.updateParams(p);
            t += delta_t;
            ctr++;
        }
    }

    public static void runVerlet(Particle p){
        double currR = p.getX();
        double currV = p.getVx();
        double mass = p.getMass();

        double prevR = calculateEulerR(currR, currV, -delta_t, mass, osc.calculateForce(currR, currV));

        double t = 0;

        double nextR;

        while (t < total_time) {
            fileUtils.takePositionSnapshot(currR, currV, "verlet", delta_t);
            nextR = 2 * currR - prevR + (Math.pow(delta_t, 2) * osc.calculateForce(currR, currV)) / mass;
            currV = (nextR - prevR) / (2 * delta_t);
            prevR = currR;
            currR = nextR;
            t += delta_t;
        }
    }

    public static void runGear(Particle p, double k){
        GearPredictorCorrector gear = new GearPredictorCorrector(delta_t);
        Double[] derivs = gear.calculateInitialDerivs(p, 5, k);
        double t = 0;
        while(t < total_time){
            fileUtils.takePositionSnapshot(p, "gear", delta_t);
            derivs = gear.gearPredictor(derivs, p);
            t += delta_t;
        }
    }
}
