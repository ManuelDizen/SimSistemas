package utils;

import IntegrationMethods.GearPredictorCorrector;
import System2.PoolTable;
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

    private Double[] force;

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
        this.force = new Double[]{0.0, 0.0};

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

    /*-----------------------------------------FUNCIONES TP4-----------------------------------------------*/

    public void setDerivsX(Double[] derivsX) {
        this.derivsX = derivsX;
    }

    public void setDerivsY(Double[] derivsY) {
        this.derivsY = derivsY;
    }
    public double getNorm(double X, double Y, double otherX, double otherY){
        return Math.sqrt(Math.pow(otherX - X, 2) + Math.pow(otherY - Y, 2));
    }


    private Double[] calculateForce(double X, double Y, double otherX, double otherY){

        double norm = getNorm(X, Y, otherX, otherY);

        double[] normal = new double[2];
        normal[0] = (otherX - X)/norm;
        normal[1] = (otherY - Y)/norm;

        Double[] toRet = new Double[2];
        double constant = k * (norm - (this.radius*2));

        toRet[0]=constant*normal[0];
        toRet[1]=constant*normal[1];

        return toRet;
    }

    private Double[][] getPredictions() { //tomo las predictions del gear sin que impacten en el estado del sistema
        return gear.getPredictions(derivsX, derivsY);
    }

    //calculo fuerza en función de variables predichas
    public Double[] predictForce(double otherX, double otherY) {
        Double[][] preds = getPredictions();
        return calculateForce(preds[0][0], preds[1][0], otherX, otherY);
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
    public boolean hasAccumForce() {
        return ((getForce()[0] != 0) || (getForce()[1] != 0));
    }

    public Double[] getDerivsX() {
        return derivsX;
    }

    public Double[] getDerivsY() {
        return derivsY;
    }

    public void accumForce(Double[] f) {
        setForce(new Double[]{f[0] + getForce()[0], f[1] + getForce()[1]});
    }

    private void setParameters() {
        setX(derivsX[0]);
        setY(derivsY[0]);
        setVx(derivsX[1]);
        setVy(derivsY[1]);
    }

    public void applyUpdate(Double[] f) {
        Double[][] preds = getPredictions(); //tomo predicciones del gear
        Double[] acceleration = {f[0]/getMass(), f[1]/getMass()}; //aceleración calculada con fórmula
        Double[] deltaR2 = {gear.calculateDeltaR2(acceleration[0], preds[0][2]), gear.calculateDeltaR2(acceleration[1], preds[1][2])};
        setDerivsX(gear.correctPredictions(preds[0], deltaR2[0])); //corrijo y seteo las derivs para la próxima iteración
        setDerivsY(gear.correctPredictions(preds[1], deltaR2[1]));
        setParameters();
    }

    public void applyBounceWithHorizontalWall() {
        Double[] derivs = derivsY;
        derivs[1] = -derivsY[1];
        setDerivsY(derivs);
        applyUpdate(new Double[]{0.0, 0.0});
    }

    //TODO: Revisar el choque con pared

    public void applyBounceWithVerticalWall() {
        Double[] derivs = derivsX;
        derivs[1] = -derivsX[1];
        setDerivsX(derivs);
        applyUpdate(new Double[]{0.0, 0.0});
    }

    public Double[] applyBounceWithVerticalWall2(){
        boolean right = this.getX() + this.getRadius() >= System2.PoolTable.LONG_SIDE;
        double springConstant = 100;
        double dist;
        if(right){
            dist = Math.abs(this.getX() + this.getRadius() - PoolTable.LONG_SIDE);
        }
        else{
            dist = Math.abs(this.getX() - this.getRadius() - 0);
        }
        double force = springConstant*dist*(right?-1:1);
        return new Double[]{force, 0.0};
    }

    public Double[] applyBounceWithHorizontalWall2(){
        boolean upper = this.getY() + this.getRadius() >= System2.PoolTable.SHORT_SIDE;
        double springConstant = 1000;
        double dist;
        if(upper){
            dist = Math.abs(this.getY() + this.getRadius() - PoolTable.SHORT_SIDE);
        }
        else{
            dist = Math.abs(this.getY() - this.getRadius() - 0);
        }
        double force = springConstant*dist*(upper?-1:1);
        return new Double[]{0.0, force};
    }

}
