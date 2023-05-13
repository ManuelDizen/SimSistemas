package IntegrationMethods;

import System1.DampedOscillator;
import utils.Particle;

public class GPC2 implements IntegrationMethod{
    private double r3;
    private double r4;
    private double r5;

    private double rp0;
    private double rp1;
    private double rp2;
    private double rp3;
    private double rp4;
    private double rp5;

    private final double alpha0 = 3/16.0;
    private final double alpha1 = 251/360.0;
    private final double alpha2 = 1;
    private final double alpha3 = 11/18.0;
    private final double alpha4 = 1/6.0;
    private final double alpha5 = 1/60.0;
    private final DampedOscillator oscillator = new DampedOscillator();
    private final double delta_t;


    public GPC2(Particle p, double delta_t){
        this.r3 = (- oscillator.getK() * p.getVx() - oscillator.getGamma() * p.getAx()) / p.getMass();
        this.r4 = (- oscillator.getK() * p.getAx() - oscillator.getGamma() * r3) / p.getMass();
        this.r5 = (- oscillator.getK() * r3 - oscillator.getGamma() * r4) / p.getMass();
        this.rp0 = 0;
        this.rp1 = 0;
        this.rp2 = 0;
        this.rp3 = 0;
        this.rp4 = 0;
        this.rp5 = 0;
        this.delta_t = delta_t;
    }

    //factorial function
    private int factorial(int n){
        if(n == 0){
            return 1;
        }
        return n * factorial(n-1);
    }

    public void updateParams(Particle p) {
        //GearPredictorCorrector's algorithm
        rp0 = p.getX() + p.getVx() * delta_t
                + (1.0/2.0) * Math.pow(delta_t, 2) * p.getAx()
                + (1.0/6.0) * Math.pow(delta_t, 3) * r3
                + (1.0/24.0) * Math.pow(delta_t, 4) * r4
                + (1.0/120.0) * Math.pow(delta_t, 5) * r5;
        rp1 = p.getVx()
                + p.getAx() * delta_t
                + (1.0/2.0) * Math.pow(delta_t, 2) * r3
                + (1.0/6.0) * Math.pow(delta_t, 3) * r4
                + (1.0/24.0) * Math.pow(delta_t, 4) * r5;
        rp2 = p.getAx()
                + r3 * delta_t
                + (1.0/2.0) * Math.pow(delta_t, 2) * r4
                + (1.0/6.0) * Math.pow(delta_t, 3) * r5;
        rp3 = r3
                + r4 * delta_t
                + (1.0/2.0) * Math.pow(delta_t, 2) * r5;
        rp4 = r4 + r5 * delta_t;
        rp5 = r5;

        double a = (- oscillator.getK() * rp0 - oscillator.getGamma() * rp1)/ oscillator.getMass();
        double dR2 = ( a - rp2 ) * Math.pow(delta_t, 2) / 2;

        rp0 = rp0 + dR2 * alpha0 * factorial(0) / Math.pow(delta_t, 0);
        rp1 = rp1 + dR2 * alpha1 * factorial(1) / Math.pow(delta_t, 1);
        rp2 = rp2 + dR2 * alpha2 * factorial(2) / Math.pow(delta_t, 2);
        rp3 = rp3 + dR2 * alpha3 * factorial(3) / Math.pow(delta_t, 3);
        rp4 = rp4 + dR2 * alpha4 * factorial(4) / Math.pow(delta_t, 4);
        rp5 = rp5 + dR2 * alpha5 * factorial(5) / Math.pow(delta_t, 5);

        p.setX(rp0);
        p.setVx(rp1);
        p.setAx(rp2);
        r3 = rp3;
        r4 = rp4;
        r5 = rp5;

    }
}
