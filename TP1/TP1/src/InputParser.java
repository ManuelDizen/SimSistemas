import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class InputParser {

    static Queue<Particle> particles = new LinkedList<>();

    public static Queue<Particle> parseParticles() throws FileNotFoundException {
        staticSystemParse();
        dynamicSystemParse();
        return particles;
    }

    private static void staticSystemParse() throws FileNotFoundException {
        File staticFile = new File("/home/manuel/Desktop/SimSistemas/TP1/TP1/src/files/Static100.txt");
        Scanner sc = new Scanner(staticFile);
        //numberOfParticles = sc.nextInt();
        CIM.CIM_PARTICLE_NUMBER = sc.nextInt();
        //areaLength = sc.nextDouble();
        CIM.CIM_LENGTH_SIDE = sc.nextInt();
        for (int i = 0; i < CIM.CIM_PARTICLE_NUMBER; i++){
            double radius   = sc.nextDouble();
            double property = sc.nextDouble();
            particles.add(new Particle(i + 1, radius, property));
        }
    }

    private static void dynamicSystemParse() throws FileNotFoundException {
        File dynamicFile = new File("/home/manuel/Desktop/SimSistemas/TP1/TP1/src/files/Dynamic100.txt");
        Scanner sc = new Scanner(dynamicFile);
        sc.nextInt();
        for (int i = 0; i < CIM.CIM_PARTICLE_NUMBER; i++){
            double x = sc.nextDouble();
            double y = sc.nextDouble();
            Particle particle = particles.poll();
            if(particle == null){throw new IllegalArgumentException();}
            particle.setX(x);
            particle.setY(y);
            particles.add(particle);
        }
    }
}
