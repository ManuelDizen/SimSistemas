package System1;

import IntegrationMethods.Beeman;
import IntegrationMethods.GearPredictorCorrector;
import IntegrationMethods.VerletOriginal;
import utils.FileUtils;
import utils.Pair;
import utils.Particle;

import java.util.ArrayList;
import java.util.List;

public class OscillatorRunnable {

    private static final double amplitude = 1.0;
    private static final double delta_t = Math.pow(10, -2);
    private static final double total_time = 5; // s
    private static final FileUtils fileUtils = new FileUtils();
    private static final DampedOscillator osc = new DampedOscillator();
    public static void main(String[] args) {
        int mass = osc.getMass();
        double initial_vx = (-amplitude * osc.getGamma())/(2.0*mass);
        Particle p1 = new Particle(1, 0, initial_vx,
                0, 1, 1, mass, 1);
        Particle p2 = new Particle(1, 0, initial_vx,
                0, 1, 1, mass, 1);
        Particle p3 = new Particle(1, 0, initial_vx,
                0, 1, 1, mass, 1);
        runGear2(p3, osc.getK());
        runBeeman(p1);
        testVerlet(p2);
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
        System.out.println("ctr:" + ctr);
    }

    public static void runVerlet(Particle p){
        VerletOriginal verlet = new VerletOriginal(delta_t);
        verlet.startParameters(p);
        double t = 0;
        while(t < total_time){
            fileUtils.takePositionSnapshot(p, "verlet", delta_t);
            verlet.updateParams(p);
            t += delta_t;
        }
    }

    public static void testVerlet(Particle p){
        double currR = p.getX();
        double currV = p.getVx();
        double mass = p.getMass();

        double prevR = eulerR(currR, currV, -delta_t, mass, osc.calculateForce(currR, currV));
        double t = 0;
        double nextR;

        fileUtils.takePositionSnapshot(p, "verlet", delta_t);
        while (t < 5) {
            nextR = VerletOriginal.updateVerletX(currR, prevR, delta_t, mass, osc.calculateForce(currR, currV));
            currV = (nextR - prevR) / (2 * delta_t);
            prevR = currR;
            currR = nextR;
            t += delta_t;
            fileUtils.takePositionSnapshot(p, "verlet", delta_t);
        }
    }

    public static double eulerR(double r, double v, double step, double mass, double f) {
        return r + step * v + step * step * f / (2 * mass);
    }

    public static void runGear(Particle p, double k){
        GearPredictorCorrector gear = new GearPredictorCorrector(delta_t);
        gear.calculateInitial(p, k);
        double t = 0;
        while(t < total_time){
            fileUtils.takePositionSnapshot(p, "gear", delta_t);
            gear.updateParams(p);
            t += delta_t;
        }
    }

    public static void runGear2(Particle p, double k){
        GearPredictorCorrector gear = new GearPredictorCorrector(delta_t);
        Double[] prediction = gear.calculateInitialDerivs(p, 5, k);
        double t = 0;
        while(t < total_time){
            fileUtils.takePositionSnapshot(p, "gear", delta_t);
            prediction = gear.gearPredictor(prediction, p);
            p.setX(prediction[0]);
            p.setVx((prediction[1]));
            t += delta_t;
        }
    }
}
