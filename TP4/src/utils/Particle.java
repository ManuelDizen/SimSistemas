package utils;

import IntegrationMethods.GearPredictorCorrector;
import System2.PoolTableRunnable;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static System2.PoolTableRunnable.k;

public class Particle {

    private double x;
    private double prev_x;
    private double y;
    private double prev_y;
    private double vx;
    private double prev_vx;
    private double vy;
    private double prev_vy;

    private double ax;
    private double prev_ax;
    private double ay;
    private double prev_ay;
    private int idx;

    private double radius;

    private double mass;
    private int collision_n = 0;

    private double[] force;

    public boolean PRECISION_TEST = false;
    public int scale = 3;

    private GearPredictorCorrector gear;

    private Double[] derivsX;
    private Double[] derivsY;

    public Particle(double x, double y, double vx, double vy, int idx, double radius, double mass) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.idx = idx;
        this.radius = radius;
        this.mass = mass;
        this.prev_x = 0.0;
        this.prev_vx = 0.0;
        this.prev_ax = 0.0;
        this.prev_y = 0.0;
        this.prev_vy = 0.0;
        this.prev_ay = 0.0;

        gear = new GearPredictorCorrector(Math.pow(10, PoolTableRunnable.delta_t));
        this.derivsX = gear.calculateInitialDerivsX(this, 5, k);
        this.derivsY = gear.calculateInitialDerivsY(this, 5, k);
    }

    public double getPrev_x() {
        return prev_x;
    }

    public void setPrev_x(double prev_x) {
        this.prev_x = prev_x;
    }

    public double getPrev_y() {
        return prev_y;
    }

    public void setPrev_y(double prev_y) {
        this.prev_y = prev_y;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

    public double getPrev_vx() {
        return prev_vx;
    }

    public void setPrev_vx(double prev_vx) {
        this.prev_vx = prev_vx;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public double getPrev_vy() {
        return prev_vy;
    }

    public void setPrev_vy(double prev_vy) {
        this.prev_vy = prev_vy;
    }

    public double getAx() {
        return ax;
    }

    public void setAx(double ax) {
        this.ax = ax;
    }

    public double getPrev_ax() {
        return prev_ax;
    }

    public void setPrev_ax(double prev_ax) {
        this.prev_ax = prev_ax;
    }

    public double getAy() {
        return ay;
    }

    public void setAy(double ay) {
        this.ay = ay;
    }

    public double getPrev_ay() {
        return prev_ay;
    }

    public void setPrev_ay(double prev_ay) {
        this.prev_ay = prev_ay;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setCollision_n(int collision_n) {
        this.collision_n = collision_n;
    }

    public double getVx() {
        return vx;
    }

    public double getVy() {
        return vy;
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

    public int getCollision_n() {
        return collision_n;
    }

    public void incCollision_n() {
        this.collision_n++;
    }

    public double getRadius() {
        return radius;
    }

    public Double[] getDerivsX() {
        return derivsX;
    }

    public Double[] getDerivsY() {
        return derivsY;
    }

    public void setDerivsX(Double[] derivsX) {
        this.derivsX = derivsX;
    }

    public void setDerivsY(Double[] derivsY) {
        this.derivsY = derivsY;
    }

    public double testing(double og){
        return PRECISION_TEST? BigDecimal.valueOf(og)
                .setScale(scale, RoundingMode.HALF_UP)
                .doubleValue() : og;
    }
    // Time calculation functions

    public double timeToXWallBounce(double x_wall_coor){
        if(this.vx > 0){
            return testing((x_wall_coor - this.radius - this.x)/this.vx);
        }
        else if(this.vx < 0){
            return testing((0 + this.radius - this.x)/this.vx);
        }
        else{
            // vx == 0
            return -1;
        }
    }

    public double timeToYWallBounce(double y_wall_coor){
        if(this.vy > 0){
            return testing((y_wall_coor - this.radius - this.y)/this.vy);
        }
        else if(this.vy < 0){
            return testing((0 + this.radius -this.y)/this.vy);
        }
        else{
            // vy == 0
            return -1;
        }
    }

    public double timeToParticleCollision(Particle other){
        double dvx = other.vx - this.vx;
        double dvy = other.vy - this.vy;
        double dx = other.x - this.x;
        double dy = other.y-this.y;
        double dvdr = (dvx*dx) + (dvy*dy);
        double dvdv = Math.pow(dvx, 2) + Math.pow(dvy, 2);
        double drdr = Math.pow(dx, 2) + Math.pow(dy,2);
        if(dvdr >= 0) return -1;
        double d = Math.pow(dvdr, 2) -
                (dvdv *(drdr - (Math.pow(this.radius + other.radius,2)) ));
        if(d < 0) return -1;
        return testing((dvdr + Math.pow(d, 0.5)) / (dvdv * -1));
    }


    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public double[] getForce() {
        return force;
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

    public double getNorm(Particle other){
        return Math.sqrt(Math.pow(other.getX() - this.getX(), 2) + Math.pow(other.getY() - this.getY(), 2));
        //TODO: chequear si esto está bien vengo para atrás con geometría
        // Escribi esto: √((xj - xi)^2 + (yj - yi)^2)
    }


    public void calculateForce(Particle other){
        double norm = getNorm(other);
        double[] normal = new double[2];
        normal[0] = (other.getX() - this.getX())/norm;
        normal[1] = (other.getY() - this.getY())/norm;

        double[] toRet = new double[2];
        double constant = k * (norm - (this.radius + other.radius));
        toRet[0]=constant*normal[0];
        toRet[1]=constant*normal[1];

        force = toRet;
    }

    public void applyUpdateNoBounce() {
        Double[][] results = gear.updateParamsNoCol(derivsX, derivsY);
        setX(results[0][0]);
        setY(results[1][0]);
        setVx(results[0][1]);
        setVy(results[1][1]);
        setDerivsX(results[0]);
        setDerivsY(results[1]);
    }

    public void applyBounceWithHorizontalWall() {
        setVx(-this.getVx());
    }

    //TODO: Revisar el choque con pared

    public void applyBounceWithVerticalWall() {
        setVy(-this.getVy());
    }
}
