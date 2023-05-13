package System2;

import utils.Particle;
import utils.FileUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.Predicate;

import static utils.StatisticsUtils.calculateStandardDeviation;

public class PoolTable {

    private static final int MAX_COLLISIONS = 1000;
    List<Particle> particles = new ArrayList<>();

    final double LONG_SIDE = 2.24; // m

    final double SHORT_SIDE = 1.12; // m
    private final double V = 2; // m/s
    private static final double INITIAL_X = 0.56; // m
    private static final double BALL_RADIUS = 0.0285; // m

    private static final double MAX_EPSILON = 0.0003; //0.03 cm

    private static final int WHITE_IDX = 6;

    private static double MASS = 0.165; // kg

    private final double initial_y;

    private final double delta_t;

    private static PriorityQueue<Collision> collisions = new PriorityQueue<>();

    public PoolTable(double initial_y, double delta_t){
        this.initial_y = initial_y;
        this.delta_t = delta_t;
    }
    
    private double epsilon() {
        return (MAX_EPSILON - Math.random() * 0.0001) / Math.sqrt(2);
    }

    public void generateSix() {
        particles.add(new Particle(INITIAL_X, initial_y, V, 0, 6, BALL_RADIUS, MASS)); //cambiar ángulo
        double initial_triangle_x = LONG_SIDE - 0.56;
        double initial_triangle_y = SHORT_SIDE / 2;

        //primera bola
        particles.add(new Particle(initial_triangle_x, initial_triangle_y, 0, 0, 7, BALL_RADIUS, MASS));

        double epsilon = epsilon();
        particles.add(new Particle(initial_triangle_x + BALL_RADIUS * 2 + epsilon,
                initial_triangle_y + BALL_RADIUS + epsilon, 0, 0, 8, BALL_RADIUS, MASS));
        epsilon = epsilon();
        particles.add(new Particle(initial_triangle_x + BALL_RADIUS * 2 + epsilon,
                initial_triangle_y - BALL_RADIUS - epsilon, 0, 0, 9, BALL_RADIUS, MASS));
    }

    public void generateParticlesNoBuchacas(){
        //bola blanca
        particles.add(new Particle(INITIAL_X, initial_y, V, 0, 6, BALL_RADIUS, MASS)); //cambiar ángulo

        double initial_triangle_x = LONG_SIDE - 0.56;
        double initial_triangle_y = SHORT_SIDE / 2;

        //primera bola
        particles.add(new Particle(initial_triangle_x, initial_triangle_y, 0, 0, 7, BALL_RADIUS, MASS));
//        System.out.println(initial_triangle_x + ", " + initial_triangle_y);

        //2da fila
        double epsilon = epsilon();
        particles.add(new Particle(initial_triangle_x + BALL_RADIUS * 2 + epsilon,
                initial_triangle_y + BALL_RADIUS + epsilon, 0, 0, 8, BALL_RADIUS, MASS));
        epsilon = epsilon();
        particles.add(new Particle(initial_triangle_x + BALL_RADIUS * 2 + epsilon,
                initial_triangle_y - BALL_RADIUS - epsilon, 0, 0, 9, BALL_RADIUS, MASS));

        // 3ra fila
        epsilon = epsilon();
        particles.add(new Particle(initial_triangle_x + (2 * 2 * BALL_RADIUS) + epsilon,
                initial_triangle_y + ((epsilon) * (Math.random() < 0.5 ? 1 : -1)), 0, 0,
                11, BALL_RADIUS, MASS));
        epsilon = epsilon();
        particles.add(new Particle(initial_triangle_x + (2 * 2 * BALL_RADIUS) + epsilon,
                initial_triangle_y + epsilon + (2 * BALL_RADIUS), 0, 0, 10, BALL_RADIUS, MASS));
        epsilon = epsilon();
        particles.add(new Particle(initial_triangle_x + (2 * 2 * BALL_RADIUS) + epsilon,
                initial_triangle_y - (2 * BALL_RADIUS) - epsilon, 0, 0, 12, BALL_RADIUS, MASS));

        //4ta fila
        epsilon = epsilon();
        particles.add(new Particle(initial_triangle_x + (2 * 3 * BALL_RADIUS) + epsilon,
                initial_triangle_y - (3 * BALL_RADIUS) - epsilon, 0, 0, 16, BALL_RADIUS, MASS));
        epsilon = epsilon();
        particles.add(new Particle(initial_triangle_x + (2 * 3 * BALL_RADIUS) + epsilon,
                initial_triangle_y + (3 * BALL_RADIUS) + epsilon, 0, 0, 13, BALL_RADIUS, MASS));
        epsilon = epsilon();
        particles.add(new Particle(initial_triangle_x + (2 * 3 * BALL_RADIUS) + epsilon,
                initial_triangle_y - BALL_RADIUS - epsilon, 0, 0, 15, BALL_RADIUS, MASS));
        epsilon = epsilon();
        particles.add(new Particle(initial_triangle_x + (2 * 3 * BALL_RADIUS) + epsilon,
                initial_triangle_y + BALL_RADIUS + epsilon, 0, 0, 14, BALL_RADIUS, MASS));

        // 5ta fila
        epsilon = epsilon();
        particles.add(new Particle(initial_triangle_x + (2 * 4 * BALL_RADIUS) + epsilon,
                initial_triangle_y + ((epsilon) * (Math.random() < 0.5 ? 1 : -1)), 0, 0,
                19, BALL_RADIUS, MASS));
        epsilon = epsilon();
        particles.add(new Particle(initial_triangle_x + (2 * 4 * BALL_RADIUS) + epsilon,
                initial_triangle_y + epsilon + (2 * BALL_RADIUS),
                0, 0, 18, BALL_RADIUS, MASS));
        epsilon = epsilon();
        particles.add(new Particle(initial_triangle_x + (2 * 4 * BALL_RADIUS) + epsilon,
                initial_triangle_y - (2 * BALL_RADIUS) - epsilon,
                0, 0, 20, BALL_RADIUS, MASS));
        epsilon = epsilon();
        particles.add(new Particle(initial_triangle_x + (2 * 4 * BALL_RADIUS) + epsilon,
                initial_triangle_y + (2 * 2 * BALL_RADIUS) + epsilon,
                0, 0, 17, BALL_RADIUS, MASS));
        epsilon = epsilon();
        particles.add(new Particle(initial_triangle_x + (2 * 4 * BALL_RADIUS) + epsilon,
                initial_triangle_y - (2 * 2 * BALL_RADIUS) - epsilon, 0, 0,
                21, BALL_RADIUS, MASS));


    }
    public void generateParticles() {

        //agujeros
        particles.add(new Particle(0, 0, 0, 0, 0, BALL_RADIUS * 2, 0));
        particles.add(new Particle(LONG_SIDE / 2, 0, 0, 0, 1, BALL_RADIUS * 2, 0));
        particles.add(new Particle(LONG_SIDE, 0, 0, 0, 2, BALL_RADIUS * 2, 0));
        particles.add(new Particle(0, SHORT_SIDE, 0, 0, 3, BALL_RADIUS * 2, 0));
        particles.add(new Particle(LONG_SIDE / 2, SHORT_SIDE, 0, 0, 4, BALL_RADIUS * 2, 0));
        particles.add(new Particle(LONG_SIDE, SHORT_SIDE, 0, 0, 5, BALL_RADIUS * 2, 0));

        generateParticlesNoBuchacas();
    }

    private Particle getByIdx(int idx) {
        return particles.stream().filter(p -> (p.getIdx() == idx)).findFirst().orElse(null);
    }

    public double getDeltaT(){
        return this.delta_t;
    }

}
