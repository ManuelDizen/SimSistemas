package models;

import utils.GearPredictorCorrector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static models.Wall.*;

public class SFM implements PedestrianModel {

    private Room room;

    private double timeElapsed;

    private final static double timeStep = 0.0001;
    private static double desiredSpeed;

    private static double target_d;
    private static double target_d_x1;
    private static double target_d_x2;

    private static final int GEAR_DELTA_T = -4;

    private static final double TAU = 0.5;

    private static final double Ai = 2000;

    private static final double Bi = 0.08;

    private static final double Kn = 120000;

    private static final double Kt = 240000;

    private GearPredictorCorrector gear;


    private final List<Particle> particles;
    private final List<Particle> corners;

    /*
    SFM: Social Force Model, modela la interacción de los peatones a medida que se caminan hacia un objetivo usando tres fuerzas:
    Desire Force, Social Force y Granular Force.
    Integramos usando Gear Predictor-Corrector de orden 4.
     */

    public SFM(double d, double v, Room room) {
        this.room = room;
        desiredSpeed = v;
        this.particles = room.getParticles();
        this.corners = room.getCorners();;
        this.gear = new GearPredictorCorrector(Math.pow(10, GEAR_DELTA_T));
        target_d = d;
        target_d_x1 = ((double) room.getWidth() /2) - (target_d/2);
        target_d_x2 = ((double) room.getWidth() /2) + (target_d/2);
        setInitials();
        timeElapsed = 0;
    }

    private void setInitials() {
        //Particle p;
        //p = new Particle(target_d_x1, room.getOffsetY(), 0,0, 0);
        //corners.add(p);
        //p = new Particle(target_d_x2, room.getOffsetY(), 0,0,0);
        //corners.add(p);
        for(Particle q : particles) {
            calculateTarget(target_d, q); //la forma en la que se eligen los targets es específico del SFM, así que se hace desde acá
            setInitialVelocity(q); //la implementación de Room no contempla velocidades, se ponen desde acá
            setInitialDerivs(q); //para el gear
        }
    }
    @Override
    public void calculateTarget(double d, Particle p) {
        if(p.getX() < (target_d_x1+0.5))
            p.setTarget_x(target_d_x1+0.5);
        else if(p.getX() > (target_d_x2-0.5))
            p.setTarget_x(target_d_x2-0.5);
        else
            p.setTarget_x(p.getX());
    }

    private void setInitialVelocity(Particle p) {
        double[] norm = Utils.norm(new double[]{p.getX(), p.getY()}, new double[]{p.getTarget_x(), p.getTarget_y()});
        p.setVx(norm[0]*Math.sqrt(desiredSpeed));
        p.setVy(norm[1]*Math.sqrt(desiredSpeed));
    }

    private void setInitialDerivs(Particle p) {
        gear.setInitialDerivs(p);
    }

    @Override
    public void iterate() {
        for(int i=0; i<particles.size(); i++) {
            Particle p = particles.get(i);
            //Predicción de fuerza
            Double[][] preds = gear.getPredictions(p.getDerivsX(), p.getDerivsY());
            p.accumForce(desireForce(p, preds[0][0], preds[1][0], preds[0][1], preds[1][1])); //Acumulo la Desire Force
            predictWithWalls(p, preds); //Acumulo la Social Force (y Granular Force si chocara con la pared) de las paredes
            Double[] forceP;
            for(int j = i; j < particles.size(); j++) {
                //Comparo a la partícula p con todas las que tengan un idx mayor
                Particle q = particles.get(j);
                if(q.getIdx() != p.getIdx()) {
                    forceP = socialForce(preds[0][0], preds[1][0], p.getRadius(), q.getX(), q.getY(), q.getRadius()); //Acumulo Social Force con
                                                                                                                    //todas las demás partículas
                    if(Utils.collides(p, q)) {
                        //si hay choque, acumulo Granular Force
                        Double[] forceG = granularForce(preds[0][0], preds[1][0], new double[]{preds[0][1], preds[1][1]}, p.getRadius(), q.getX(), q.getY(), new double[]{q.getVx(), q.getVy()}, q.getRadius());
                        forceP[0] += forceG[0];
                        forceP[1] += forceG[1];
                    }
                    p.accumForce(forceP);
                    q.accumForce(new Double[]{-forceP[0], -forceP[1]}); //como no se vuelve a comparar el par de partículas, tengo que almacenar
                                                                        //la fuerza contraria en q
                }
            }
            correctForce(p, preds); //Corregimos
            p.resetForce(); //Dejo en 0 para la siguiente iteración
        }
        checkForDoor();
        timeElapsed += timeStep;
    }

    private void correctForce(Particle p, Double[][] preds) {
        double[] calcAcc = new double[]{p.getForce()[0]/p.getMass(), p.getForce()[1]/p.getMass()};
        Double[] deltaR2 = {gear.calculateDeltaR2(calcAcc[0], preds[0][2]), gear.calculateDeltaR2(calcAcc[1], preds[1][2])};
        p.setDerivsX(gear.correctPredictions(preds[0], deltaR2[0])); //corrijo y seteo las derivs para la próxima iteración
        p.setDerivsY(gear.correctPredictions(preds[1], deltaR2[1]));
        p.setParameters(); //dejo la nueva posición y velocidad
    }

