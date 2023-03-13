import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;
import java.util.Scanner;

public class InputParser {

    static Queue<Particle> particles = new LinkedList<>();

    public static Queue<Particle> parseParticles(int N, int file) throws FileNotFoundException {
        particles = new LinkedList<>();
        staticSystemParse(N);
        dynamicSystemParse(N, file);
        return particles;
    }

  
    private static void staticSystemParse(int N) throws FileNotFoundException {
        File staticFile = new File("/home/manuel/Desktop/SimSistemas/TP1/TP1/src/files/Static" + N + ".txt");
        Scanner sc = new Scanner(staticFile).useLocale(Locale.US);
        //numberOfParticles = sc.nextInt();
        CIM.CIM_PARTICLE_NUMBER = sc.nextInt();
        //System.out.println(CIM.CIM_PARTICLE_NUMBER);
        //areaLength = sc.nextDouble();
        CIM.CIM_LENGTH_SIDE = sc.nextInt();
        //System.out.println(CIM.CIM_LENGTH_SIDE);
        System.out.println("particle number: " + CIM.CIM_PARTICLE_NUMBER);
        for (int i = 0; i < CIM.CIM_PARTICLE_NUMBER; i++){
            //System.out.println(i);
            double radius   = sc.nextDouble();
            double property = sc.nextDouble();
            particles.add(new Particle(i + 1, radius, property));
        }

        System.out.println("queue size: " + particles.size());
    }
    
    private static void dynamicSystemParse(int N, int file) throws FileNotFoundException {
        File dynamicFile = new File("/home/manuel/Desktop/SimSistemas/TP1/TP1/Dynamic" + N + "." + file + ".txt");
        Scanner sc = new Scanner(dynamicFile).useLocale(Locale.US);
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

        System.out.println("queue size: " + particles.size());
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
