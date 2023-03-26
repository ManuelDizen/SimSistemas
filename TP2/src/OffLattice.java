import models.Particle;
import utils.Neighbours;

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
        this.particles = new ArrayList<>();
    }

    public int getL() {
        return L;
    }

    public int getN() {
        return N;
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
        // Argv[0]=eta, argv[1]=N, argv[2]=L
        OffLattice offLattice = new OffLattice(Double.parseDouble(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));

        if (args.length != 3) {
            System.out.println("Usage: java SelfPropelledSystem <eta> <particle_count> <space_size>");
            System.exit(1);
        }
        //Generar N partículas aleatorias (posición, ángulo)
        offLattice.generateParticles();

        String fileName = "output_N=" + offLattice.N + "_L=" + offLattice.L + "_eta=" + offLattice.eta + ".txt";

        try(FileWriter output = new FileWriter(fileName)) {
            for (int i = 0; i < iterations; i++) {
                setHeaders(output, offLattice.N, i);
                for(Particle p : particles){
                    output.write(String.format("%d %f %f %f %f %f\n", p.getIdx(),
                            p.getX(), p.getY(), 0*1.0, p.getAngle(), radius*1.0));
                }
                // TODO: Guardar en un archivo el output necesario
                // (1) Dejo calculados los vecinos
                Neighbours.CellIndexMethod(particles, offLattice.getN(), offLattice.getL(),
                        2, R_C, true);

                //(2) Con vecinos calculados, puedo calcular nuevas direcciones
                // Nota: debemos tomar una instancia adicional de particles para no perder la "foto"
                /*
                Nota explicativa sobre la nota anterior: No es necesario porque la serie de pasos es:
                    1. Calcular direcciones nuevas antes de mover cualquier particula
                    2. Mover en función del ÁNGULO y no de posición

                 Al no ser pertinente la posición, y el ángulo ya cambió, no es necesaria uan estructura
                 auxiliar
                 */

                for (Particle p : particles) {
                    p.updateAngle();
                    p.updatePosition(V, offLattice.getL());
                }
            }
        }
        catch(IOException e){
            System.out.println(e.getMessage());
            System.exit(1);
        }
        //Modelar un paso temporal dt = 1

    }

    private static void setHeaders(FileWriter output, int N, int i){
        try {
            /*output.write(String.format("%d atoms\n1 atom types\n\n", N));
            output.write(String.format("0.000000 %f xlo xhi\n" +
                            "0.000000 %f ylo yhi\n" +
                            "0.000000 %f zlo zhi\n",
                    L*1.0, L*1.0, L*1.0));
            output.write(String.format("\nAtoms\n\n"));*/
            output.write(String.format("%d\nFrame %d\n", N, i));
        }
        catch(IOException e){
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}
