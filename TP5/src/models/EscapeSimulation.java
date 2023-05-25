package models;

import java.util.ArrayList;
import java.util.List;

public class EscapeSimulation {
    /*
    Los valores de estos parámetros fijos son tomados a partir del paper.

    Se tomará el dataset que dice "set of parameters 1" con la excepción que se
    aclara que vd max debe ser 2 en la consigna. TODO: Mandar mail preguntando, en paper dice 1.55

     */
    private static final double MAX_DESIRED_VEL = 2.0; // 2m/s
    private static final double MAX_ESCAPE_VEL = MAX_DESIRED_VEL; // ve max = vd max (en paper)

    private static final double R_MIN = 0.15; // m
    private static final double R_MAX = 0.32;
    private static final double BETA = 0.9;

    private final List<Particle> particles = new ArrayList<>();
    private final List<Particle> corners = new ArrayList<>();
    private static int N_PARTICLES;
    private static double exit;
    private static double target_d;
    private static double target_d_x1;
    private static double target_d_x2;

    public EscapeSimulation(int n, double d){
        N_PARTICLES = n;
        exit = d;
        target_d = exit - 0.2;
        target_d_x1 = (20/2) - (target_d/2);
        target_d_x2 = (20/2) + (target_d/2);
    }

    public void createParticles(){
        for(int n = 0; n < N_PARTICLES; n++){
            /*
            Necesito ubicar las partículas entre (0, 10), (0,30), (20, 30), (20,10)
            y darles un target (con y = 10) entre valores x de (20/2) +- (target_d / 2)
            y ver que no arranquen superpuestas

            Nota: Como se pide que después haya un 2do target directamente por debajo
            del primero a 10 metros, "levanto" 10 metros el cuadro de 20x20 (ahora el y
            inicial esta en 10 y va hasta 30). El 2do target estará en y = 0

            TODO: Preguntar si dibujo está bien (muestra 2do target de 3m pero parece medir lo mismo que t1)

            El radio inicial de todas será r_min

            Para el target_x, leer del paper página 4 debajo de "B. Specific Flow Rate"

            TODO: Chequear colisiones con las paredes
             */
            double aux_x = (Math.random()*20) + 10;
            double aux_y = (Math.random()*20);
            while(collides(aux_x, aux_y)){
                aux_x = (Math.random()*20) + 10;
                aux_y = (Math.random()*20);
            }
            Particle p = new Particle(aux_x, aux_y, 0, 0, R_MIN);
            double target_x = (aux_x < target_d_x1 + 0.2*target_d) ||
                    (aux_x > target_d_x1 + 0.8*target_d) ?
                    getRandomRange(target_d_x1 + 0.2*target_d, target_d_x1 + 0.8*target_d): aux_x;
            p.setTarget_x(target_x);
            p.setTarget_y(10);
            particles.add(p);
        }
    }

    private boolean collides(double x, double y){
        for(Particle p : particles){
            double distance = Math.sqrt(Math.pow(x - p.getX(), 2) + Math.pow(y - p.getY(), 2));
            if(distance <= 2*R_MIN){
                System.out.println("Particle p " + p.toString() + " collides with " + x + ", " + y);
                return true;
            }
        }
        return false;
    }

    public double getRandomRange(double min, double max){
        return (Math.random() * (max-min)) + min;
    }

    public List<Particle> getParticles(){
        return this.particles;
    }


}
