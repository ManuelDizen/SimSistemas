package System1;

import IntegrationMethods.Beeman;
import IntegrationMethods.GPC2;
import IntegrationMethods.GearPredictorCorrector;
import IntegrationMethods.VerletOriginal;
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
            runBeeman(p1);
            runVerlet(p2);
            Particle p3 = new Particle(1, 0, initial_vx,
                    0, 1, 1, mass);
            runGear(p3, osc.getK());
        }
    }

    public static void runBeeman(Particle p){
        Beeman beeman = new Beeman(delta_t);
        double t = 0;
        while(t < total_time){
            fileUtils.takePositionSnapshot(p, "beeman", delta_t);
            beeman.updateParams(p);
            t += delta_t;
        }
    }

    public static void runVerlet(Particle p){
        VerletOriginal verlet = new VerletOriginal(delta_t, p.getX(), p.getVx(), p.getMass());
        double t = 0;
        while (t < total_time) {
            fileUtils.takePositionSnapshot(p, "verlet", delta_t);
            verlet.updateParams(p);
            t += delta_t;
        }
    }

    public static void runGear(Particle p, double k){
        System.out.println("Entro con delta_T=" + delta_t);
        /*GearPredictorCorrector gear = new GearPredictorCorrector(delta_t);
        gear.calculateInitialDerivs(p, 5, k);
        double t = 0;
        while(t < total_time){
            fileUtils.takePositionSnapshot(p, "gear", delta_t);
            gear.updateParams(p);
            t += delta_t;
        }*/
        p.setAx( ( (-k * p.getX()) - (osc.getGamma() * p.getVx() ) ) / p.getMass());
        GPC2 gear = new GPC2(p, delta_t);
        double t = 0;
        double MSE = 0;
        int ctr = 0;
        while(t < total_time){
            MSE += calculateSqDiff(p.getX(), getActualVal(t));
            fileUtils.takePositionSnapshot(p, "gear", delta_t);
            gear.updateParams(p);
            t += delta_t;
            ctr++;
        }
        System.out.println(String.format("MSE con delta_t %e, método gear: %e", delta_t, MSE/ctr));
    }

    public static double calculateSqDiff(double n1, double n2){
        return Math.pow(n1-n2, 2);
    }

    public static double getActualVal(double t){
        int gamma = osc.getGamma();
        int mass = osc.getMass();
        return amplitude * (Math.exp(- ((double) gamma /(2* mass) ) * t)) *
                (Math.cos(Math.pow((osc.getK()/mass) - ((double) (gamma * gamma) /
                        (4*(mass*mass))), 0.5) * t));
    }


}
