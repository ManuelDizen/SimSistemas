package models;

import java.util.ArrayList;
import java.util.List;

import static models.Wall.*;

public class CPM implements PedestrianModel{

    /*
   Los valores de estos parámetros fijos son tomados a partir del paper.

   Se tomará el dataset que dice "set of parameters 1" con la excepción que se
   aclara que vd max debe ser 2 en la consigna. TODO: Mandar mail preguntando, en paper dice 1.55

    */
    private static double desiredSpeed; // 2m/s
    private static final double MAX_ESCAPE_VEL = desiredSpeed; // ve max = vd max (en paper)

    private static final double R_MIN = 0.10; // m
    private static final double R_MAX = 0.37;
    private static final double BETA = 0.9;

    private static final double TAU = 0.5; /*We choose τ = 0.5 s in accordance to the
                        value used in the desired force in the social force model*/
    private static double delta_r;
    private final List<Particle> particles;
    private final List<Particle> corners;

    private static double exit;
    private static double target_d;
    private static double target_d_x1;
    private static double target_d_x2;
    private static double time_step;
    private double time_elapsed;

    private Room room;

    public CPM(double d, double v, Room room){
        this.room = room;
        desiredSpeed = v;
        this.particles = room.getParticles();
        this.corners = room.getCorners();
        target_d = d; // Discutiendo con los chicos, la figura del ejercicio 2 solo se toma si hacemos SFM
        target_d_x1 = ((double) room.getWidth() /2) - (target_d/2);
        target_d_x2 = ((double) room.getWidth() /2) + (target_d/2);
        for(Particle p : particles) {
            calculateTarget(d, p);
        }
        time_step = R_MIN / (2*desiredSpeed); /*VelDMAX == Vesc MAX
                (So, we choose for the model a fixed value of the escape speed v e = v d max .) */
        delta_r = (R_MAX - R_MIN)/(TAU / time_step);
        System.out.println("Medidas:\ntarget_d_x1= " + target_d_x1 + "\ntarget_d_x2= " + target_d_x2 +
                "\ntarget_d= " + target_d);
        time_elapsed = 0;
    }

    @Override
    public void calculateTarget(double d, Particle p) {
        double target_x = (p.getX() < target_d_x1 + 0.2*d) ||
                (p.getX() > target_d_x1 + 0.8*d) ?
                getRandomRange(target_d_x1 + 0.2*d, target_d_x1 + 0.8*d): p.getX();
        p.setTarget_x(target_x);
    }

    private double getRandomRange(double min, double max){
        return (Math.random() * (max-min)) + min;
    }


    private double[] calculateEscape(Particle p1, Particle p2){
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
                if(p!=q && Utils.collides(p,q)){
                    double[] aux_escape = calculateEscape(q,p);
                    escape[0] += aux_escape[0];
                    escape[1] += aux_escape[1];
                }
            }
            List<Wall> walls = collideWithWalls(p);
            if(walls.size() != 0){
                /* Colisiona con paredes */
                for(Wall w : walls){
                    /* Acá esta el caso especial donde wall es DOWN pero tenes en la pared,
                    por eso no sale ninguna*/
                    if(!((w.equals(Wall.DOWN) || w.equals(Wall.DOWN_END))
                            && p.getX() <=target_d_x2 && p.getX() >= target_d_x1)) {
                        Particle aux_p = createParticleForWall(w, p);
                        double[] aux_escape = calculateEscape(aux_p, p);
                        escape[0] += aux_escape[0];
                        escape[1] += aux_escape[1];
                    }
                }
            }


            // 2. Ajusto radios de acuerdo a si colisiono o no
            if(!hasCollision(escape)){
                double adding_radius = (R_MAX - R_MIN)/(TAU / time_step);
                if(p.getRadius() + adding_radius >= R_MAX){
                    p.setNextRadius(R_MAX);
                }
                else{
                    p.setNextRadius(p.getRadius() + adding_radius);
                }
            }
            else{
                p.setNextRadius(R_MIN); // COlisiono con algo => Vuelve a estado 0
            }

            // 3. Calculo direcciones y magnitudes de velocidades
            if(hasCollision(escape)){
                // Necesito calcular en función de la vei
                p.setVx(escape[0]);
                p.setVy(escape[1]);
            }
            else{
                // NO tiene colisión => Uso vdi
                double vd_magnitude = desiredSpeed * Math.pow(
                        (p.getNextRadius() - R_MIN)/(R_MAX - R_MIN), BETA
                );
                double[] vd_norm = Utils.norm(new double[]{p.getX(), p.getY()},
                        new double[]{p.getTarget_x(), p.getTarget_y()});
                p.setVx(vd_norm[0] * vd_magnitude);
                p.setVy(vd_norm[1] * vd_magnitude);
            }

        }
        time_elapsed += time_step;
        updateAllParticles();
        checkForDoor();
    }

    private void checkForDoor(){
        List<Particle> toRemove = new ArrayList<>();
        for(Particle p : particles){
            if(p.getY() - p.getRadius() <= 0){
                toRemove.add(p);
            }
            else if(p.getTarget_y() != 0 && p.getY() < room.getOffsetY()){
                p.setTarget_y(0);
            }
        }
        particles.removeAll(toRemove);
    }

    private void updateAllParticles(){
        for(Particle p : particles){
            double new_x = p.getX() + (p.getVx() * time_step);
            double new_y = p.getY() + (p.getVy() * time_step);
            p.setX(new_x);
            p.setY(new_y);
            p.setRadius(p.getNextRadius());
        }
    }

    private boolean hasCollision(double[] escape){
        return escape[0] != 0 || escape[1] != 0;
    }

    private Particle createParticleForWall(Wall w, Particle ref){
        switch(w){
            case UP:
                return new Particle(ref.getX(), room.getTotalHeight(), 0,0 ,0);
            case DOWN:
                return new Particle(ref.getX(), room.getOffsetY(), 0,0 ,0);
            case LEFT:
                return new Particle(0, ref.getY(), 0,0 ,0);
            case RIGHT:
                return new Particle(room.getWidth(), ref.getY(), 0,0 ,0);
            case DOWN_END:
                return new Particle(ref.getX(), 0, 0, 0,0);
        }
        return null;
    }

    private List<Wall> collideWithWalls(Particle p){
        List<Wall> walls = new ArrayList<>();
        if(p.getX() + p.getRadius() > room.getWidth()){
            walls.add(RIGHT);
        }
        if(p.getX() - p.getRadius() < 0){
            walls.add(LEFT);
        }
        if(p.getY() + p.getRadius() > (room.getTotalHeight())){
            walls.add(UP);
        }
        if(p.getY() - p.getRadius() < (0 + room.getOffsetY()) && p.getTarget_y() == room.getOffsetY()){ //Dejo el 0 por claridad de lo q hace (removible)
            walls.add(DOWN);
        }
        if(p.getY() - p.getRadius() < 0){
            walls.add(DOWN_END);
        }
        return walls;
    }


    public double getTimeElapsed(){return this.time_elapsed;}
    public List<Particle> getParticles(){return this.particles;}
    public List<Particle> getCorners(){return this.corners;}
    public int getRemainingParticles(){
        int count = 0;
        for(Particle p : particles){
            if(p.getTarget_y() == room.getOffsetY()){
                count++;
            }
        }
        return count;
    }

}
