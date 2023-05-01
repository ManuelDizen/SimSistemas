package IntegrationMethods;

import System1.DampedOscillator;
import utils.Pair;
import utils.Particle;

import static IntegrationMethods.Euler.calculateEulerR;

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
    public void updateParams(Particle p) {

    }

    /*@Override
    public void updateParams(Particle p){
        next_x =
                2*curr_x - prev_x +
                        ((Math.pow(delta_t, 2)/p.getMass()) * oscillator.calculateForce(curr_x, curr_vx));
        curr_vx = (next_x + prev_x)/(2*delta_t);
        prev_x = curr_x;
        curr_x = next_x;
        p.setX(curr_x);
        p.setVx(curr_vx);
    }*/
}
