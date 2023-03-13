import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class CIM {
    static int CIM_LENGTH_SIDE = 20;
    static int CIM_PARTICLE_NUMBER = 100;
    private static final double CIM_PARTICLE_RADIUS_SIZE = 0.25;
    private static final double CIM_INTERACTION_RADIUS = 1;
    private static final int CIM_CELLS_PER_SIDE = 5;
    private static final boolean CIM_BRUTE = true; // False = CIM, True = Brute
    private static final boolean CIM_ROUND = true;

    private void locateInMatrix(Particle p, int L, int M, List<List<List<Particle>>> particleMatrix) {
        int idxX = (int) Math.floor(p.getX()*M/L);
        int idxY = (int) Math.floor(p.getY()*M/L);
        if(particleMatrix.get(idxX) == null)
            particleMatrix.add(idxX, new ArrayList<>());
        if(particleMatrix.get(idxX).get(idxY) == null)
            particleMatrix.get(idxX).add(idxY, new ArrayList<>());
        particleMatrix.get(idxX).get(idxY).add(p);
    }

    /*
    Los vecinos se calcularan en una L acostada en la siguiente forma:
    |    |****|  1 |
    |____|____|____|
    |  4 |  3 |  2 |
    |____|____|____|
    |    |    |    |
    |____|____|____|
     */

    private void calculateNeighbours(List<Particle> particles, List<List<List<Particle>>> particleMatrix,
                                     boolean round, int cellX, int cellY, double r_c, int L, int M) {
        for(Particle p : particles) {
            for (int i = particles.indexOf(p) + 1; i < particles.size(); i++) {
                /*
                Condición de vecino:
                raiz((x1 - x2)² + (y1-y2)^2) < r_c + 2r
                 */
                Particle k = particles.get(i);
                if(!round){
                    if(p.isNeighbour(k, r_c)){
                        if(!p.getNeighbours().contains(k))
                            p.addNeighbour(k);
                        if(!k.getNeighbours().contains(p))
                            k.addNeighbour(p);
                    }
                }
                else{
                    if(p.isPeriodicNeighbour(k, r_c, L)){
                        if(!p.getNeighbours().contains(k))
                            p.addNeighbour(k);
                        if(!k.getNeighbours().contains(p))
                            k.addNeighbour(p);
                    }
                }
            }
            int rightAdjacentCellX = cellX+1 < M? cellX+1 : (
                        round && cellX+1 == M? 0 : -1
                    );
            /*
            |   |   |///|
            |   | * |///|
            |___|___|///|
            |   |   |   |
            |   |   |   |
            |___|___|___|
             */
            if(rightAdjacentCellX >= 0 && particleMatrix.get(rightAdjacentCellX).get(cellY) != null) {
                for (Particle k : particleMatrix.get(rightAdjacentCellX).get(cellY)) {
                    if(!round){
                        if(p.isNeighbour(k, r_c)){
                            if(!p.getNeighbours().contains(k))
                                p.addNeighbour(k);
                            if(!k.getNeighbours().contains(p))
                                k.addNeighbour(p);
                        }
                    }
                    else{
                        if(p.isPeriodicNeighbour(k, r_c, L)){
                            if(!p.getNeighbours().contains(k))
                                p.addNeighbour(k);
                            if(!k.getNeighbours().contains(p))
                                k.addNeighbour(p);
                        }
                    }
                }
            }
            int bottomAdjacentCellY = cellY+1 < M? cellY+1 : (
                    round && cellY+1 == M? 0 : -1);
            /*
            |   |   |   |
            |   | * |   |
            |___|___|___|
            |   |   |///|
            |   |   |///|
            |___|___|///|
             */
            if(rightAdjacentCellX >= 0 && bottomAdjacentCellY >= 0
                    && particleMatrix.get(rightAdjacentCellX).get(bottomAdjacentCellY) != null){
                for (Particle k : particleMatrix.get(rightAdjacentCellX).get(bottomAdjacentCellY)) {
                    if(!round){
                        if(p.isNeighbour(k, r_c)){
                            if(!p.getNeighbours().contains(k))
                                p.addNeighbour(k);
                            if(!k.getNeighbours().contains(p))
                                k.addNeighbour(p);
                        }
                    }
                    else{
                        if(p.isPeriodicNeighbour(k, r_c, L)){
                            if(!p.getNeighbours().contains(k))
                                p.addNeighbour(k);
                            if(!k.getNeighbours().contains(p))
                                k.addNeighbour(p);
                        }
                    }
                }
            }

            /*
            |   |   |   |
            |   | * |   |
            |___|___|___|
            |   |///|   |
            |   |///|   |
            |___|///|___|
             */

            if(bottomAdjacentCellY >= 0  && particleMatrix.get(cellX).get(bottomAdjacentCellY) != null){
                for (Particle k : particleMatrix.get(cellX).get(bottomAdjacentCellY)) {
                    if(!round){
                        if(p.isNeighbour(k, r_c)){
                            if(!p.getNeighbours().contains(k))
                                p.addNeighbour(k);
                            if(!k.getNeighbours().contains(p))
                                k.addNeighbour(p);
                        }
                    }
                    else{
                        if(p.isPeriodicNeighbour(k, r_c, L)){
                            if(!p.getNeighbours().contains(k))
                                p.addNeighbour(k);
                            if(!k.getNeighbours().contains(p))
                                k.addNeighbour(p);
                        }
                    }
                }
            }

            /*
            |   |   |   |
            |   | * |   |
            |___|___|___|
            |///|   |   |
            |///|   |   |
            |///|___|___|
             */
            int leftAdjacentCellX = cellX - 1 >= 0 ? cellX - 1 : (
                    round && cellX - 1 < 0? M - 1: -1
                    );
            if(leftAdjacentCellX >= 0 && bottomAdjacentCellY >= 0
                    && particleMatrix.get(leftAdjacentCellX).get(bottomAdjacentCellY) != null){
                for (Particle k : particleMatrix.get(leftAdjacentCellX).get(bottomAdjacentCellY)) {
                    if(!round){
                        if(p.isNeighbour(k, r_c)){
                            p.addNeighbour(k);
                            k.addNeighbour(p);
                        }
                    }
                    else{
                        if(p.isPeriodicNeighbour(k, r_c, L)){
                            p.addNeighbour(k);
                            k.addNeighbour(p);
                        }
                    }
                }
            }
        }

    }

    private void initializeMatrix(List<List<List<Particle>>> particleMatrix, int cells){
        for(int i = 0; i < cells; i++){
            particleMatrix.add(i, new ArrayList<>());
            for(int j = 0; j < cells; j++){
                particleMatrix.get(i).add(j, new ArrayList<>());
            }
        }
    }

    private long CellIndexMethod(List<Particle> particleList, int N, double r, int L, int M, double r_c, boolean round,
                                           Queue<Particle> particles){

        if(L/M <= r_c){
            throw new RuntimeException("L/M debe ser mayor exclusivo a r_c. Terminando ejecución");
        }
        //tenemos un espacio de LxL, y habría que randomizar N posiciones
        //cada partícula es un par ordenado, con lo que habría que randomizar n posiciones
        // para cada dimensión

        List<List<List<Particle>>> particleMatrix = new ArrayList<>();
        initializeMatrix(particleMatrix, M);
        for(Particle p : particles) {
            //Particle p = new Particle(i, Math.random() * L, Math.random() * L, r);
            particleList.add(p);
            locateInMatrix(p, L, M, particleMatrix);
        }

        // Se empieza a contabilizar el tiempo a partir de que se ejecuta el algoritmo,
        // no en la generación de particulas. Creimos innecesario este último tiempo dado
        // que es el mismo proceso para cualquier método comparativo.
        long startTime = System.nanoTime();

        for(int i=0; i<M; i++) {
            for(int j=0; j<M; j++) {
                List<Particle> auxList = particleMatrix.get(i).get(j);
                if(auxList != null && !auxList.isEmpty()) {
                    auxList.sort((o1, o2) -> {
                        double distX = o1.getX() - o2.getX();
                        double retVal = distX != 0 ? distX : (o1.getY() - o2.getY());
                        return retVal > 0 ? 1 : (retVal < 0 ? -1 : 0);
                    });
                    calculateNeighbours(auxList, particleMatrix, round, i, j, r_c, L, M);
                }
            }
        }
        // A esta altura, las particulas insertadas en la matriz deberían tener sus vecinos ya guardados.
        long totalTime = (System.nanoTime() - startTime)/1000;
        System.out.println("totaltime = " + totalTime);
        //System.out.println("Execution time: " + totalTime/1000 + "ms\n");
        return totalTime;

    }

    private long BruteMethod(List<Particle> particlesBrute, int L, double r_c, boolean round,
                                       Queue<Particle> particles){
        //Particle p = new Particle(i, Math.random() * L, Math.random() * L, r);
        List<Particle> particleList = new ArrayList<>(particles);
        long startTime = System.nanoTime();
        for(Particle p : particleList){
            for(Particle k : particleList){
                if(p.getIdx() != k.getIdx() && !p.getNeighbours().contains(k)){
                    if(!round){
                        if(p.isNeighbour(k, r_c)){
                            p.addNeighbour(k);
                            k.addNeighbour(p);
                        }
                    }
                    else{
                        if(p.isPeriodicNeighbour(k, r_c, L)){
                            p.addNeighbour(k);
                            k.addNeighbour(p);
                        }
                    }
                }
            }
        }

        particlesBrute.addAll(particleList);

        long totalTime = (System.nanoTime() - startTime)/1000;
        //System.out.println("Execution time: " + totalTime/1000000 + "ms\n");

        System.out.println("totaltime = " + totalTime);
        return totalTime;
    }

    private void checkCalculation(List<Particle> particles, List<Particle> particlesBrute) {
        //System.out.println("CHECK CALCULATION");
        for(int i=0; i<particles.size(); i++) {
            Particle current = particles.get(i);
            //System.out.println("checking " + i);
            if(current.getIdx() == particlesBrute.get(i).getIdx()) {
                for(Particle n : current.getNeighbours()) {
                    //System.out.println("neighbour: " + n.getIdx());
                    boolean found = false;
                    for(Particle b : particlesBrute.get(i).getNeighbours()) {
                        //System.out.println("entering with b: " + b.getIdx());
                        if(n.getIdx() == b.getIdx()) {
                            found = true;
                            //System.out.println("found!");
                            break;
                        }
                    }
                    if(!found)
                        throw new RuntimeException("Error en el cálculo");
                }
            } else {
                System.out.println("hello");
                int j=0;
                while(particlesBrute.get(j).getIdx() != current.getIdx())
                    j++;
                for(Particle n : current.getNeighbours()) {
                    boolean found = false;
                    for(Particle b : particlesBrute.get(j).getNeighbours()) {
                        if(n.getIdx() == b.getIdx()) {
                            found = true;
                            break;
                        }
                    }
                    if(!found)
                        throw new RuntimeException("Error en el cálculo");
                }
            }
        }
    }

   public static void main(String[] args) {

        // TODO: Cambiar generación random de particulas por las del archivo estático / dinámico

        CIM cim = new CIM();

        List<Particle> particles = new ArrayList<>();
        List<Particle> particlesBrute = new ArrayList<>();
        Queue<Particle> particleQueue = null;
        Map<Integer, List<Long>> timeCellMap = new HashMap<>();
        Map<Integer, Long> timeBruteMap = new HashMap<>();
        long timeCell = 0;
        long timeBrute = 0;

        for(int i=10; i<=200; i+=10) {
            if(i<50||i==80||i>190) {
                List<Long> timeCellList = new ArrayList<>();
            Generator.generateInputFiles(i, 5);
            List<Queue<Particle>> queueList = new ArrayList<>();
            List<List<Particle>> brutes = new ArrayList<>();
            timeBrute = 0;
            for(int b=0; b<5; b++) {
                try {
                    particleQueue = InputParser.parseParticles(i, b);
                    queueList.add(particleQueue);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                particlesBrute = new ArrayList<>();
                timeBrute += cim.BruteMethod(particlesBrute, CIM_LENGTH_SIDE,
                        CIM_INTERACTION_RADIUS, CIM_ROUND, particleQueue);
                brutes.add(particlesBrute);
            }
            timeBruteMap.put(i, timeBrute/5);
            for(int m=2; m<=10; m+=2) {
                timeCell = 0;
                for(int j=0; j<5; j++) {
                    particles = new ArrayList<>();
                    timeCell += cim.CellIndexMethod(particles, CIM_PARTICLE_NUMBER, CIM_PARTICLE_RADIUS_SIZE, CIM_LENGTH_SIDE,
                            m, CIM_INTERACTION_RADIUS, CIM_ROUND, queueList.get(j));
                    cim.checkCalculation(particles, brutes.get(j));
                    Generator.generateOutputFile(particles, CIM_PARTICLE_NUMBER, j, CIM_INTERACTION_RADIUS, m);
                }
                timeCellList.add(timeCell/5);

            }
            timeCellMap.put(i, timeCellList);   
            }
        }
        Generator.generateResults(timeCellMap, timeBruteMap);
    }

}
