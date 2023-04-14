package models;

public class Collision implements Comparable<Collision> {

    /* Nota: Siguiendo la sugerencia de la bibliografía, se decidió implementar la clase "collision"
    con la siguiente lógica:
        1) Si idx1 != -1 e idx != -1, se trata de una colisión entre 2 particulas
        2) Si idx1 != -1 e idx2 == -1, se trata de una colisión con una pared vertical
        3) Si idx1 == -1 e idx2 != -1, se trata de una colisión con una parted horizontal
        (Si ambas son -1 habla de "redraw" pero no se que sería)
     */
    private int idx1;
    private int collision_n_1;

    private int idx2;
    private int collision_n_2;

    private double t;

    public Collision(int idx1, int collision_n_1, int idx2, int collision_n_2, double t) {
        this.idx1 = idx1;
        this.collision_n_1 = collision_n_1;
        this.idx2 = idx2;
        this.collision_n_2 = collision_n_2;
        this.t = t;
    }

    public int getIdx1() {
        return idx1;
    }

    public int getIdx2() {
        return idx2;
    }

    public double getT() {
        return t;
    }

    public void elapse(double time) {
        this.t -= time;
    }

    public boolean isValidCollision(int collision_n_1, int collision_n_2){
        return (this.collision_n_1 == collision_n_1 || this.collision_n_1 == -1)
                && (this.collision_n_2 == collision_n_2 || this.collision_n_2 == -1)
                && (collision_n_1 != -1 || collision_n_2 != -1);
        // Revisar esta condición: SE pensó así:
        // Si las colisiones de 1 son -1, es una pared, no hay que chequear. Si no es -1,
        // hay que chequear que, antes de la colisión que estoy evaluando, no se haya producido
        // otra colisión de las partículas involucradas (misma lógica para partícula 2).
        // La última condición estaría atajando un absurdo donde ambas sean paredes.
    }
    public boolean isPocket(){
        return (this.idx1 >= 0 && this.idx1 <= 5) || (this.idx2 >= 0 && this.idx2 <= 5);
    }


    @Override
    public int compareTo(Collision o) { //para la PriorityQueue
        int toRet = 0;
        if(this.t > o.t)
            toRet = 1;
        else if(o.t > this.t)
            toRet = -1;
        return toRet;
    }


}
