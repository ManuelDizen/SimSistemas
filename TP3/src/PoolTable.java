import models.Collision;
import models.Particle;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

public class PoolTable {

    private static List<Particle> particles;

    private static final double LONG_SIDE = 2.24; // m

    private static final double SHORT_SIDE = 1.12; // m
    private static final double V = 2.00; // m/s
    private static final double INITIAL_X = 0.56; // m
    private static final double BALL_RADIUS = 0.0285; // m
    
    private static final double MAX_EPSILON = 0.0003; //0.03 cm

    private static final int WHITE_IDX = 6;

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


    public PriorityQueue<Collision> setInitialCollisions(PriorityQueue<Collision> collisions){
        for(int i=6; i<particles.size(); i++) {
            Particle p = particles.get(i);
            collisions.add(new Collision(-1, -1, p.getIdx(), p.getCollision_n(), p.timeToXWallBounce(2.24)));
            collisions.add(new Collision(p.getIdx(), p.getCollision_n(), -1, -1, p.timeToYWallBounce(1.12)));

            for(int j=i+1; j<particles.size(); j++) {
                Particle p2 = particles.get(j);
                collisions.add(new Collision(p.getIdx(), p.getCollision_n(), p2.getIdx(), p2.getCollision_n(), p.timeToParticleCollision(p2)));
            }

        }
        return collisions;
    }

    private void calculateInitialCollisions(PriorityQueue<Collision> collisions){
        Particle white = getByIdx(WHITE_IDX);
        collisions.add(new Collision(white.getIdx(), white.getCollision_n(), -1, -1, white.timeToXWallBounce(2.24)));
        for(int j=WHITE_IDX+1; j<particles.size(); j++) {
            Particle p2 = particles.get(j);
            double tc = white.timeToParticleCollision(p2);
            if(tc > 0)
                collisions.add(new Collision(white.getIdx(), white.getCollision_n(), p2.getIdx(), p2.getCollision_n(), tc));
        }
    }


    private void removePreviousCollisions(PriorityQueue<Collision> collisions, Particle p){

        int toRemove = p.getIdx();
        Predicate<Collision> pr = a->(a.getIdx1() == toRemove || a.getIdx2() == toRemove);
        collisions.removeIf(pr);

    }

    public PriorityQueue<Collision> updateAfterCollision(PriorityQueue<Collision> collisions, Particle p){

        removePreviousCollisions(collisions, p);

        double time_to_x = p.timeToXWallBounce(LONG_SIDE);
        if(time_to_x > 0)
            collisions.add(new Collision(p.getIdx(), p.getCollision_n(), -1, -1,
                    time_to_x));
        double time_to_y = p.timeToYWallBounce(SHORT_SIDE);
        if(time_to_y > 0)
            collisions.add(new Collision(-1, -1, p.getIdx(), p.getCollision_n(),
                    time_to_y));
        for(Particle k : particles) {
            int i = k.getIdx();
            if(i != p.getIdx()) {
                double tc = p.timeToParticleCollision(k);
                if(tc > 0 )
                    collisions.add(new Collision(p.getIdx(), p.getCollision_n(), k.getIdx(), k.getCollision_n(),
                            tc));
            }
        }
        return collisions;
    }

    private Particle getByIdx(int idx){
        return particles.stream().filter(p -> (p.getIdx() == idx)).findFirst().orElse(null);
    }

    private void updateCollision_ns(Collision collision, PriorityQueue<Collision> collisions) {

        //System.out.println("Entro a updateCollision_ns");
        //System.out.println("collision: " + collision.getIdx1() + ": " + getByIdx(collision.getIdx1()).getCollision_n() + " - " + collision.getIdx2() + ": " + getByIdx(collision.getIdx2()).getCollision_n() );

        Particle aux = null;
        if(collision.getIdx1() == -1) {
            //particles.get(collision.getIdx2()).bounceWithHorizontalWall();
            aux = getByIdx(collision.getIdx2());
            if (aux != null)
                aux.bounceWithHorizontalWall();
        }
        else if(collision.getIdx2() == -1) {
            //particles.get(collision.getIdx1()).bounceWithVerticalWall();
            aux = getByIdx(collision.getIdx1());
            if (aux != null)
                aux.bounceWithVerticalWall();
        }
        else {
            //particles.get(collision.getIdx1()).bounceWithParticle(particles.get(collision.getIdx2()));
            Particle aux1 = getByIdx(collision.getIdx1());
            Particle aux2 = getByIdx(collision.getIdx2());
            aux1.bounceWithParticle(aux2);
            aux2.incCollision_n();
        }

        //System.out.println("collision: " + collision.getIdx1() + ": " + getByIdx(collision.getIdx1()).getCollision_n() + " - " + collision.getIdx2() + ": " + getByIdx(collision.getIdx2()).getCollision_n() );
    }

