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

    public Double[] makePrediction(Double[] derivs, boolean mruv){
        Double[] ret = derivs.clone();
        double aux;
        int limit;
        if(mruv)
            limit = 3;
        else
            limit = ret.length;
        for(int i = 0; i < limit; i++){
            if(i==3) {
                System.out.println("cálculo deriv3: ");
            }
            aux = 0;
            for(int j = 0; j < limit - i; j++) {
                aux += derivs[i + j] * gpArray[j];
                if(i==3)
                    System.out.println(derivs[i + j] * gpArray[j] + ", aux: " + aux);
            }
            ret[i] = aux;
        }
        return ret;
    }

    public Double evaluateAcceleration(Double r0, Double r1, Double r2, Particle p){
        double accel = oscillator.calculateForce(r0, r1) / p.getMass();
        return calculateDeltaR2(accel, r2);
    }

    public Double calculateDeltaR2(double accel, double r2) {
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

    public Double[] calculateInitialDerivsX(Particle p, int n, double k){
        Double[] ret = new Double[n+1];
        ret[0] = p.getX();
        ret[1] = p.getVx();
        ret[2] = ret[3] = ret[4] = ret[5] = 0.0;
        return ret;
    }

    public Double[] calculateInitialDerivsY(Particle p, int n, double k){
        Double[] ret = new Double[n+1];
        ret[0] = p.getY();
        ret[1] = p.getVy();
        ret[2] = ret[3] = ret[4] = ret[5] = 0.0;
        return ret;
    }

    public void updateParams(Particle p){
        Double[] preds = makePrediction(derivs, false);
        double deltaR2 = evaluateAcceleration(preds[0], preds[1], preds[2], p);

        derivs = correctPredictions(preds, deltaR2);

        p.setX(preds[0]);
        p.setVx(preds[1]);
    }

    public Double[][] getPredictions(Double[] dX, Double[] dY) {
        Double[] predsX = makePrediction(dX, false);
        Double[] predsY = makePrediction(dY, false);

        //al no haber cambios en la aceleración, no hay que corregir
        Double[][] ret = {predsX, predsY};
        return ret;
    }

    public Double[][] getPredictionsMruv(Double[] dX, Double[] dY) {
        Double[] predsX = makePrediction(dX, true);
        Double[] predsY = makePrediction(dY, true);

        //al no haber cambios en la aceleración, no hay que corregir
        Double[][] ret = {predsX, predsY};
        return ret;
    }



    public Double[] correctPredictions(Double[] preds, double deltaR2){
        for(int i = 0; i < preds.length; i++){
            preds[i] += alphas_w_v[i] * deltaR2 * factorial(i) / Math.pow(delta_t, i);
        }
        return preds;
    }

}
