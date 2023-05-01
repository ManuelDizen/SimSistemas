package IntegrationMethods;

public class Euler {
    public static double calculateEulerR(double r, double v, double step, double mass, double f) {
        return r + step * v + step * step * f / (2 * mass);
    }
}