    private boolean inDoor(double x) {
        return (x >= target_d_x1 && x <= target_d_x2);
    }

    private void predictWithWalls(Particle p, Double[][] preds) {
        if(!inDoor(preds[0][0]))
            p.accumForce(socialForce(preds[0][0], preds[1][0], p.getRadius(), preds[0][0], room.getOffsetY(), 0));//PARED INFERIOR
        p.accumForce(socialForce(preds[0][0], preds[1][0], p.getRadius(), preds[0][0], room.getTotalHeight(), 0));//PARED SUPERIOR
        p.accumForce(socialForce(preds[0][0], preds[1][0], p.getRadius(), 0, preds[1][0], 0));//PARED LATERAL IZQUIERDA
        p.accumForce(socialForce(preds[0][0], preds[1][0], p.getRadius(), room.getWidth(), preds[1][0], 0));//PARED LATERAL DERECHA
        collideWithWalls(p, preds);
    }

    private void collideWithWalls(Particle p, Double[][] preds){
        if(preds[0][0] + p.getRadius() > room.getWidth()){
           p.accumForce(granularForce(preds[0][0], preds[1][0], new double[]{preds[0][1], preds[1][1]}, p.getRadius(), room.getWidth(), preds[1][0], new double[]{0.0, 0.0}, 0));
        }
        if(preds[0][0] - p.getRadius() < 0){
            p.accumForce(granularForce(preds[0][0], preds[1][0], new double[]{preds[0][1], preds[1][1]}, p.getRadius(), 0, preds[1][0], new double[]{0.0, 0.0}, 0));
        }
        if(preds[1][0] + p.getRadius() > (room.getTotalHeight())){
            p.accumForce(granularForce(preds[0][0], preds[1][0], new double[]{preds[0][1], preds[1][1]}, p.getRadius(), preds[0][0], room.getTotalHeight(), new double[]{0.0, 0.0}, 0));
        }
        if(!inDoor(preds[0][0]) && preds[1][0] - p.getRadius() < (0 + room.getOffsetY()) && p.getTarget_y() == room.getOffsetY()){ //Dejo el 0 por claridad de lo q hace (removible)
            p.accumForce(granularForce(preds[0][0], preds[1][0], new double[]{preds[0][1], preds[1][1]}, p.getRadius(), preds[0][0], room.getOffsetY(),new double[]{0.0, 0.0}, 0));
        }
    }

    //acá los mismos métodos, pero para el Gear Predictor-Corrector
    private Double[] desireForce(Particle p, double x, double y, double vx, double vy) {
        /*
        Fuerza deseada: simula el deseo del peatón de caminar hacia su objetivo
         */
        //calculo vector de movimiento
        double[] norm = Utils.norm(new double[]{x, y}, new double[]{p.getTarget_x(), p.getTarget_y()});
        //calculo la fuerza
        double fx = p.getMass()*(desiredSpeed*norm[0]-vx)/TAU;
        double fy = p.getMass()*(desiredSpeed*norm[1]-vy)/TAU;
        return new Double[]{fx, fy};
    }

    private Double[] socialForce(double pX, double pY, double pR, double qX, double qY, double qR) {
        /*
        Fuerza Social: fuerza que simula como los peatones afectan su trayectoria ante la presencia de otros
        Las partículas se repelen entre ellas, mostrando una tendencia a caminar por donde no hay gente
         */
        double rij = pR + qR;
        double dij = Utils.magnitude(new double[]{pX-qX, pY-qY}); //distancia entre centros
        double exponent = (rij-dij)/Bi; // -(eps ij) = -((dij-rij)/Bi) = (rij-dij)/Bi
        double factor = Ai*Math.exp(exponent);
        //calculo dirección de la fuerza -> partícula q repele a partícula p
        double[] nij = Utils.norm(new double[]{qX, qY}, new double[]{pX, pY});
        return new Double[]{factor*nij[0], factor*nij[1]};
    }


    private Double[] granularForce(double pX, double pY, double[] pV, double pR,
                                   double qX, double qY, double[] qV, double qR) { //podría pasarle directamente el vector derivs, pero así es más legible

        double rij = pR + qR;
        double dij = Utils.magnitude(new double[]{pX-qX, pY-qY});
        double[] nij = Utils.norm(new double[]{qX, qY}, new double[]{pX, pY});
        double[] tij = new double[]{-nij[1], nij[0]}; //vector tangencial
        double tangentialVelocity = Utils.tangential(pV, qV, tij);
        double factorBody = Kn*(rij-dij);
        double factorSliding = Kt*(rij-dij)*tangentialVelocity;
        double[] bodyForce = new double[]{factorBody*nij[0], factorBody*nij[1]};
        double[] slidingForce = new double[]{factorSliding*tij[0], factorSliding*tij[1]};
        return new Double[]{bodyForce[0]+slidingForce[0], bodyForce[1]+slidingForce[1]};
    }

    @Override
    public double getTimeElapsed() {
        return timeElapsed;
    }

    @Override
    public List<Particle> getParticles() {
        return this.particles;
    }

    @Override
    public List<Particle> getCorners() {
        return this.corners;
    }

    @Override
    public int getRemainingParticles() {
        int count = 0;
        for(Particle p : particles){
            if(p.getTarget_y() == room.getOffsetY()){
                count++;
            }
        }
        return count;
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
}
