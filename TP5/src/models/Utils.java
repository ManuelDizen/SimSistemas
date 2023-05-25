package models;

public class Utils {

    public double magnitude(double[] vector){
        return Math.sqrt(Math.pow(vector[0], 2) + Math.pow(vector[1], 2));
    }

    public double[] norm(double[] v1, double[] v2){
        double xDiff = v2[0] - v1[0];
        double yDiff = v2[1] - v1[1];
        double mag = magnitude(new double[]{xDiff, yDiff});
        return new double[]{xDiff/mag, yDiff/mag};
    }

    public double[] calculateEscapeVelocity(double[] v1, double[] v2, double escMagnitude){
        double[] direction = norm(v1, v2);
        return new double[]{direction[0]*escMagnitude, direction[1]*escMagnitude};
    }


}
