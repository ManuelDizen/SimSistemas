package IntegrationMethods;

import System1.DampedOscillator;
import utils.Pair;
import utils.Particle;

public class VerletOriginal implements IntegrationMethod
{
    private final double delta_t;
    private final DampedOscillator oscillator = new DampedOscillator();

    public VerletOriginal(double delta_t){this.delta_t = delta_t;}
    @Override
    public Pair<Double> updateParams(Particle p) {
        double curr_x = p.getX();
        double mass = p.getMass();

        double curr_force = oscillator.calculateForce(curr_x, p.getVx()); // TODO: Acá tengo un problema,
                                                    // ¿como calculo Vx actual? esta en función de la pos
                                                    // siguiente. ¿Problema circular?
                                                    // Toy cansado pero es una cuestión de entender teoría igual
        double next_x = (2*curr_x) - (p.getPrev_x()) +
                ((Math.pow(delta_t, 2) / mass) * curr_force);

        double curr_vx = (next_x + p.getPrev_x()) / (2*delta_t);

        p.setPrev_x(curr_x);
        p.setPrev_vx(curr_vx);
        p.setX(next_x);
        // ¿¿¿ p.setVx(¿?) ???

        return new Pair<>(next_x, curr_vx); // Siguiendo comentario de arriba, devuelvo nueva pos
        // (en t + delta_t) con v en t. (No entiendo como hacer para devolver la v de t + delta_t
    }
}
