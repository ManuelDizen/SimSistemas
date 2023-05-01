package IntegrationMethods;

import System1.DampedOscillator;
import utils.Pair;
import utils.Particle;

public class Beeman implements IntegrationMethod{
    private final double delta_t;
    private final DampedOscillator oscillator = new DampedOscillator();

    public Beeman(double delta_t){
        this.delta_t = delta_t;
    }

    public Pair<Double> updateParams(Particle p){
        double curr_x = p.getX();
        double curr_vx = p.getVx();
        double mass = p.getMass();

        double curr_acc = oscillator.calculateForce(curr_x, curr_vx) / mass;
        double prev_acc = oscillator.calculateForce(p.getPrev_x(), p.getPrev_vx()) / mass;

        double next_x = curr_x + (curr_vx * delta_t) +
                ((2/3.0) * curr_acc * Math.pow(delta_t, 2)) -
                ((1/6.0) * prev_acc * Math.pow(delta_t, 2));

        double next_vx_pred = curr_vx + ((3/2.0) * curr_acc * delta_t) -
                ((1/2.0) * prev_acc * delta_t);

        double next_acc = oscillator.calculateForce(next_x, next_vx_pred) / mass;
        double next_vx_corrected = curr_vx + ((1/3.0) * next_acc * delta_t)
                + ((5/6.0) * curr_acc * delta_t) - ((1/6.0) * prev_acc * delta_t);

        p.setPrev_x(curr_x);
        p.setPrev_vx(curr_vx);
        p.setX(next_x);
        p.setVx(next_vx_corrected);

        return new Pair<>(next_x, next_vx_corrected);

    }
}
