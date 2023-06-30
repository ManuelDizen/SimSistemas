package models;

import java.util.ArrayList;
import java.util.List;

public class Room {

    private static final double R_MIN = 0.10; // m

    private static final double R_MAX = 0.37;

    public int width;
    public int height;
    public int offsetY;
    public int totalHeight;

    public int n_particles;

    private final List<Particle> particles = new ArrayList<>();

    private final List<Particle> corners = new ArrayList<>();

    public Room(int n_particles, int width, int height, int offsetY) {
        this.n_particles = n_particles;
        this.width = width;
        this.height = height;
        this.offsetY = offsetY;
        this.totalHeight = height + offsetY;
        createParticles();
    }

    public void createParticles(){
        for(int n = 0; n < n_particles; n++){
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
             */
            double aux_x = (Math.random()*width);
            double aux_y = (Math.random()* height) + offsetY;
            while(Utils.collides(particles, aux_x, aux_y, R_MIN)){
                aux_x = (Math.random()* width);
                aux_y = (Math.random()*height) + offsetY;
            }
            Particle p = new Particle(aux_x, aux_y, 0, 0, R_MIN);
            p.setTarget_y(offsetY);
            particles.add(p);
        }
        createCornerParticles();
    }

    private void createCornerParticles(){
        Particle p = new Particle(0,0,0,0,0);
        corners.add(p);
        p = new Particle(0, totalHeight,0,0,0);
        corners.add(p);
        p = new Particle(width, totalHeight, 0,0,0);
        corners.add(p);
        p = new Particle(width, 0,0,0,0);
        corners.add(p);
    }

    public int getNParticles() {
        return n_particles;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public int getTotalHeight() {
        return totalHeight;
    }

    public List<Particle> getParticles() {
        return particles;
    }

    public List<Particle> getCorners() {
        return corners;
    }
}