package models;

import java.util.ArrayList;
import java.util.List;

public class Particle {

    private double x;
    private double y;
    private double vx;
    private double vy;
    private double speed;
    private double angle;
    //private final List<Particle> neighbours;
    private double eta;
    private int idx;

    private double radius;

    private double mass;

    public Particle(double x, double y, double vx, double vy, int idx, double radius, double mass) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.idx = idx;
        this.radius = radius;
        this.mass = mass;
    }

    public double getVx() {
        return vx;
    }

    public double getVy() {
        return vy;
    }

    public double getSpeed() {
        return speed;
    }

    public int getIdx() {
        return idx;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public  void setY(double y) {
        this.y = y;
    }

    public void setEta(double eta) { this.eta = eta; }

    public double getAngle() {
        return angle;
    }


    public boolean isNeighbour(Particle p, double r_c){
        return Math.sqrt(Math.pow(this.getX() - p.getX(), 2) +
                Math.pow(this.getY() - p.getY(), 2))
                < r_c;
    }

    public boolean isPeriodicNeighbour(Particle p, double r_c, double L){
        double dx = Math.abs(this.getX() - p.getX());
        if (dx > L / 2)
            dx = L - dx;

        double dy = Math.abs(this.getY() - p.getY());
        if (dy > L / 2)
            dy = L - dy;

        return Math.sqrt(Math.pow(dx,2) + Math.pow(dy,2))
                < r_c;
    }


    public void updatePosition(double v, double L){
        double dx = v * Math.cos(this.angle);
        double dy = v * Math.sin(this.angle);
        this.vx = dx;
        this.vy = dy;
        this.x = (L+this.x + dx)%L;
        this.y = (L+this.y + dy)%L;
    }
}
