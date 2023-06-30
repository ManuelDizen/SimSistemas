package models;

import java.util.List;

public class SFM implements PedestrianModel {

    private Room room;

    private static double desiredSpeed;

    private static double target_d;
    private static double target_d_x1;
    private static double target_d_x2;

    private static final double MASS = 80; //kg

    private static final double TAU = 0.5;

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
        double fx = MASS*(desiredSpeed*norm[0]-p.getVx())/TAU;
        double fy = MASS*(desiredSpeed*norm[1]-p.getVy())/TAU;
        return new double[]{fx, fy};
    }

    @Override
    public void iterate() {
        for(Particle p: particles) {
            System.out.println("Particle " + p.getIdx() + " - (" + p.getX() + ", " + p.getY() + ")");
            double[] f = desireForce(p);
            System.out.println("Desired force: " + f[0] + ", " + f[1]);
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
