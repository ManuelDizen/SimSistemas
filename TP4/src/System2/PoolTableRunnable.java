package System2;

public class PoolTableRunnable {
    // Según enunciado, en punto 1 se varía el delta_t unicamente
    // mientras que en el 2 se varía la posición inicial de la bola tmb

    private static PoolTable table;
    public static final double delta_t = -2;

    public static final double k = Math.pow(10, 4);

    private static final double total_time = 100;

    public static void main(String[] args) {
        double initial_y = Double.parseDouble(args[0]);
        table = new PoolTable(initial_y, delta_t);
        boolean buchacas = args[1] != null && args[1].equals("buchacas");
        if(buchacas) {
            System.out.println("Corro con buchacas!\n");
            runWithBuchacas();
        }
        else {
            System.out.println("Corro sin buchacas con delta_t " + delta_t + "\n");
            runWithoutBuchacas(delta_t);
        }
    }

    public static void runWithBuchacas(){
        // Tiene que correr y simular mientras queden pelotas
        // Poner un boolean para saber si es con la mitad o no
    }

    public static void runWithoutBuchacas(double delta_t){
        /*
        1) Creo tabla
        2) Creo partículas SIN buchacas
        3) Itero hasta que el tiempo termina
         */
        table.generateParticlesNoBuchacas();
        table.calculateInitialCollisions();

        double elapsed_time = 0;
        // Vamos a tener un boolean que indique si necesitamos
        // buscar una proxima colisión
        Collision current_collision = null;
        while(elapsed_time < total_time){
            if(current_collision == null){
                current_collision = table.getNextCollision();
            }
            if(elapsed_time + delta_t < current_collision.getT()) {
                // TODO: Rehacer método para actualizar las condiciones de partícula con
                // método de integració gear. Por abajo, es solo actualizar con MRUV pero
                // habría que hacerlo independiente de las condiciones
                // Esto quiere decir que el updateAllParticles debería actualizar los parámetros de gear nomas
                // y no valerse de la definición de MRUV (entiendo eso)

                // table.updateAllParticles(delta_t);
            }
            else{
                /*
                TODO: Se produjo la colisión: Hay que actualizar los valores calculando
                la fuerza con la formula dada. Mientras no se produce esto, es un mero MRUV
                (Igual habría que cambiar el updateAllParticles para que se comporte con las formulas
                de gear esperadas.

                El tema ahora es calcular cuando hay una colisión. Dos formas rápidas:
                1) CIM (paja)
                2) Brute Force (ineficiente, no paja)

                Cualquiera sea de las dos, hay que implementar cuando |ri - rj| < Ri + Rj
                En caso de la pared, la fuerza es la misma (le pregunte a Germán) solo que
                la condición de corte sería algo como rix >= MAX_X o riy >= MAX_Y
                 */
                // table. makeCollision();
                current_collision = null;
            }

            elapsed_time += delta_t;
        }

        /* Tenemos r0 de todas las particulas, v0 de todas las partículas,
        y podemos calcular la fuerza entre dos partículas cualquiera
         */

    }

}
