package System2;
import utils.Particle;
public class PoolTableRunnable {
    private static PoolTable table;
    public static final double delta_t = -3;

    public static final double k = Math.pow(10, 4);

    private static final double total_time = 0.5;

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
            runWithoutBuchacas(Math.pow(10, delta_t));
        }
    }

    public static void runWithBuchacas(){
        // Tiene que correr y simular mientras queden pelotas
        // Poner un boolean para saber si es con la mitad o no
    }

    public static void runWithoutBuchacas(double delta_t){
        //table.generateParticlesNoBuchacas();
        table.generateSix();
        double elapsed_time = 0;
        while(elapsed_time < total_time){
            for(int i = 0; i < table.particles.size(); i++){
                //para cada partícula p, itero por el resto de las partículas a ver cuáles la están "influenciando"
                //acumulo las fuerzas para sacar la fuerza neta
                Particle p = table.particles.get(i);
                System.out.println("Particle " + p.getIdx() + ": " + p.getX() + ", " + p.getY() + " vel: " + p.getVx() + ", " + p.getVy() + " acc: " + p.getDerivsX()[2] + ", " + p.getDerivsY()[2]);
                boolean flag = false;
                Double[] forceP;
                for(int j = i + 1; j < table.particles.size(); j++) {
                    Particle q = table.particles.get(j);
                    if (p.getNorm(p.getX(), p.getY(), q.getX(), q.getY()) < (p.getRadius() + q.getRadius())) {
                        System.out.println("COLLISION!!!!!!!!!!!!!!!");
                        forceP = p.predictForce(q.getX(), q.getY());
                        p.accumForce(forceP[0], forceP[1]);
                        q.accumForce(-forceP[0], -forceP[1]);
                        flag = true;
                    }
                }
                System.out.println(p.hasAccumForce());
                if(flag || p.hasAccumForce()) {
                    p.applyUpdateWithForce(p.getForce());
                    p.resetForce();
                }
                else {
                    if(p.getX() >= table.LONG_SIDE || p.getX() <= 0){
                        System.out.println("vertical wall ");
                        p.applyBounceWithVerticalWall();
                    }
                    else if(p.getY() >= table.SHORT_SIDE || p.getY() <= 0){
                        System.out.println("horizontal wall ");
                        p.applyBounceWithHorizontalWall();
                    }
                    else {
                        // Update sin fuerza que calcular, usamos el MRUV con la aceleración que ya tenía
                        p.applyUpdateNoBounce();
                    }
                }
            }

            elapsed_time += delta_t;
        }
    }

}
