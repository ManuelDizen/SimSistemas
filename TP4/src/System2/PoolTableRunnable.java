package System2;
import utils.FileUtils;
import utils.Particle;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.exit;

public class PoolTableRunnable {
    private static PoolTable table;
    public static final double delta_t = -3;

    public static final double k = Math.pow(10, 4);

    private static final double total_time = 100;
    private static final boolean test_part1 = true;

    private static final List<Double> phi = new ArrayList<>((int)total_time);



    public static void main(String[] args) {
        double initial_y = Double.parseDouble(args[0]);
        table = new PoolTable(initial_y, delta_t);
        boolean buchacas = args[1] != null && args[1].equals("buchacas");
        if(buchacas) {
            System.out.println("Corro con buchacas!\n");
            runWithBuchacas(delta_t);
        }
        else {
            System.out.println("Corro sin buchacas con delta_t " + delta_t + "\n");
            if(test_part1){
                double[] delta_ts = {-2,-3,-4};
                for(double t : delta_ts){
                    PoolTable table1 = new PoolTable(initial_y, t);
                    table1.generateParticlesNoBuchacas();
                    PoolTable table2 = new PoolTable(initial_y, t-1);
                    table2.generateParticlesNoBuchacas();
                    runWithoutBuchacasPart1(table1, table2);
                }
            }
            else
                runWithoutBuchacas(Math.pow(10, delta_t));
        }
    }

    public static void setPhiVal(List<Particle> p1, List<Particle> p2, int time){
        double sum = 0;
        for(Particle p : p1){
            sum += calculateNorm(p, getByIdx(p2, p.getIdx()));
        }
        phi.add(time, sum);
    }

    public static double calculateNorm(Particle p1, Particle p2){
        return Math.sqrt(Math.pow(p2.getX() - p1.getX(), 2) + Math.pow(p2.getY() - p1.getY(), 2));
    }

    public static Particle getByIdx(List<Particle> parts, int idx) {
        return parts.stream().filter(p -> (p.getIdx() == idx)).findFirst().orElse(null);
    }

    public static void runWithoutBuchacasPart1(PoolTable t1, PoolTable t2){
        double elapsed_time = 0;
        int ctr = 1;
        double time_block = 1;
        setPhiVal(t1.particles, t2.particles, 0);
        while(elapsed_time < total_time){
            progressDeltaT(t1); // Recordemos que delta_t de t1 y t2 está siempre separada por un orden de magnitud
            // osea, 10 iteraciones de uno equivalen para llegar al tiempo del otro.
            for(int i = 0; i < 10; i++){
                progressDeltaT(t2);
            }

            if(elapsed_time + t1.getDeltaT() > ctr*time_block){
                setPhiVal(t1.particles, t2.particles, ctr);
                ctr++;
            }
            elapsed_time += Math.pow(10,t1.getDeltaT());
        }
        System.out.println(String.format("Phi entre %d y %d: ", (int)t1.getDeltaT(), (int)t1.getDeltaT()-1));
        System.out.println(phi);

    }

    public static void progressDeltaT(PoolTable t){
        for(int i = 0; i < t.particles.size(); i++){
            Particle p = t.particles.get(i);
            boolean flag = false;
            Double[] forceP;
                /*para cada partícula p, itero por el resto de las partículas a ver cuáles la están "influenciando"
                acumulo las fuerzas para sacar la fuerza neta*/
            for(int j = i + 1; j < t.particles.size(); j++) {
                Particle q = t.particles.get(j);
                if (p.getNorm(p.getX(), p.getY(), q.getX(), q.getY()) < (p.getRadius() + q.getRadius())) {
                    //si se influencian, calculo la fuerza entre ellas
                    forceP = p.predictForce(q.getX(), q.getY());
                    //cada bocha tiene que guardar y "acumular" la fuerza de esta interacción
                    p.accumForce(forceP[0], forceP[1]);
                    q.accumForce(-forceP[0], -forceP[1]);
                    flag = true;
                }
            }
            if(flag || p.hasAccumForce()) { //si al salir del for hubo al menos una colisión, entonces hay fuerza neta
                p.applyUpdate(p.getForce());
                p.resetForce(); //vuelvo la fuerza a 0 para empezar a acumular de 0 en la próxima iteración
            }
            else { //si no hubo choque con otra bocha, entro acá
                if(p.getX() >= t.LONG_SIDE || p.getX() <= 0){
                    p.applyBounceWithVerticalWall();
                }
                else if(p.getY() >= t.SHORT_SIDE || p.getY() <= 0){
                    p.applyBounceWithHorizontalWall();
                }
                else {
                    // al no haber colisión, hago update con fuerza 0
                    p.applyUpdate(new Double[]{0.0, 0.0});
                }
            }
        }
    }


