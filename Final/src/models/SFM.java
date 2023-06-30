package models;

import java.util.List;

public class SFM implements PedestrianModel {

    private Room room;

    public SFM(double d, Room room) {
        this.room = room;
    }


    @Override
    public void calculateTarget(double d) {

    }

    @Override
    public void iterate() {

    }

    @Override
    public double getTimeElapsed() {
        return 0;
    }

    @Override
    public List<Particle> getParticles() {
        return null;
    }

    @Override
    public List<Particle> getCorners() {
        return null;
    }

    @Override
    public int getRemainingParticles() {
        return 0;
    }
}
