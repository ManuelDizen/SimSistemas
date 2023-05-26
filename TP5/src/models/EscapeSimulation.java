package models;

import java.util.ArrayList;
import java.util.List;

import static models.Wall.*;

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

    private static final double TAU = 0.5; /*We choose τ = 0.5 s in accordance to the
                        value used in the desired force in the social force model*/



    private final List<Particle> particles = new ArrayList<>();
    private final List<Particle> corners = new ArrayList<>();
    private static int N_PARTICLES;
    private static double exit;
    private static double target_d;
    private static double target_d_x1;
    private static double target_d_x2;
    private static double time_step;
    private static int ROOM_WIDTH = 20;
    private static int ROOM_HEIGHT = 20;
    private static int ROOM_OFFSET_Y = 10;

    private static int ROOM_TOTAL_HEIGHT = ROOM_HEIGHT + ROOM_OFFSET_Y;

    public EscapeSimulation(int n, double d){
        N_PARTICLES = n;
        target_d = d; // Discutiendo con los chicos, la figura del ejercicio 2 solo se toma si hacemos SFM
        target_d_x1 = ((double) ROOM_WIDTH /2) - (target_d/2);
        target_d_x2 = ((double) ROOM_WIDTH /2) + (target_d/2);
        time_step = R_MIN / (2*MAX_DESIRED_VEL); /*VelDMAX == Vesc MAX
                (So, we choose for the model a fixed value of the escape speed v e = v d max .) */
    }

    public double[] calculateEscape(Particle p1, Particle p2){
        double[] norm = Utils.norm(new double[]{p1.getX(), p1.getY()},
                new double[]{p2.getX(), p2.getY()});
        return new double[]{norm[0]*MAX_ESCAPE_VEL, norm[1]*MAX_ESCAPE_VEL};

        /*double magnitude = Utils.magnitude(p1, p2);
        return new double[]{((p2.getX() - p1.getX())/magnitude)*MAX_ESCAPE_VEL,
                ((p2.getY() - p1.getY())/magnitude)*MAX_ESCAPE_VEL};*/
    }

    public void iterate(){
        /* 1. Calcular velocidades de escape */
        for(Particle p : particles){
            double[] escape = new double[]{0,0};
            for(Particle q : particles){
                if(p!=q && collides(p,q)){
                    double[] aux_escape = calculateEscape(q,p);
                    escape[0] += aux_escape[0];
                    escape[1] += aux_escape[1];
                }
            }
            List<Wall> walls = collideWithWalls(p);
            if(walls.size() != 0){
                /* Colisiona con paredes */
                for(Wall w : walls){
                    Particle aux_p = createParticleForWall(w, p);
                    double[] aux_escape = calculateEscape(aux_p, p);
                    escape[0] += aux_escape[0];
                    escape[1] += aux_escape[1];
                }
            }

            // 2. Ajusto radios de acuerdo a si colisiono o no
            if(!hasCollision(escape)){
                double adding_radius = (R_MAX - R_MIN)/(TAU / time_step);
                if(p.getRadius() + adding_radius >= R_MAX){
                    p.setRadius(R_MAX);
                }
                else{
                    p.setRadius(p.getRadius() + adding_radius);
                }
            }
            else{
                p.setRadius(R_MIN); // COlisiono con algo => Vuelve a estado 0
            }

            // 3. Calculo direcciones y magnitudes de velocidades
            if(hasCollision(escape)){
                // Necesito calcular en función de la vei
                p.setVx(p.getVx() + escape[0]);
                p.setVy(p.getVy() + escape[1]); //TODO: Check de paranoia, pero ver si el vector escape tiene seteado
                                    // el versor * la magnitud o solo el versor
            }
            else{
                // NO tiene colisión => Uso vdi
                double vd_magnitude = MAX_DESIRED_VEL * Math.pow(
                        (p.getRadius() - R_MIN)/(R_MAX - R_MIN), BETA
                );
                double[] vd_norm = Utils.norm(new double[]{p.getX(), p.getY()},
                        new double[]{p.getTarget_x(), p.getTarget_y()});
                p.setVx(vd_norm[0] * vd_magnitude);
                p.setVy(vd_norm[1] * vd_magnitude);
            }
        }
        Particle p = particles.get(0);
        System.out.println("Partícula 1: " + p.getX() + ", " + p.getY());
        updateAllParticles();
    }

    public void updateAllParticles(){
        double old_x = particles.get(0).getX();
        for(Particle p : particles){
            double new_x = p.getX() + (p.getVx() * time_step);
            double new_y = p.getY() + (p.getVy() * time_step);
            p.setX(new_x);
            p.setY(new_y);
        }
        Particle p = particles.get(0);
        System.out.println("Cambio " + old_x + " por " + p.getX());
    }

    public boolean hasCollision(double[] escape){
        return escape[0] != 0 || escape[1] != 0;
    }

    public Particle createParticleForWall(Wall w, Particle ref){
        /*
        "In the case of interaction with a wall, x j indicates the nearest
        point on the wall to the center of particle p i ."

        A juzgar por esta parte del paper, la velocidad de escape con una pared se debe simular con
        el punto mas cercano al centro de la partícula. Por ende, va a ser en línea recta del centro
        hasta la pared. Se crea esta partícula auxiliar para simular la colisión y no reescribir todo,
        pero es lo mismo.

        TODO: Acá hay algo lógico que no llegue a pensar. Cuando una partícula choca con una pared,
        al instante siguiente se pone la velocidad correcta, que está bien. El problema es que se
        queda en un loop donde vuelve a tirar hacia la pared un instante después.
         */
        switch(w){
            case UP:
                return new Particle(ref.getX(), ROOM_TOTAL_HEIGHT, 0,0 ,0);
            case DOWN:
                return new Particle(ref.getX(), ROOM_OFFSET_Y, 0,0 ,0);
            case LEFT:
                return new Particle(0, ref.getY(), 0,0 ,0);
            case RIGHT:
                return new Particle(ROOM_WIDTH, ref.getY(), 0,0 ,0);
        }
        return null;
    }

    public List<Wall> collideWithWalls(Particle p){
        List<Wall> walls = new ArrayList<>();
        if(p.getX() + p.getRadius() > ROOM_WIDTH){
            walls.add(RIGHT);
        }
        if(p.getX() - p.getRadius() < 0){
            walls.add(LEFT);
        }
        if(p.getY() + p.getRadius() > (ROOM_HEIGHT + ROOM_OFFSET_Y)){
            walls.add(UP);
        }
        if(p.getY() - p.getRadius() < (0 + ROOM_OFFSET_Y)){ //Dejo el 0 por claridad de lo q hace (removible)
            walls.add(DOWN);
        }
        return walls;
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
            double aux_x = (Math.random()*ROOM_WIDTH);
            double aux_y = (Math.random()*ROOM_HEIGHT) + ROOM_OFFSET_Y;
            while(collides(aux_x, aux_y)){
                aux_x = (Math.random()*ROOM_WIDTH);
                aux_y = (Math.random()*ROOM_HEIGHT) + ROOM_OFFSET_Y;
            }
            Particle p = new Particle(aux_x, aux_y, 0, 0, R_MIN);
            double target_x = (aux_x < target_d_x1 + 0.2*target_d) ||
                    (aux_x > target_d_x1 + 0.8*target_d) ?
                    getRandomRange(target_d_x1 + 0.2*target_d, target_d_x1 + 0.8*target_d): aux_x;
            System.out.println("Target: " + target_x + ", " + ROOM_OFFSET_Y);
            p.setTarget_x(target_x);
            p.setTarget_y(ROOM_OFFSET_Y);
            particles.add(p);
        }
    }

    // La verdad es que lo podemos refactorear, me dio paja pensar en la función general
    private boolean collides(double x, double y){
        for(Particle p : particles){
            double distance = Math.sqrt(Math.pow(x - p.getX(), 2) + Math.pow(y - p.getY(), 2));
            if(distance <= 2*R_MIN){
                System.out.println("Particle p " + p + " collides with " + x + ", " + y);
                return true;
            }
        }
        return false;
    }

    private boolean collides(Particle p1, Particle p2){
        double distance = Math.sqrt(Math.pow(p2.getX() - p1.getX(), 2) + Math.pow(p2.getY() - p1.getY(), 2));
        return distance <= (p1.getRadius() + p2.getRadius());
    }

    public double getRandomRange(double min, double max){
        return (Math.random() * (max-min)) + min;
    }

    public List<Particle> getParticles(){
        return this.particles;
    }


}