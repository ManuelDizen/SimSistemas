package IntegrationMethods;

import utils.Pair;
import utils.Particle;

import java.util.ArrayList;
import java.util.List;

public class GearPredictorCorrector implements IntegrationMethod{
    private final double delta_t;
    private final int deriv_n = 5;
    private final List<Double> derivatives = new ArrayList<>(deriv_n + 1);

    public GearPredictorCorrector(double delta_t){
        this.delta_t = delta_t;
    }

    public void calculateInitial(Particle p, double k){
        double x = p.getX();
        double vx = p.getVx();
        derivatives.set(0, x);
        derivatives.set(1, vx);
        double km = -k / p.getMass();
        derivatives.set(2, km * x);
        derivatives.set(3, km * derivatives.get(1));
        derivatives.set(4, km * derivatives.get(2));
        derivatives.set(5, km * derivatives.get(3));
    }

    @Override
    public Pair<Double> updateParams(Particle p) {
        double d = delta_t;
        return null;
    }
}
