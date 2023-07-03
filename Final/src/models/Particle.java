package models;

public class Particle {
    private static int count = 0;
    private int idx;

    private double x;
    private double y;
    private double vx;
    private double vy;
    private double target_x;
    private double target_y;

    private double radius;

    private double nextRadius;

    private double mass;

    private Double[] force;

    private Double[] derivsX;
    private Double[] derivsY;


    public Particle(double x, double y, double vx, double vy, double radius, double mass) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.radius = radius;
        this.mass = mass;
        this.idx = count++;
        this.force = new Double[]{0.0, 0.0};
    }

    public Particle(double x, double y, double vx, double vy, double radius) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.radius = radius;
        this.idx = count++;
        this.force = new Double[]{0.0, 0.0};
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getVx() {
        return vx;
    }

    public double getVy() {
        return vy;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public double getTarget_x() {
        return target_x;
    }

    public void setTarget_x(double target_x) {
        this.target_x = target_x;
    }

    public double getTarget_y() {
        return target_y;
    }

    public void setTarget_y(double target_y) {
        this.target_y = target_y;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getMass() {
        return mass;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public String toString() {
        return "Particle " + idx + " (" + x + ", " + y + ")";
    }

    public double getNextRadius() {
        return nextRadius;
    }

    public void setNextRadius(double nextRadius) {
        this.nextRadius = nextRadius;
    }

    public Double[] getForce() {
        return force;
    }

    private void setForce(Double[] force) {
        this.force = force;
    }

    public void resetForce() { //resetear fuerza a 0
        setForce(new Double[]{0.0, 0.0});
    }

    public void accumForce(Double[] f) {
        setForce(new Double[]{f[0] + getForce()[0], f[1] + getForce()[1]});
    }

    /*---------------------------------GEAR PREDICTOR-CORRECTOR--------------------------------*/

    public Double[] getDerivsX() {
        return derivsX;
    }

    public void setDerivsX(Double[] derivsX) {
        this.derivsX = derivsX;
    }

    public Double[] getDerivsY() {
        return derivsY;
    }

    public void setDerivsY(Double[] derivsY) {
        this.derivsY = derivsY;
    }

    public void setParameters() {
        setX(derivsX[0]);
        setY(derivsY[0]);
        setVx(derivsX[1]);
        setVy(derivsY[1]);
    }


}