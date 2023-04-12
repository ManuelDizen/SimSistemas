import models.Collision;
import models.Particle;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class PoolTable {

    private static List<Particle> particles;

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

        double initial_triangle_x = LONG_SIDE-0.56;
        double initial_triangle_y = SHORT_SIDE/2;

        //primera bola
        particles.add(new Particle(LONG_SIDE-0.56, SHORT_SIDE/2, 0, 0, 7, BALL_RADIUS, MASS));

        //2da fila
        particles.add(new Particle(initial_triangle_x + BALL_RADIUS*2 + MAX_EPSILON/2,
                initial_triangle_y + BALL_RADIUS + MAX_EPSILON/2, 0, 0, 8, BALL_RADIUS, MASS));
        particles.add(new Particle(initial_triangle_x + BALL_RADIUS*2 + MAX_EPSILON/2,
                initial_triangle_y - BALL_RADIUS + MAX_EPSILON/2, 0, 0, 9, BALL_RADIUS, MASS));

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

    public PriorityQueue<Collision> evaluateCollisions(PriorityQueue<Collision> collisions, Collision collision) {
        //TODO: Cambiar esta función, porque una vez que ocurre la primera colisión, hay que dejar de evaluar a todas las partículas (solo se evalúan las que cambiaron su trayectoria)
        if((collision.getIdx1() == -1) && (collision.getIdx2() == -1)) {
            for(int i=6; i<particles.size(); i++) {
                Particle p = particles.get(i);
                collisions.add(new Collision(-1, -1, p.getIdx(), p.getCollision_n(), p.timeToXWallBounce(2.24)));
                collisions.add(new Collision(p.getIdx(), p.getCollision_n(), -1, -1, p.timeToYWallBounce(1.12)));

                for(int j=i+1; j<particles.size(); j++) {
                    Particle p2 = particles.get(j);
                    collisions.add(new Collision(p.getIdx(), p.getCollision_n(), p2.getIdx(), p2.getCollision_n(), p.timeToParticleCollision(p2)));
                }

            }
        } else {
            Particle p1 = particles.get(collision.getIdx1());
            Particle p2 = particles.get(collision.getIdx2());
            collisions.add(new Collision(-1, -1, p1.getIdx(), p1.getCollision_n(), p1.timeToXWallBounce(2.24)));
            collisions.add(new Collision(p1.getIdx(), p1.getCollision_n(), -1, -1, p1.timeToYWallBounce(1.12)));
            collisions.add(new Collision(-1, -1, p2.getIdx(), p2.getCollision_n(), p2.timeToXWallBounce(2.24)));
            collisions.add(new Collision(p2.getIdx(), p2.getCollision_n(), -1, -1, p2.timeToYWallBounce(1.12)));
            for(int i=6; i<particles.size(); i++) {
                if(i != p1.getIdx() && i != p2.getIdx()) {
                    Particle p3 = particles.get(i);
                    collisions.add(new Collision(p1.getIdx(), p1.getCollision_n(), p3.getIdx(), p3.getCollision_n(), p1.timeToParticleCollision(p3)));
                    collisions.add(new Collision(p2.getIdx(), p2.getCollision_n(), p3.getIdx(), p3.getCollision_n(), p2.timeToParticleCollision(p3)));
                }
            }
        }

        return collisions;
    }

    private void updateCollision_ns(Collision collision) {
        if(collision.getIdx1() == -1)
            particles.get(collision.getIdx2()).bounceWithHorizontalWall();
        else if(collision.getIdx2() == -1)
            particles.get(collision.getIdx1()).bounceWithVerticalWall();
        else
            particles.get(collision.getIdx1()).bounceWithParticle(particles.get(collision.getIdx2()));

    }

    private boolean isValid(Collision collision) {
        if(collision.getT() >= 0) {
            int col_n1, col_n2;
            if(collision.getIdx1() == -1) //pared horizontal
                col_n1 = -1;
            else
                col_n1 = particles.get(collision.getIdx1()).getCollision_n();
            if(collision.getIdx2() == -1) //pared vertical
                col_n2 = -1;
            else
                col_n2 = particles.get(collision.getIdx2()).getCollision_n();
            return collision.isValidCollision(col_n1, col_n2);
        }
        return false;
    }

    public void updateAllParticles(double t) {
        for(Particle p : particles) {
            p.setX(p.getX() + p.getVx()*t);
            p.setY(p.getY() + p.getVy()*t);
        }
    }

    private void updateCollisionTimes(PriorityQueue<Collision> collisions, double time) {
        for(Collision col : collisions) {
            col.elapse(time);
        }
    }


    public static void main(String[] args) {
        // args[0] = initial y position for white ball
        PoolTable table = new PoolTable(Double.parseDouble(args[0]));
        table.generateParticles();
        PriorityQueue<Collision> collisions = new PriorityQueue<>(new Comparator<Collision>() {
            @Override
            public int compare(Collision o1, Collision o2) {
                return (int) (o1.getT() - o2.getT());
            }
        });
        try(FileWriter output = new FileWriter(new File("C:\\Users\\Franco De Simone\\Documents\\2C\\SS\\SimSistemas\\TP3\\src\\utils", "output2.txt"));) {
            int i=0;
            Collision next = new Collision(-1, -1, -1, -1, -1);
            while(i<10) { //TODO: condición de corte
                setHeaders(output, 16, i);
                for(int idx = 6; idx<22; idx++){
                    Particle p = particles.get(idx);
                    output.write(String.format("%d %f %f %f %f %f %f %f\n", p.getIdx(),
                            p.getX(), p.getY(), 0*1.0,
                            p.getVx(), p.getVy(),
                            p.getAngle(), p.getMass()));
                }

                collisions = table.evaluateCollisions(collisions, next); //ordeno todas las colisiones en la cola
                System.out.println("NEW COLLISIONS!-------------------------------------");
                for(Collision col : collisions) {
                    System.out.println(col.getIdx1() + ", " + col.getIdx2() + ": " + col.getT());
                }

                do {
                    next = collisions.poll();

                } while(!table.isValid(next)); //busco la primera colisión válida
                System.out.println("collision! " + next.getIdx1() + ", " + next.getIdx2() + ": " + next.getT());
                System.out.println("Collisions AFTER COLLISION!-------------------------------------");
                for(Collision col : collisions) {
                    System.out.println(col.getIdx1() + ", " + col.getIdx2() + ": " + col.getT());
                }
                table.updateAllParticles(next.getT()); //muevo todas las partículas al tiempo t
                table.updateCollision_ns(next);
                table.updateCollisionTimes(collisions, next.getT());
                System.out.println("Collisions AFTER CHANGING TIMES!-------------------------------------");
                for(Collision col : collisions) {
                    System.out.println(col.getIdx1() + ", " + col.getIdx2() + ": " + col.getT());
                }
                //hago el choque
                i++;
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
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
