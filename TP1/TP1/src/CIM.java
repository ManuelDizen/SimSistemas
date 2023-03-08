import java.util.ArrayList;
import java.util.List;

public class CIM {
    private static final int CIM_LENGTH_SIDE = 10;
    private static final int CIM_PARTICLE_NUMBER = 10;
    private static final double CIM_PARTICLE_RADIUS_SIZE = 0.25;
    private static final double CIM_INTERACTION_RADIUS = 2;
    private static final int CIM_CELLS_PER_SIDE = 5;
    private static final boolean CIM_BRUTE = true; // False = CIM, True = Brute
    private static final boolean CIM_ROUND = true;

    private void locateInMatrix(Particle p, int L, int M, List<List<List<Particle>>> particleMatrix) {
        int idxX = (int) Math.floor(p.getX()/M);
        int idxY = (int) Math.floor(p.getY()/M);
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
                                     boolean round, int cellX, int cellY, double r_c, int L) {
        for(Particle p : particles) {
            for (int i = particles.indexOf(p) + 1; i < particles.size(); i++) {
                /*
                Condición de vecino:
                raiz((x1 - x2)² + (y1-y2)^2) < r_c + 2r
                 */
                Particle k = particles.get(i);
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
            int rightAdjacentCellX = cellX+1 < CIM_CELLS_PER_SIDE? cellX+1 : (
                        round && cellX+1 == CIM_CELLS_PER_SIDE? 0 : -1
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
            int bottomAdjacentCellY = cellY+1 < CIM_CELLS_PER_SIDE? cellY+1 : (
                    round && cellY+1 == CIM_CELLS_PER_SIDE? 0 : -1);
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

            /*
            |   |   |   |
            |   | * |   |
            |___|___|___|
            |///|   |   |
            |///|   |   |
            |///|___|___|
             */
            int leftAdjacentCellX = cellX - 1 >= 0 ? cellX - 1 : (
                    round && cellX - 1 < 0? CIM_CELLS_PER_SIDE - 1: -1
                    );
            if(leftAdjacentCellX >= 0 && bottomAdjacentCellY >= 0
                    && particleMatrix.get(leftAdjacentCellX).get(bottomAdjacentCellY) != null){
                for (Particle k : particleMatrix.get(rightAdjacentCellX).get(bottomAdjacentCellY)) {
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

    private List<Particle> CellIndexMethod(int N, double r, int L, int M, double r_c, boolean round){

        if(L/M <= r_c){
            throw new RuntimeException("L/M debe ser mayor exclusivo a r_c. Terminando ejecución");
        }
        //tenemos un espacio de LxL, y habría que randomizar N posiciones
        //cada partícula es un par ordenado, con lo que habría que randomizar n posiciones
        // para cada dimensión

        List<Particle> particleList = new ArrayList<Particle>();
        List<List<List<Particle>>> particleMatrix = new ArrayList<>();
        initializeMatrix(particleMatrix, M);
        for(int i=0; i<N; i++) {
            Particle p = new Particle(i, Math.random() * L, Math.random() * L, r);
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
                    calculateNeighbours(auxList, particleMatrix, round, i, j, r_c, L);
                }
            }
        }
        // A esta altura, las particulas insertadas en la matriz deberían tener sus vecinos ya guardados.
        long totalTime = System.nanoTime() - startTime;
        System.out.println("Execution time: " + totalTime/1000000 + "ms\n");
        for(Particle p : particleList) {
            System.out.println("Vecinos de la particula " + p.getIdx() + " (X: " + p.getX() + ", Y: "
                    + p.getY() + ")\n" + "---------------------");
            for (Particle f : p.getNeighbours()) {
                System.out.println(f.getIdx() + " (X: " + f.getX() + ", Y: " + f.getY() + ")\n");
            }
        }
        return particleList;
    }

    private List<Particle> BruteMethod(int N, double r, int L, int M, double r_c, boolean round){
        List<Particle> particleList = new ArrayList<>();
        for(int i=0; i<N; i++) {
            Particle p = new Particle(i, Math.random() * L, Math.random() * L, r);
            particleList.add(p);
        }
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
        long totalTime = System.nanoTime() - startTime;
        System.out.println("Execution time: " + totalTime/1000000 + "ms\n");
        for(Particle p : particleList) {
            System.out.println("Vecinos de la particula " + p.getIdx() + " (X: " + p.getX() + ", Y: "
                    + p.getY() + ")\n" + "---------------------");
            for (Particle f : p.getNeighbours()) {
                System.out.println(f.getIdx() + " (X: " + f.getX() + ", Y: " + f.getY() + ")\n");
            }
        }

        return particleList;
    }

    public static void main(String[] args) {

        // TODO: Cambiar generación random de particulas por las del archivo estático / dinámico

        CIM cim = new CIM();
        List<Particle> particles;
        if(!CIM_BRUTE)
            particles = cim.CellIndexMethod(CIM_PARTICLE_NUMBER, CIM_PARTICLE_RADIUS_SIZE, CIM_LENGTH_SIDE,
                    CIM_CELLS_PER_SIDE, CIM_INTERACTION_RADIUS, CIM_ROUND);
        else
            particles = cim.BruteMethod(CIM_PARTICLE_NUMBER, CIM_PARTICLE_RADIUS_SIZE, CIM_LENGTH_SIDE,
                    CIM_CELLS_PER_SIDE, CIM_INTERACTION_RADIUS, CIM_ROUND);

        Generator.generateOutputFile(particles, CIM_PARTICLE_NUMBER, CIM_INTERACTION_RADIUS);
    }

}
