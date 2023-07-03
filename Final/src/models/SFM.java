package models;

import utils.GearPredictorCorrector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static models.Wall.*;

public class SFM implements PedestrianModel {

    private Room room;

    private double timeElapsed;

    private final static double timeStep = 0.001;
    private static double desiredSpeed;

    private static double target_d;
    private static double target_d_x1;
    private static double target_d_x2;

    private static final int GEAR_DELTA_T = -3;

    private static final double TAU = 0.5;

    private static final double Ai = 2000;

    private static final double Bi = 0.08;

    private static final double Kn = 120000;

    private static final double Kt = 240000;

    private GearPredictorCorrector gear;


    private final List<Particle> particles;
    private final List<Particle> corners;

    public SFM(double d, double v, Room room) {
        this.room = room;
        desiredSpeed = v;
        this.particles = room.getParticles();
        this.corners = room.getCorners();
        this.gear = new GearPredictorCorrector(Math.pow(10, GEAR_DELTA_T));
        target_d = d; // Discutiendo con los chicos, la figura del ejercicio 2 solo se toma si hacemos SFM
        target_d_x1 = ((double) room.getWidth() /2) - (target_d/2);
        target_d_x2 = ((double) room.getWidth() /2) + (target_d/2);
        setInitials();
        timeElapsed = 0;
    }

    private void setInitials() {
        for(Particle p : particles) {
            calculateTarget(target_d, p);
            setInitialVelocity(p);
            setInitialDerivs(p);
        }
    }
    @Override
    public void calculateTarget(double d, Particle p) {
        if(p.getX() < (target_d_x1+0.1))
            p.setTarget_x(target_d_x1+0.1);
        else if(p.getX() > (target_d_x2-0.1))
            p.setTarget_x(target_d_x2-0.1);
        else
            p.setTarget_x(p.getX());
    }

