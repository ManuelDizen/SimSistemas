package IntegrationMethods;

import System1.DampedOscillator;
import utils.Pair;
import utils.Particle;

public class VerletOriginal implements IntegrationMethod
{
    private final double delta_t;
    private double next_x;

    private double curr_x;

    private double prev_x;

    private double curr_vx;
    private final DampedOscillator oscillator = new DampedOscillator();

    public VerletOriginal(double delta_t){this.delta_t = delta_t;}

    @Override
    public void updateParams(Particle p){
        next_x =
                2*curr_x - prev_x +
                        ((Math.pow(delta_t, 2)/p.getMass()) * oscillator.calculateForce(curr_x, curr_vx));
        curr_vx = (next_x + prev_x)/(2*delta_t);
        prev_x = curr_x;
        curr_x = next_x;
        p.setX(curr_x);
        p.setVx(curr_vx);
    }

    public void startParameters(Particle p){
        double initial_f = oscillator.calculateForce(p.getX(), p.getVx());
        prev_x = euler(p, delta_t, initial_f);
        curr_x = p.getX();
        next_x = 0;
        curr_vx = p.getVx();
    }

    public double euler(Particle p, double delta_t, double force){
        return p.getX() + delta_t*p.getVx() + delta_t * delta_t * (force / (2 * p.getMass()));
    }

    public static double updateVerletX(double currR, double r, double step, double mass, double f) {
        return 2 * currR - r + (Math.pow(step, 2) * f) / mass;
    }
}