    private PriorityQueue<Collision> updateCollisionPocket(Collision collision,
                                                           PriorityQueue<Collision> collisions){
        System.out.println("POCKET!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        int toRemove = collision.getIdx1() <= 5? collision.getIdx2():collision.getIdx1(); // Si llegue hasta acá, uno de los dos es pocket
        System.out.println(toRemove);
        particles.removeIf(p -> p.getIdx() == toRemove);
        Predicate<Collision> pr = a->(a.getIdx1() == toRemove || a.getIdx2() == toRemove);
        collisions.removeIf(pr);

        return collisions;
    }

    private boolean isValid(Collision collision) {
        if(collision.getT() >= 0) {
            int col_n1 = -1, col_n2 = -1;
            Particle p;
            if(collision.getIdx1() != -1) { //pared horizontal
                p = getByIdx(collision.getIdx1());
                if (p != null)
                    col_n1 = p.getCollision_n();
            }
            if(collision.getIdx2() != -1) { //pared vertical
                p = getByIdx(collision.getIdx2());
                if(p != null){
                    col_n2 = p.getCollision_n();
                }
            }
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
        PriorityQueue<Collision> collisions = new PriorityQueue<>();
        double totalTime = 0;
        try(FileWriter output = new FileWriter(
                "output2.txt")) {
            int i=0;
            Collision next;

            table.calculateInitialCollisions(collisions);
            //collisions = table.setInitialCollisions(collisions);
                // TODO: TIene sentido un setInitialCollisions? No alcanza con calcular las de la blanca?
            while(particles.size() > 6) { //TODO: condición de corte
                setHeaders(output, particles.size(), i);
                for(Particle p : particles){
                    if(p.getIdx() > 5){
                        output.write(String.format("%d %f %f %f %f %f %f %f\n", p.getIdx(),
                                p.getX(), p.getY(), 0*1.0,
                                p.getVx(), p.getVy(),
                                p.getAngle(), p.getMass()));
                    }
                }

                System.out.println(String.format("\nITERATION %d: (size %d)\n", i, particles.size()));

                do {
                    next = collisions.poll();
                    if(next.getT() >= 0)
                        System.out.println("next: " + next.getIdx1() + ", " + next.getIdx2() + ": " + next.getT());
                } while(!table.isValid(next)); //busco la primera colisión válida
                table.updateAllParticles(next.getT()); //muevo todas las partículas al tiempo t

                if(next.isPocket()){
                    table.updateCollisionPocket(next, collisions);
                    table.updateCollisionTimes(collisions, next.getT());
                }
                else {
                    table.updateCollision_ns(next, collisions);
                    table.updateCollisionTimes(collisions, next.getT());
                    if(next.getIdx1() != -1){
                        Particle p = table.getByIdx(next.getIdx1());
                        if(p!= null)
                            table.updateAfterCollision(collisions, p);
                    }
                    if(next.getIdx2() != -1){
                        Particle p = table.getByIdx(next.getIdx2());
                        if(p!= null)
                            table.updateAfterCollision(collisions, p);
                    }
                }//hago el choque

//                System.out.println("---------------ANTES DE SALIR---------------------------");
//                for(Collision col : collisions) {
//                    System.out.println("collision: " + col.getIdx1() + " - " + col.getIdx2() + ": " + col.getT());
//                }
                totalTime += next.getT();
                i++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.printf("\nTiempo total hasta que todas las bolas ingresaron a los huecos: %f\n", totalTime);
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