    private void setInitialVelocity(Particle p) {
        double[] norm = Utils.norm(new double[]{p.getX(), p.getY()}, new double[]{p.getTarget_x(), p.getTarget_y()});
        //System.out.println("norm: " + norm[0] + " " + norm[1]);
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
            //System.out.println("particle " + p.getIdx() + ": (" + p.getX() + ", " + p.getY() + ") ->" + p.getVx() + " - " + p.getVy() );
            Double[][] preds = gear.getPredictions(p.getDerivsX(), p.getDerivsY());
            p.accumForce(desireForce(p, preds[0][0], preds[1][0], preds[0][1], preds[1][1]));
            predictWithWalls(p, preds);
            Double[] forceP;
            for(int j = p.getIdx(); j < particles.size(); j++) {
                Particle q = particles.get(j);
                if(q.getIdx() != p.getIdx()) {
                    forceP = socialForce(preds[0][0], preds[1][0], p.getRadius(), q.getX(), q.getY(), q.getRadius());
                    if(Utils.collides(p, q)) {
                        Double[] forceG = granularForce(preds[0][0], preds[1][0], new double[]{preds[0][1], preds[1][1]}, p.getRadius(), q.getX(), q.getY(), new double[]{q.getVx(), q.getVy()}, q.getRadius());
                        forceP[0] += forceG[0];
                        forceP[1] += forceG[1];
                    }
                    p.accumForce(forceP);
                    q.accumForce(new Double[]{-forceP[0], -forceP[1]});
                }
            }
            correctForce(p, preds);
            //System.out.println("force: " + p.getForce()[0] + " " + p.getForce()[1]);
            p.resetForce();
        }
        checkForDoor();
        timeElapsed += timeStep;
    }

    private void correctForce(Particle p, Double[][] preds) {
        double[] calcAcc = new double[]{p.getForce()[0]/p.getMass(), p.getForce()[1]/p.getMass()};
        Double[] deltaR2 = {gear.calculateDeltaR2(calcAcc[0], preds[0][2]), gear.calculateDeltaR2(calcAcc[1], preds[1][2])};
        p.setDerivsX(gear.correctPredictions(preds[0], deltaR2[0])); //corrijo y seteo las derivs para la próxima iteración
        p.setDerivsY(gear.correctPredictions(preds[1], deltaR2[1]));
        p.setParameters();
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

    //Social Force Model suma tres fuerzas: desire, social y granular
    //acá los métodos originales para el cálculo de fuerzas
    private Double[] desireForce(Particle p) {
        double[] norm = Utils.norm(new double[]{p.getX(), p.getY()}, new double[]{p.getTarget_x(), p.getTarget_y()});
        //System.out.println("norm: " + norm[0] + " " + norm[1]);
        double fx = p.getMass()*(desiredSpeed*norm[0]-p.getVx())/TAU;
        double fy = p.getMass()*(desiredSpeed*norm[1]-p.getVy())/TAU;
        return new Double[]{fx, fy};
    }

    private Double[] socialForce(Particle p, Particle q) {
        double rij = p.getRadius() + q.getRadius();
        double dij = Utils.magnitude(p, q);
        double exponent = (rij-dij)/Bi;
        double factor = Ai*Math.exp(exponent);
        double[] nij = Utils.norm(q, p);
        return new Double[]{factor*nij[0], factor*nij[1]};
    }

    private Double[] granularForce(Particle p, Particle q) {
        double rij = p.getRadius() + q.getRadius();
        double dij = Utils.magnitude(p, q);
        double[] nij = Utils.norm(q, p);
        double[] tij = new double[]{-nij[1], nij[0]};
        //System.out.println("nij " +  nij[0] + " " + nij[1]);
        //System.out.println("tij " +  tij[0] + " " + tij[1]);
        double tangentialVelocity = Utils.tangential(p, q, tij);
        //System.out.println("tangVel " + tangentialVelocity);
        double factorBody = Kn*(rij-dij);
        double factorSliding = Kt*(rij-dij)*tangentialVelocity;
        double[] bodyForce = new double[]{factorBody*nij[0], factorBody*nij[1]};
        double[] slidingForce = new double[]{factorSliding*tij[0], factorSliding*tij[1]};
        return new Double[]{bodyForce[0]+slidingForce[0], bodyForce[1]+slidingForce[1]};
    }

    //acá los mismos métodos, pero para el Gear Predictor-Corrector
    private Double[] desireForce(Particle p, double x, double y, double vx, double vy) {
        double[] norm = Utils.norm(new double[]{x, y}, new double[]{p.getTarget_x(), p.getTarget_y()});
        //System.out.println("norm: " + norm[0] + " " + norm[1]);
        double fx = p.getMass()*(desiredSpeed*norm[0]-vx)/TAU;
        double fy = p.getMass()*(desiredSpeed*norm[1]-vy)/TAU;
        //System.out.println("desireForce " + fx + " " + fy);
        return new Double[]{fx, fy};
    }

    private Double[] socialForce(double pX, double pY, double pR, double qX, double qY, double qR) {
        double rij = pR + qR;
        double dij = Utils.magnitude(new double[]{pX-qX, pY-qY});
        double exponent = (rij-dij)/Bi;
        double factor = Ai*Math.exp(exponent);
        double[] nij = Utils.norm(new double[]{qX, qY}, new double[]{pX, pY});
        return new Double[]{factor*nij[0], factor*nij[1]};
    }

    private Double[] granularForce(double pX, double pY, double[] pV, double pR,
                                   double qX, double qY, double[] qV, double qR) { //podría pasarle directamente el vector derivs, pero así es más legible
        double rij = pR + qR;
        double dij = Utils.magnitude(new double[]{pX-qX, pY-qY});
        double[] nij = Utils.norm(new double[]{qX, qY}, new double[]{pX, pY});
        double[] tij = new double[]{-nij[1], nij[0]};
        //System.out.println("nij " +  nij[0] + " " + nij[1]);
        //System.out.println("tij " +  tij[0] + " " + tij[1]);
        double tangentialVelocity = Utils.tangential(pV, qV, tij);
        //System.out.println("tangVel " + tangentialVelocity);
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
