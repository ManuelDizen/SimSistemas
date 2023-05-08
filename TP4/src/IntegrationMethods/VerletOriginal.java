package IntegrationMethods;

import System1.DampedOscillator;
import utils.Pair;
import utils.Particle;

import static IntegrationMethods.Euler.calculateEulerR;

public class VerletOriginal implements IntegrationMethod {
    private final double delta_t;

    private double curr_vx;
    private double prev_r;
    private double curr_r;
    private double next_r;
    private double mass;
    private final DampedOscillator oscillator = new DampedOscillator();

    public VerletOriginal(double delta_t, double curr_r, double curr_vx, double mass){
        this.delta_t = delta_t;
        this.curr_r = curr_r;
        this.curr_vx = curr_vx;
        this.mass = mass;
        this.prev_r = calculateEulerR(curr_r, curr_vx, -delta_t, mass, oscillator.calculateForce(curr_r, curr_vx));
    }

    @Override
    public void updateParams(Particle p){
        next_r = 2 * curr_r - prev_r + (Math.pow(delta_t, 2) * oscillator.calculateForce(curr_r, curr_vx)) / mass;
        curr_vx = (next_r - prev_r) / (2 * delta_t);
        prev_r = curr_r;
        curr_r = next_r;

        p.setX(curr_r);
        p.setVx(curr_vx);
    }
}
