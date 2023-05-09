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



    public void calculateInitialCollisions() {
        Particle white = getByIdx(WHITE_IDX);
        collisions.add(new Collision(white.getIdx(), white.getCollision_n(), -1, -1, white.timeToXWallBounce(2.24)));
        for (int j = WHITE_IDX + 1; j < particles.size(); j++) {
            Particle p2 = particles.get(j);
            double tc = white.timeToParticleCollision(p2);
            if (tc > 0)
                collisions.add(new Collision(white.getIdx(), white.getCollision_n(), p2.getIdx(), p2.getCollision_n(), tc));
        }
    }


    private void removePreviousCollisions(PriorityQueue<Collision> collisions, Particle p) {

        int toRemove = p.getIdx();
        Predicate<Collision> pr = a -> (a.getIdx1() == toRemove || a.getIdx2() == toRemove);
        collisions.removeIf(pr);

    }

    public void updateAfterCollision(PriorityQueue<Collision> collisions, Particle p) {

        removePreviousCollisions(collisions, p);

        double time_to_x = p.timeToXWallBounce(LONG_SIDE);
        if (time_to_x > 0)
            collisions.add(new Collision(p.getIdx(), p.getCollision_n(), -1, -1,
                    time_to_x));
        double time_to_y = p.timeToYWallBounce(SHORT_SIDE);
        if (time_to_y > 0)
            collisions.add(new Collision(-1, -1, p.getIdx(), p.getCollision_n(),
                    time_to_y));

        int ogIdx = p.getIdx();
        for (Particle k : particles) {
            int i = k.getIdx();
            if (i != ogIdx) {
                double tc = p.timeToParticleCollision(k);
                if (tc > 0)
                    collisions.add(new Collision(p.getIdx(), p.getCollision_n(), k.getIdx(), k.getCollision_n(), tc));
            }
        }
    }

    private Particle getByIdx(int idx) {
        return particles.stream().filter(p -> (p.getIdx() == idx)).findFirst().orElse(null);
    }

    private void updateCollision_ns(Collision collision) {
        Particle aux = null;
        if (collision.getIdx1() == -1) {
            aux = getByIdx(collision.getIdx2());
            if (aux != null)
                aux.bounceWithHorizontalWall();
        } else if (collision.getIdx2() == -1) {
            aux = getByIdx(collision.getIdx1());
            if (aux != null)
                aux.bounceWithVerticalWall();
        } else {
            Particle aux1 = getByIdx(collision.getIdx1());
            Particle aux2 = getByIdx(collision.getIdx2());
            aux1.bounceWithParticle(aux2);
            aux2.incCollision_n();
        }
    }

    private void updateCollisionPocket(Collision collision,
                                       PriorityQueue<Collision> collisions) {
        int toRemove = collision.getIdx1() <= 5 ? collision.getIdx2() : collision.getIdx1(); // Si llegue hasta acá, uno de los dos es pocket
        particles.removeIf(p -> p.getIdx() == toRemove);
        Predicate<Collision> pr = a -> (a.getIdx1() == toRemove || a.getIdx2() == toRemove);
        collisions.removeIf(pr);

    }

    private boolean isValid(Collision collision) {
        if (collision.getT() >= 0) {
            int col_n1 = -1, col_n2 = -1;
            Particle p;
            if (collision.getIdx1() != -1) { //pared horizontal
                p = getByIdx(collision.getIdx1());
                if (p != null)
                    col_n1 = p.getCollision_n();
            }
            if (collision.getIdx2() != -1) { //pared vertical
                p = getByIdx(collision.getIdx2());
                if (p != null) {
                    col_n2 = p.getCollision_n();
                }
            }
            return collision.isValidCollision(col_n1, col_n2);
        }
        return false;
    }

    public void updateAllParticles(double t) {
        for (Particle p : particles) {
            p.setX(p.getX() + p.getVx() * t);
            p.setY(p.getY() + p.getVy() * t);
        }
    }

    private void updateCollisionTimes(PriorityQueue<Collision> collisions, double time) {
        List<Collision> toRemove = new ArrayList<>();
        for (Collision col : collisions) {
            col.elapse(time);
            if(col.getT() == Double.MIN_VALUE){
                toRemove.add(col);
            }
        }
        collisions.removeAll(toRemove);
    }

    public void simulateNoBuchacas(double total_time, double delta_t, Collision c){
        double elapsed_time = 0;
        while(elapsed_time < total_time){

        }
    }

    public Collision getNextCollision(){
        Collision next;
        do {
            next = collisions.poll();
            if (next == null) {
                System.out.println("Error de sistema: No quedan colisiones y no se terminaron las partículas.");
                System.exit(1);
            }
        } while (!isValid(next)); //busco la primera colisión válida
        return next;
    }

    public void previousRunnable(String[] args) {
        // args[0] = initial y position for white ball
        double[] heights = {0.56};
        double[] velocities = {2.0};
        int[] precisions = {20, 16, 12, 8, 4};


        double[] finalTimes = new double[precisions.length];
        double[] stdDevs = new double[precisions.length];
        int timesCounter = 0;
        int iterations = 50;
        for(double initial_y : heights) {
            for (double initial_velocity : velocities) {
                for(int precision : precisions) {
                    List<Double> times = new ArrayList<>();
                    for (int iters = 0; iters < iterations; iters++) {
                        PoolTable table = new PoolTable(initial_y, delta_t);
                        table.generateParticles();

                        //PriorityQueue<Collision> collisions = new PriorityQueue<>();
                        table.calculateInitialCollisions();

                        double totalTime = 0;
                        try (FileWriter output = new FileWriter(
                                String.format("output_y=%f_f=%d.txt", initial_y, precision))) {
                            int i = 0;
                            Collision next;
                            double[] collisionTimes = new double[MAX_COLLISIONS*100];
                            int timesCounter2 = 0;
                            while (particles.size() > 6) {
                                FileUtils.takeSystemSnapshot(output, particles, i);
                                do {
                                    next = collisions.poll();
                                    if (next == null) {
                                        System.out.println("Error de sistema: No quedan colisiones y no se terminaron las partículas.");
                                        System.exit(1);
                                    }
                                } while (!table.isValid(next)); //busco la primera colisión válida

                                collisionTimes[timesCounter2++] = next.getT();
                                table.updateAllParticles(next.getT());

                                if (next.isPocket()) {
                                    table.updateCollisionPocket(next, collisions);
                                    table.updateCollisionTimes(collisions, next.getT());
                                } else {
                                    table.updateCollision_ns(next);
                                    table.updateCollisionTimes(collisions, next.getT());

                                    if (next.getIdx1() != -1) {
                                        Particle p = table.getByIdx(next.getIdx1());
                                        if (p != null)
                                            table.updateAfterCollision(collisions, p);
                                    }
                                    if (next.getIdx2() != -1) {
                                        Particle p = table.getByIdx(next.getIdx2());
                                        if (p != null)
                                            table.updateAfterCollision(collisions, p);
                                    }
                                }
                                totalTime += next.getT();
                                i++;
                            }
                            FileUtils.takeSystemSnapshot(output, particles, i);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        times.add(totalTime);
                    }

                    double avg = (times.stream().mapToDouble(a -> a).sum()) / ((long) times.size());
                    double[] array = times.stream().mapToDouble(Double::doubleValue).toArray();
                    double stdDev = calculateStandardDeviation(array);

                    System.out.println("Total times with initial v=" + initial_velocity + ", precision=" + precision +
                    ": " + "\nAverage: " + avg + "\nStdDev: " + stdDev);
                    stdDevs[timesCounter] = stdDev;
                    finalTimes[timesCounter++] = avg;
                }
            }
        }
    }

}
