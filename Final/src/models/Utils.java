package models;

import java.util.List;

public class Utils {

    public static double magnitude(double[] vector){
        return Math.sqrt(Math.pow(vector[0], 2) + Math.pow(vector[1], 2));
    }

    public static double magnitude(Particle p1, Particle p2){
        return magnitude(new double[]{p2.getX()-p1.getX(), p2.getY()-p1.getY()});
    }

    public static double[] norm(double[] v1, double[] v2){
        double xDiff = v2[0] - v1[0];
        double yDiff = v2[1] - v1[1];
        double mag = magnitude(new double[]{xDiff, yDiff});
        return new double[]{xDiff/mag, yDiff/mag};
    }

    public double[] calculateEscapeVelocity(double[] v1, double[] v2, double escMagnitude){
        double[] direction = norm(v1, v2);
        return new double[]{direction[0]*escMagnitude, direction[1]*escMagnitude};
    }

    // La verdad es que lo podemos refactorear, me dio paja pensar en la función general
    public static boolean collides(List<Particle> particles, double x, double y, double rMin){
        for(Particle p : particles){
            double distance = Math.sqrt(Math.pow(x - p.getX(), 2) + Math.pow(y - p.getY(), 2));
            if(distance <= 2*rMin){
                return true;
            }
        }
        return false;
    }

    public static boolean collides(Particle p1, Particle p2){
        double distance = Math.sqrt(Math.pow(p2.getX() - p1.getX(), 2) + Math.pow(p2.getY() - p1.getY(), 2));
        return distance <= (p1.getRadius() + p2.getRadius());
    }


}
