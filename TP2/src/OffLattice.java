import models.Particle;
import utils.Neighbours;

import java.util.ArrayList;
import java.util.List;

public class OffLattice {

    private static List<Particle> particles;
    private static final double FULL_ANGLE = 2 * Math.PI;

    private int L;
    private int N;
    private double eta;
    private static final double R_C = 1.0;
    private static final double V = 0.03;
    private static final int dT = 1;

    public OffLattice(double eta, int N, int L) {
        this.eta = eta;
        this.N = N;
        this.L = L;
        this.particles = new ArrayList<>();
    }

    public int getL() {
        return L;
    }

    public void setL(int l) {
        L = l;
    }

    public int getN() {
        return N;
    }

    public void setN(int n) {
        N = n;
    }

    public double getEta() {
        return eta;
    }

    public void setEta(double eta) {
        this.eta = eta;
    }

    private void generateParticles() {

        int N = getN();
        double L = getL();

        for(int i=0; i<N; i++) {
            double angle = Math.random() * FULL_ANGLE;
            Particle particle = new Particle(i, angle);
            double x = Math.random() * L;
            double y = Math.random() * L;
            particle.setX(x);
            particle.setY(y);
            this.particles.add(particle);
        }
    }

    private void nextIteration() {

        Neighbours.CellIndexMethod(particles, getN(), getL(), 10, R_C, true);

        for(Particle p : particles) {
            p.setX(p.getX() + V*Math.cos(p.getAngle())*dT);
            p.setY(p.getY() + V*Math.sin(p.getAngle())*dT);
            p.updateAngle();
        }

    }

    public static void main(String[] args) {

        OffLattice offLattice = new OffLattice(Double.parseDouble(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));

        if (args.length != 3) {
            System.out.println("Usage: java SelfPropelledSystem <eta> <particle_count> <space_size>");
            System.exit(1);
        }
        //Generar N partículas aleatorias (posición, ángulo)
        offLattice.generateParticles();

        for(Particle particle : particles) {
            System.out.printf(particle.getIdx() + ": " + particle.getX() + ", " + particle.getY() + ", angle: " + particle.getAngle()*360/(2*Math.PI) + "\n");
        }









        //Modelar un paso temporal dt = 1

    }

}
