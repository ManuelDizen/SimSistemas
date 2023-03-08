import java.util.ArrayList;
import java.util.List;

public class Particle {
    private double x;
    private double y;
    private double r;
    private final List<Particle> neighbours;

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    private int idx;

    public Particle(int idx, double x, double y, double r){
        this.idx = idx;
        this.x = x;
        this.y = y;
        this.r = r;
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

    public double getR() {
        return r;
    }

    public  void setR(double r) {
        this.r = r;
    }

    public List<Particle> getNeighbours() {
        return neighbours;
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
                < (this.getR() +  p.getR() + r_c);
    }

    public boolean isPeriodicNeighbour(Particle p, double r_c, double L){
        double dx = Math.abs(this.x - p.x);
        if (dx > L / 2)
            dx = L - dx;

        double dy = Math.abs(this.y - p.y);
        if (dy > L / 2)
            dy = L - dy;

        return Math.sqrt(Math.pow(dx,2) + Math.pow(dy,2))
                < (this.getR() +  p.getR() + r_c);
    }
}
