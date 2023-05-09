package IntegrationMethods;

import System1.DampedOscillator;
import utils.Particle;

public class GearPredictorCorrector implements IntegrationMethod{
    private final double delta_t;
    private final int deriv_n = 5;
    private final Double[] gpArray = new Double[deriv_n+1];
    private final DampedOscillator oscillator = new DampedOscillator();
    private Double[] derivs = new Double[deriv_n + 1];

    private final Double[] alphas_w_v =
            new Double[]{ (3 / 16.0), (251/360.0), 1.0, (11/18.0), (1/6.0), (1/60.0)};

    public GearPredictorCorrector(double delta_t){
        this.delta_t = delta_t;
        initializeGPArray();
    }

    private void initializeGPArray(){
        for(int i = 0; i < deriv_n + 1; i++)
            gpArray[i] = Math.pow(delta_t, i) / factorial(i);
    }
    public double factorial(int n){
        if(n == 0 || n == 1) return 1;
        return n * factorial(n-1);
    }

    public Double[] makePrediction(Double[] derivs){
        Double[] ret = derivs.clone();
        double aux;
        for(int i = 0; i < ret.length; i++){
            aux = 0;
            for(int j = 0; j < ret.length - i; j++) {
                aux += derivs[i + j] * gpArray[j];
            }
            ret[i] = aux;
        }
        return ret;
    }

    public Double evaluateAcceleration(Double r0, Double r1, Double r2, Particle p){
        double accel = oscillator.calculateForce(r0, r1) / p.getMass();
        return (accel - r2) * delta_t * delta_t / 2; // 2! = 2
    }

    public void calculateInitialDerivs(Particle p, int n, double k){
        Double[] ret = new Double[n+1];
        ret[0] = p.getX();
        ret[1] = p.getVx();
        double km = -k / p.getMass();
        ret[2] = km * p.getX();
        ret[3] = km * ret[1];
        ret[4] = km * km * p.getX();
        ret[5] = km * km * ret[1];
        derivs = ret;
    }

    public void updateParams(Particle p){
        Double[] preds = makePrediction(derivs);
        double deltaR2 = evaluateAcceleration(preds[0], preds[1], preds[2], p);

        correctPredictions(preds, deltaR2);
        derivs = preds;

        p.setX(preds[0]);
        p.setVx(preds[1]);
    }

    private void correctPredictions(Double[] preds, double deltaR2){
        for(int i = 0; i < preds.length; i++){
            preds[i] += alphas_w_v[i] * deltaR2 * factorial(i) / Math.pow(delta_t, i);
        }
    }

}
