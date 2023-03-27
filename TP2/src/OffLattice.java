import models.Particle;
import utils.Neighbours;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OffLattice {

    private static List<Particle> particles;
    private static final double FULL_ANGLE = 2 * Math.PI;

    private int L;
    private int N;
    private int M;
    private double eta;
    private static final double R_C = 1.0;
    private static final double V = 0.03;
    private static final int dT = 1;

    private static final int radius = 0;

    private static final int iterations = 1000;

    public OffLattice(double eta, int N, int L) {
        this.eta = eta;
        this.N = N;
        this.L = L;
        this.M = 5;//(int) Math.floor(L/4.0);
        this.particles = new ArrayList<>();
    }

    public int getL() {
        return L;
    }

    public int getN() {
        return N;
    }
    public int getM(){return M;}

    private void generateParticles(double V) {

        int N = getN();
        double L = getL();

        for(int i=0; i<N; i++) {
            //double angle = Math.random() * FULL_ANGLE;
            double angle = Math.random() * Math.PI * (Math.random() < 0.5? -1:1);
            Particle particle = new Particle(i, angle, V);
            double x = Math.random() * L;
            double y = Math.random() * L;
            particle.setX(x);
            particle.setY(y);
            particle.setEta(eta);
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

    private static double calculateOrder(int N) {
        double cos = 0;
        double sin = 0;
        for(Particle p : particles) {
            cos += V*Math.cos(p.getAngle());
            sin += V*Math.sin(p.getAngle());
        }
        double order = Math.sqrt(Math.pow(cos, 2) + Math.pow(sin, 2));
        return (Math.abs(order) / (N * V));
    }


    public static void main(String[] args) {
        // Argv[0]=eta, argv[1]=N, argv[2]=L
        OffLattice offLattice = new OffLattice(Double.parseDouble(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));

        if (args.length != 3) {
            System.out.println("Usage: java SelfPropelledSystem <eta> <particle_count> <space_size>");
            System.exit(1);
        }
        offLattice.generateParticles(V);

        String fileName = "output_N=" + offLattice.N + "_L=" + offLattice.L + "_eta=" +
                offLattice.eta + ".txt";
        String vaFileName = "va_output_N" + offLattice.N + "_L" + offLattice.L + "_eta=" +
                offLattice.eta + ".txt";


        try(FileWriter output = new FileWriter(new File("C:\\Users\\Franco De Simone\\Documents\\SS\\SimSistemas\\TP2\\src\\utils", fileName))) {
            try(FileWriter vaOutput = new FileWriter(new File("C:\\Users\\Franco De Simone\\Documents\\SS\\SimSistemas\\TP2\\src\\utils", vaFileName))) {
                for (int i = 0; i < iterations; i++) {
                    System.out.println(i + "\n");
                    setHeaders(output, offLattice.N, i);
                    for(Particle p : particles){
                        output.write(String.format("%d %f %f %f %f %f %f %f\n", p.getIdx(),
                                p.getX(), p.getY(), 0*1.0,
                                p.getVx(), p.getVy(),
                                p.getAngle(), radius*1.0));
                    }
                    vaOutput.write(String.format("%f\n", calculateOrder(offLattice.getN())));
                    Neighbours.CellIndexMethod(particles, offLattice.getN(), offLattice.getL(),
                            offLattice.getM(), R_C, true);

                    for (Particle p : particles) {
                        p.updateAngle();
                        p.updatePosition(V, offLattice.getL());
                    }
                }
            } catch(IOException e){
                System.out.println(e.getMessage());
                System.exit(1);
            }
        }
        catch(IOException e){
            System.out.println(e.getMessage());
            System.exit(1);
        }

    }

    private static void setHeaders(FileWriter output, int N, int i){
        try {
            output.write(String.format("%d\nFrame %d\n", N, i));
        }
        catch(IOException e){
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}
