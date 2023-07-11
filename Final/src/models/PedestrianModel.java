package models;

import java.util.List;

public interface PedestrianModel {

    public void calculateTarget(double d, Particle p);

    public void iterate();

    public double getTimeElapsed();

    public List<Particle> getParticles();

    public List<Particle> getCorners();

    public int getRemainingParticles();


}
