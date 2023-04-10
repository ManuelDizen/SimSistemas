import com.sun.xml.internal.ws.wsdl.writer.document.Part;
import models.Particle;

import java.util.ArrayList;
import java.util.List;

public class PoolTable {

    private List<Particle> particles;

    private static final double LONG_SIDE = 2.24; // m

    private static final double SHORT_SIDE = 1.12; // m
    private static final double V = 2.00; // m/s
    private static final double INITIAL_X = 0.56; // m
    private static final double BALL_RADIUS = 0.0285; // m
    
    private static final double MAX_EPSILON = 0.0003; //0.03 cm

    private static double MASS = 0.165; // kg

    private double initial_y;

    public PoolTable(double initial_y) {
        this.initial_y = initial_y;
        this.particles = new ArrayList<>();
    }

    private void generateParticles() {

        //agujeros
        particles.add(new Particle(0, 0, 0, 0, 0, BALL_RADIUS*2, 0));
        particles.add(new Particle(LONG_SIDE/2, 0, 0, 0, 1, BALL_RADIUS*2, 0));
        particles.add(new Particle(LONG_SIDE, 0, 0, 0, 2, BALL_RADIUS*2, 0));
        particles.add(new Particle(0, SHORT_SIDE, 0, 0, 3, BALL_RADIUS*2, 0));
        particles.add(new Particle(LONG_SIDE/2, SHORT_SIDE, 0, 0, 4, BALL_RADIUS*2, 0));
        particles.add(new Particle(LONG_SIDE, SHORT_SIDE, 0, 0, 5, BALL_RADIUS*2, 0));

        //bola blanca
        particles.add(new Particle(INITIAL_X, initial_y, V, 0, 6, BALL_RADIUS, MASS)); //cambiar ángulo

        double initial_triangle_x = LONG_SIDE-56;
        double initial_triangle_y = SHORT_SIDE/2;

        //primera bola
        particles.add(new Particle(LONG_SIDE-56, SHORT_SIDE/2, 0, 0, 7, BALL_RADIUS, MASS));

        //2da fila
        particles.add(new Particle(initial_triangle_x + BALL_RADIUS*2 + MAX_EPSILON/2,
                initial_triangle_y + BALL_RADIUS + MAX_EPSILON/2, 0, 0, 8, BALL_RADIUS, MASS);
        particles.add(new Particle(initial_triangle_x + BALL_RADIUS*2 + MAX_EPSILON/2,
                initial_triangle_y - BALL_RADIUS + MAX_EPSILON/2, 0, 0, 9, BALL_RADIUS, MASS);

        // 3ra fila
        particles.add(new Particle(initial_triangle_x + (2*2*BALL_RADIUS) + MAX_EPSILON/2,
                                    initial_triangle_y + MAX_EPSILON/2, 0, 0, 10, BALL_RADIUS, MASS));
        particles.add(new Particle(initial_triangle_x + (2*2*BALL_RADIUS) + MAX_EPSILON/2,
                initial_triangle_y + MAX_EPSILON/2 + (2*BALL_RADIUS), 0, 0, 11, BALL_RADIUS, MASS));
        particles.add(new Particle(initial_triangle_x + (2*2*BALL_RADIUS) + MAX_EPSILON/2,
                initial_triangle_y - (2*BALL_RADIUS) + MAX_EPSILON/2, 0, 0, 12, BALL_RADIUS, MASS));
        
        //4ta fila
        particles.add(new Particle(initial_triangle_x + (2*3*BALL_RADIUS) + MAX_EPSILON/2,
                initial_triangle_y - (3*BALL_RADIUS) + MAX_EPSILON/2, 0, 0, 13, BALL_RADIUS, MASS));
        particles.add(new Particle(initial_triangle_x + (2*3*BALL_RADIUS) + MAX_EPSILON/2,
                initial_triangle_y + (3*BALL_RADIUS) + MAX_EPSILON/2, 0, 0, 14, BALL_RADIUS, MASS));
        particles.add(new Particle(initial_triangle_x + (2*3*BALL_RADIUS) + MAX_EPSILON/2,
                initial_triangle_y - BALL_RADIUS + MAX_EPSILON/2, 0, 0, 15, BALL_RADIUS, MASS));
        particles.add(new Particle(initial_triangle_x + (2*3*BALL_RADIUS) + MAX_EPSILON/2,
                initial_triangle_y - BALL_RADIUS + MAX_EPSILON/2, 0, 0, 16, BALL_RADIUS, MASS));

        // 5ta fila
        particles.add(new Particle(initial_triangle_x + (2*4*BALL_RADIUS) + MAX_EPSILON/2,
                initial_triangle_y + MAX_EPSILON/2, 0, 0, 17, BALL_RADIUS, MASS));
        particles.add(new Particle(initial_triangle_x + (2*4*BALL_RADIUS) + MAX_EPSILON/2,
                initial_triangle_y + MAX_EPSILON/2 + (2*BALL_RADIUS), 0, 0, 18, BALL_RADIUS, MASS));
        particles.add(new Particle(initial_triangle_x + (2*4*BALL_RADIUS) + MAX_EPSILON/2,
                initial_triangle_y - (2*BALL_RADIUS) + MAX_EPSILON/2, 0, 0, 19, BALL_RADIUS, MASS));
        particles.add(new Particle(initial_triangle_x + (2*4*BALL_RADIUS) + MAX_EPSILON/2,
                initial_triangle_y + (2*2*BALL_RADIUS) + MAX_EPSILON/2, 0, 0, 20, BALL_RADIUS, MASS));
        particles.add(new Particle(initial_triangle_x + (2*4*BALL_RADIUS) + MAX_EPSILON/2,
                initial_triangle_y - (2*2*BALL_RADIUS) + MAX_EPSILON/2, 0, 0, 21, BALL_RADIUS, MASS));
        
    }

    public static void main(String[] args) {
        PoolTable table = new PoolTable(Double.parseDouble(args[0]));
        table.generateParticles();
    }

}
