package utils;

import models.Particle;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class OutputParser {
    public static void takeSnapshot(FileWriter fw, List<Particle> particles, int i){
        setHeaders(fw, particles.size(), i); //TODO: Optimize
        try {
            for (Particle p : particles) {
                fw.write(String.format("%d %f %f %f %f %f %f %f %f\n", p.getIdx(),
                        p.getX(), p.getY(), 0 * 1.0,
                        p.getVx(), p.getVy(),
                        p.getAngle(), p.getMass(),
                        p.getRadius()));
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
