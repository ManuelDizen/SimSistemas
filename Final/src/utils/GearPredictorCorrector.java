package utils;

import models.Particle;

public class GearPredictorCorrector {
    private final double delta_t;
    private final int deriv_n = 5;
    private final Double[] gpArray = new Double[deriv_n+1];


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


    public void setInitialDerivs(Particle p) {
        p.setDerivsX(calculateInitialDerivsX(p));
        p.setDerivsY(calculateInitialDerivsY(p));
    }

    //calculate initial derivs para la mesa de pool, en ejes x e y
    private Double[] calculateInitialDerivsX(Particle p){
        Double[] ret = new Double[deriv_n+1];
        ret[0] = p.getX();
        ret[1] = p.getVx();
        ret[2] = ret[3] = ret[4] = ret[5] = 0.0;
        return ret;
    }

    private Double[] calculateInitialDerivsY(Particle p){
        Double[] ret = new Double[deriv_n+1];
        ret[0] = p.getY();
        ret[1] = p.getVy();
        ret[2] = ret[3] = ret[4] = ret[5] = 0.0;
        return ret;
    }

    public Double[][] getPredictions(Double[] dX, Double[] dY) {
        Double[] predsX = makePrediction(dX);
        Double[] predsY = makePrediction(dY);

        //al no haber cambios en la aceleración, no hay que corregir
        return new Double[][]{predsX, predsY};
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

    public Double calculateDeltaR2(double accel, double r2) {
        return (accel - r2) * delta_t * delta_t / 2; // 2! = 2
    }


    public Double[] correctPredictions(Double[] preds, double deltaR2){
        for(int i = 0; i < preds.length; i++){
            preds[i] += alphas_w_v[i] * deltaR2 * factorial(i) / Math.pow(delta_t, i);
        }
        return preds;
    }

}