    public static void runWithBuchacas(double delta_t){
        // Tiene que correr y simular mientras queden pelotas
        // Poner un boolean para saber si es con la mitad o no

        table.generateParticles();
        double elapsed_time = 0;
        int ctr = 0;
        double block_time = 0.001;
        try (FileWriter output = new FileWriter(
                String.format("output_delta_t=%f.txt", delta_t))) {
            FileUtils.takeSystemSnapshot(output, table.particles, ctr);
            while (table.particles.size() > 14) { //Tenemos 22, si salen la mitad quedarían 22-(16/2)=14
                progressDeltaTBuchacas(table);
                /*if (elapsed_time + delta_t > ctr * block_time) {
                    FileUtils.takeSystemSnapshot(output, table.particles, ctr);
                    ctr++;
                }*/
                FileUtils.takeSystemSnapshot(output, table.particles, ctr);
                elapsed_time += Math.pow(10, delta_t);
            }
        }
        catch(IOException e){
            exit(1);
        }
        System.out.println(String.format("Tiempo que tardó con delta_t=%f: %f\n", delta_t, elapsed_time));
    }

    public static void runWithoutBuchacas(double delta_t){
        table.generateParticlesNoBuchacas();
        double elapsed_time = 0;
        while(elapsed_time < total_time){
            progressDeltaT(table);
            elapsed_time += delta_t;
        }
    }

    private static boolean inContact(Particle p, Particle q){
        return p.getNorm(p.getX(), p.getY(), q.getX(), q.getY()) < (p.getRadius() + q.getRadius());
    }


    public static void progressDeltaTBuchacas(PoolTable t){

        /*
        Antes de chequear las fuerzas, elminio aquellas que esten colisionando
        con una buchaca
         */
        removeBuchacaCollisions();
        for(int i = 6; i < t.particles.size(); i++){
            Particle p = t.particles.get(i);
            boolean flag = false;
            Double[] forceP;
                /*para cada partícula p, itero por el resto de las partículas a ver cuáles la están "influenciando"
                acumulo las fuerzas para sacar la fuerza neta*/
            for(int j = i + 1; j < t.particles.size(); j++) {
                Particle q = t.particles.get(j);
                if (inContact(p,q)) {
                    //si se influencian, calculo la fuerza entre ellas
                    forceP = p.predictForce(q.getX(), q.getY());
                    //cada bocha tiene que guardar y "acumular" la fuerza de esta interacción
                    p.accumForce(forceP[0], forceP[1]);
                    q.accumForce(-forceP[0], -forceP[1]);
                    flag = true;
                }
            }
            if(flag || p.hasAccumForce()) { //si al salir del for hubo al menos una colisión, entonces hay fuerza neta
                p.applyUpdate(p.getForce());
                p.resetForce(); //vuelvo la fuerza a 0 para empezar a acumular de 0 en la próxima iteración
            }
            else { //si no hubo choque con otra bocha, entro acá
                if(p.getX() >= t.LONG_SIDE || p.getX() <= 0){
                    p.applyBounceWithVerticalWall();
                }
                else if(p.getY() >= t.SHORT_SIDE || p.getY() <= 0){
                    p.applyBounceWithHorizontalWall();
                }
                else {
                    // al no haber colisión, hago update con fuerza 0
                    p.applyUpdate(new Double[]{0.0, 0.0});
                }
            }
        }
    }

    public static void removeBuchacaCollisions() {
        for (int i = 0; i < 6; i++) {
            Particle p = table.particles.get(i);
            for (int j = 6; j < table.particles.size(); j++) {
                Particle q = table.particles.get(j);
                if (inContact(p, q)) {
                    int toRemove = q.getIdx();
                    table.particles.removeIf(m -> m.getIdx() == toRemove);
                    System.out.println("Saque una bocha");
                }
            }
        }
    }

}
