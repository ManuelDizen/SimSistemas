package IntegrationMethods;

import utils.Pair;
import utils.Particle;

public interface IntegrationMethod {
    Pair<Double> updateParams(Particle p);
}
