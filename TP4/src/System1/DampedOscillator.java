package System1;

public class DampedOscillator {

    private int mass = 70; // kg
    private double k = Math.pow(10, 4); // N/m
    private int gamma = 100; // kg/s
    public double calculateForce(double pos, double vel){
        return (-k * pos) - (gamma * vel);
    }

    public int getMass() {
        return mass;
    }

    public double getK() {
        return k;
    }

    public int getGamma() {
        return gamma;
    }
}
