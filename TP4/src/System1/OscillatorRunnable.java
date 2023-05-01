package System1;

import IntegrationMethods.Beeman;
import IntegrationMethods.GearPredictorCorrector;
import IntegrationMethods.VerletOriginal;
import utils.FileUtils;
import utils.Pair;
import utils.Particle;

public class OscillatorRunnable {

    private static final double amplitude = 1.0;
    private static final double delta_t = Math.pow(10, -2);
    private static final double total_time = 5; // s
    private static final FileUtils fileUtils = new FileUtils();
    public static void main(String[] args) {
        DampedOscillator osc = new DampedOscillator();
        int mass = osc.getMass();
        double initial_vx = (-amplitude * osc.getGamma())/(2.0*mass);
        Particle p1 = new Particle(1, 0, initial_vx,
                0, 1, 1, mass, 1);
        Particle p2 = new Particle(1, 0, initial_vx,
                0, 1, 1, mass, 1);
        Particle p3 = new Particle(1, 0, initial_vx,
                0, 1, 1, mass, 1);
        runGear(p3, osc.getK());
        runBeeman(p1);
        runVerlet(p2);
    }

    public static void runBeeman(Particle p){
        Beeman beeman = new Beeman(delta_t);
        for(int i = 0; i < total_time/delta_t; i++){
            fileUtils.takePositionSnapshot(p, "beeman");
            beeman.updateParams(p);
        }
    }

    public static void runVerlet(Particle p){
        VerletOriginal verlet = new VerletOriginal(delta_t);
        for(int i = 0; i < total_time/delta_t; i++){
            fileUtils.takePositionSnapshot(p, "verlet");
            verlet.updateParams(p);
        }
    }

    public static void runGear(Particle p, double k){
        GearPredictorCorrector gear = new GearPredictorCorrector(delta_t);
        gear.calculateInitial(p, k);
        for(int i = 0; i < total_time / delta_t; i++){
            fileUtils.takePositionSnapshot(p, "gear");
            gear.updateParams(p);
        }
    }
}
