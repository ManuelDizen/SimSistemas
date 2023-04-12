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
    private int collision_n = 0; // Sugerencia de bibliografía para saber si una colisión calculada
                             // sigue siendo válida

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

    public int getCollision_n() {
        return collision_n;
    }

    public void incCollision_n() {
        this.collision_n++;
    }

    /*
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
*/
    // Time calculation functions

    public double timeToXWallBounce(double x_wall_coor){
        if(this.vx > 0){
            return (x_wall_coor - this.radius - this.x)/this.vx;
        }
        else if(this.vx < 0){
            return (0 + this.radius - this.x)/this.vx;
        }
        else{
            // vx == 0
            return -1;
        }
    }

    public double timeToYWallBounce(double y_wall_coor){
        if(this.vy > 0){
            return (y_wall_coor - this.radius - this.y)/this.vy;
        }
        else if(this.vy < 0){
            return (0 + this.radius -this.y)/this.vy;
        }
        else{
            // vy == 0
            return -1;
        }
    }

    public double timeToParticleCollision(Particle other){
        double dvx = other.vx - this.vx;
        double dvy = other.vy-this.vy;
        double dx = other.x - this.x;
        double dy = other.y-this.y;
        double dvdr = (dvx*dx) + (dvy*dy);
        double dvdv = Math.pow(dvx, 2) + Math.pow(dvy, 2);
        double drdr = Math.pow(dx, 2) + Math.pow(dy,2);
        if(dvdr >= 0) return -1;
        double d = Math.pow(dvdr, 2) -
                (dvdv *(drdr - (Math.pow(this.radius + other.radius,2))));
        if(d < 0) return -1;
        return (dvdr + Math.pow(d, 0.5)) / (dvdv * -1);
    }


    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public void bounceWithVerticalWall(){
        this.vx = -this.vx;
        this.collision_n++;
    }

    public void bounceWithHorizontalWall(){
        this.vy = -this.vy;
        this.collision_n++;
    }

    public void bounceWithParticle(Particle other){
        double dvx = other.vx - this.vx;
        double dvy = other.vy-this.vy;
        double dx = other.x - this.x;
        double dy = other.y-this.y;
        double dvdr = (dvx*dx) + (dvy*dy);
        double sigma = this.radius + other.radius;
        double J = (2*this.mass*other.mass*(dvdr))/(sigma* (this.mass + other.mass));
        double Jx = (J*dx)/sigma;
        double Jy = (J*dy)/sigma;
        this.vx = this.vx + (Jx/this.mass); // vxi
        this.vy = this.vy + (Jy/this.mass); // vyi
        other.vx = other.vx - (Jx/other.mass);
        other.vy = other.vy - (Jy/other.mass);

        this.collision_n++;
    }
}
