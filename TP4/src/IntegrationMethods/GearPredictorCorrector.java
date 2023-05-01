package IntegrationMethods;

import System1.DampedOscillator;
import utils.Pair;
import utils.Particle;

import java.util.ArrayList;
import java.util.List;

/*
TODO: Me falta en todos los métodos pasar bien a archivos para hacer gráficos
 */
public class GearPredictorCorrector implements IntegrationMethod{
    private final double delta_t;
    private final int deriv_n = 5;
    private Double[] gpArray = new Double[deriv_n+1];
    private final DampedOscillator oscillator = new DampedOscillator();

    private final Double[] alphas_w_v =
            new Double[]{ (3 / 16.0), (251/360.0), 1.0, (11/18.0), (1/6.0), (1/60.0)};

    private List<Double> derivatives = new ArrayList<>(deriv_n + 1);

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
    public void calculateInitial(Particle p, double k){
        double x = p.getX();
        double vx = p.getVx();
        derivatives.add(0, x);
        derivatives.add(1, vx);
        double km = -k / p.getMass();
        derivatives.add(2, km * x);
        derivatives.add(3, km * derivatives.get(1));
        derivatives.add(4, km * derivatives.get(2));
        derivatives.add(5, km * derivatives.get(3));
    }

    public List<Double> predict(){
        List<Double> predictions = new ArrayList<>(derivatives);
        System.out.println(predictions);
        double sum = 0;
        for(int i = 0; i < predictions.size(); i++){
            sum = 0;
            for(int j = 0; j < predictions.size() - i; j++){
                sum += derivatives.get(i+j) * gpArray[j];
            }
            predictions.set(i, sum);
        }
        return predictions;
    }

    @Override
    public void updateParams(Particle p) {

        List<Double> preds = predict();
        Double delta_r2 = getR2value(preds.get(0), preds.get(1), preds.get(2), p.getMass());
        System.out.println("X antes: " + preds.get(0));
        for(int i = 0; i < preds.size(); i++){
            preds.set(i, preds.get(i) + ((alphas_w_v[i]*delta_r2*factorial(i)) / Math.pow(delta_t, i)));
        }
        System.out.println("X después: " + preds.get(0));
        p.setPrev_x(p.getX());
        p.setPrev_vx(p.getVx());
        p.setPrev_ax(p.getAx());
        p.setX(preds.get(0));
        p.setVx(preds.get(1));
    }

    private Double getR2value(Double x, Double vx, Double ax, Double mass){
        double next_ax = oscillator.calculateForce(x, vx) / mass;
        return ((next_ax - ax) * Math.pow(delta_t, 2))/2;
    }


    public Double[] makePrediction(Double[] derivs){
        Double[] ret = derivs.clone();
        double aux;
        for(int i = 0; i < ret.length; i++){
            aux = 0;
            for(int j = 0; j < ret.length - i; j++)
                aux += derivs[i + j] * gpArray[j];
            ret[i] = aux;
        }
        return ret;
    }

    public Double evaluateAcceleration(Double r0, Double r1, Double r2, Particle p){
        double accel = oscillator.calculateForce(r0, r1) / p.getMass();
        return (accel - r2) * delta_t * delta_t / 2;
    }

    public Double[] calculateInitialDerivs(Particle p, int n, double k){
        Double[] ret = new Double[n+1];
        ret[0] = p.getX();
        ret[1] = p.getVx();
        double km = -k / p.getMass();
        ret[2] = km * p.getX();
        ret[3] = km * ret[1];
        ret[4] = km * km * p.getX();
        ret[5] = km * km * ret[1];
        return ret;
    }

    public Double[] gearPredictor(Double[] derivs, Particle p){
        Double[] predictions = makePrediction(derivs);
        double deltaR2 = evaluateAcceleration(predictions[0], predictions[1], predictions[2], p);
        for(int i = 0; i < predictions.length; i++){
            predictions[i] += alphas_w_v[i] * deltaR2 * factorial(i) / Math.pow(delta_t, i);
        }
        return predictions;
    }

}
