package models;

import java.util.List;

public class SFM implements PedestrianModel {

    private static int N_PARTICLES;



    private Room room;

    public SFM(int N, double d, Room room) {
        N_PARTICLES = N;
        this.room = room;
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
