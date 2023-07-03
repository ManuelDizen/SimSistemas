package models;

import java.util.ArrayList;
import java.util.List;

public class SFM implements PedestrianModel {

    private Room room;

    private static double desiredSpeed;

    private static double target_d;
    private static double target_d_x1;
    private static double target_d_x2;

    private static final double MASS = 80; //kg

    private static final double TAU = 0.5;

    private static final double Ai = 2000;

    private static final double Bi = 0.08;

    private static final double Kn = 120000;

    private static final double Kt = 240000;

    private final List<Particle> particles;
    private final List<Particle> corners;

    public SFM(double d, double v, Room room) {
        this.room = room;
        desiredSpeed = v;
        this.particles = room.getParticles();
        this.corners = room.getCorners();
        calculateTarget(d);
    }


    @Override
    public void calculateTarget(double d) {
        target_d = d; // Discutiendo con los chicos, la figura del ejercicio 2 solo se toma si hacemos SFM
        target_d_x1 = ((double) room.getWidth() /2) - (target_d/2);
        target_d_x2 = ((double) room.getWidth() /2) + (target_d/2);
        for(Particle p : particles) {
            if(p.getX() < (target_d_x1+0.1))
                p.setTarget_x(target_d_x1+0.1);
            else if(p.getX() > (target_d_x2-0.1))
                p.setTarget_x(target_d_x2-0.1);
            else
                p.setTarget_x(p.getX());
        }
    }

    private double[] desireForce(Particle p) {
        double[] norm = Utils.norm(new double[]{p.getX(), p.getY()}, new double[]{p.getTarget_x(), p.getTarget_y()});
        System.out.println("norm: " + norm[0] + " " + norm[1]);
        double fx = MASS*(desiredSpeed*norm[0]-p.getVx())/TAU;
        double fy = MASS*(desiredSpeed*norm[1]-p.getVy())/TAU;
        return new double[]{fx, fy};
    }

    private double[] socialForce(Particle p, Particle q) {
        double rij = p.getRadius() + q.getRadius();
        double dij = Utils.magnitude(p, q);
        double exponent = (rij-dij)/Bi;
        double factor = Ai*Math.exp(exponent);
        double[] nij = Utils.norm(q, p);
        return new double[]{factor*nij[0], factor*nij[1]};
    }

    private double[] granularForce(Particle p, Particle q) {
        double rij = p.getRadius() + q.getRadius();
        double dij = Utils.magnitude(p, q);
        double[] nij = Utils.norm(q, p);
        double[] tij = new double[]{-nij[1], nij[0]};
        double tangentialVelocity = Utils.tangential(p, q, tij);
        double factorBody = Kn*(rij-dij);
        double factorSliding = Kt*(rij-dij)*tangentialVelocity;
        double[] bodyForce = new double[]{factorBody*nij[0], factorBody*nij[1]};
        double[] slidingForce = new double[]{factorSliding*tij[0], factorSliding*tij[1]};
        return new double[]{bodyForce[0]+slidingForce[0], bodyForce[1]+slidingForce[1]};
    }

    @Override
    public void iterate() {
        for(Particle p: particles) {
            System.out.println("Particle " + p.getIdx() + " - (" + p.getX() + ", " + p.getY() + ")");
        }

    }

    @Override
    public double getTimeElapsed() {
        return 0;
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
        return 0;
    }
}
