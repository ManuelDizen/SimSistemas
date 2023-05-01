package System1;

public class DampedOscillator {

    private int mass = 70; // kg
    private double k = Math.pow(10, 4); // N/m
    private int gamma = 100; // kg/s
    private double total_time = 5; // s
    public double calculateForce(double pos, double vel){
        return (-k * pos) - (gamma * vel);
    }

    public int getMass() {
        return mass;
    }

    public void setMass(int mass) {
        this.mass = mass;
    }

    public double getK() {
        return k;
    }

    public void setK(double k) {
        this.k = k;
    }

    public int getGamma() {
        return gamma;
    }

    public void setGamma(int gamma) {
        this.gamma = gamma;
    }

    public double getTotal_time() {
        return total_time;
    }

    public void setTotal_time(double total_time) {
        this.total_time = total_time;
    }
}
