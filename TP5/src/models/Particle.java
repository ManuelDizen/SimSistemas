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


    public Particle(double x, double y, double vx, double vy, double radius) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.radius = radius;
        this.idx = count++;
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

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public String toString(){
        return "Particle " + idx + " (" + x + ", " + y + ")";
    }
}
