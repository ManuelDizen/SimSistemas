package models;

import java.util.ArrayList;
import java.util.List;

public class Particle {

    private double x;
    private double y;
    private double angle;
    private final List<Particle> neighbours;
    private double eta;

    public void setEta(double eta){
        this.eta = eta;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    private int idx;

    public Particle(int idx, double angle){
        this.idx = idx;
        this.angle = angle;
        this.neighbours = new ArrayList<>();
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

    public double getAngle() {
        return angle;
    }

    public  void setAngle(double angle) {
        this.angle = angle;
    }

    public List<Particle> getNeighbours() {
        return neighbours;
    }

    public void removeNeighbours(){
        this.neighbours.clear();
    }

    public void addNeighbour(Particle p) {
        this.neighbours.add(p);
    }

    public void addNeighbours(List<Particle> pList) {
        this.neighbours.addAll(pList);
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

    public void updateAngle(){
        //This method assumes the neighbours have already been correctly updated
        // "Promedio de todos los ángulos de todas las particulas dentro de r incluyendo a la propia"
        double totalSin = 0;
        double totalCos = 0;
        totalSin += Math.sin(this.getAngle());
        totalCos += Math.cos(this.getAngle());
        for(Particle p : neighbours){
            totalSin += Math.sin(p.getAngle());
            totalCos += Math.cos(p.getAngle());
        }
        double avgSin = totalSin / (neighbours.size() + 1);
        double avgCos = totalCos / (neighbours.size() + 1);
        double val = Math.atan2(avgSin, avgCos);
        double randomFactor = ((eta/2)*(Math.random())*(Math.random() <= 0.5? -1:1));
        val += randomFactor;
        this.angle = val;
    }


}
