package IntegrationMethods;

import System1.DampedOscillator;
import utils.Pair;

public class VerletOriginal {

    /*
    Pequeña documentación:
    En slide 18 muestra lo siguiente para Verlet (recordar que este método funciona sin depender de la velocidad):
    1) Partimos de t - delta_t
    2) Avanzo r hasta t
    3) Uso r en t, y a en t - delta_t, para calcular a en t
    4) Ahora, puedo calcular t + delta_t iterativamente.


     */

    private double x;
    private double vx;
    private double delta_t;

    private double f;

    private double prevX; //Esto es una convención

    public VerletOriginal(double x, double vx, double f, double delta_t){
        this.x = x;
        this.vx = vx;
        this.delta_t = delta_t;

        double firstStep = vx + (delta_t * vx); // En otro contexto es vectorial, el oscilador se maneja en 1D
        this.f = f;
    }

    public double advancePosition() {
        return -1;
    }

    public void advanceVerlet(){
        /*
        ¿Que tengo que hacer? vamos a desmenuzar en orden.
        Para arrancar necesito r(t) y r(t - delta_t)
        Como el tiempo es 0, no existe el segundo término. Por lo que vamos a hacer esto,
        basandonos en el artículo https://es.wikipedia.org/wiki/Integraci%C3%B3n_de_Verlet#Algoritmo_de_Verlet._Integraci%C3%B3n

        Como no podemos determinar este tiempo, vamos a utilizar el método de Euler para el cálculo de esta posición
        en t + delta_t, únicamente en el primer paso (tratando al sistema de un movimiento rectilineo).
        (TODO: PREGUNTAR SI ESTÁ BIEN!!!)
         */

        double firstStep = vx + (delta_t * vx); // En otro contexto es vectorial, el oscilador se maneja en 1D

    }




}
