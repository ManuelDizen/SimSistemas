import java.util.ArrayList;
import java.util.List;

public class CIM {

    private static class Particle{

        private double x;
        private double y;
        private double r;
        private final List<Particle> neighbours;

        public int getIdx() {
            return idx;
        }

        public void setIdx(int idx) {
            this.idx = idx;
        }

        private int idx;

        public Particle(int idx, double x, double y, double r){
            this.idx = idx;
            this.x = x;
            this.y = y;
            this.r = r;
            this.neighbours = new ArrayList<>();
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public  void setY(double y) {
            this.y = y;
        }

        public double getR() {
            return r;
        }

        public  void setR(double r) {
            this.r = r;
        }

        public List<Particle> getNeighbours() {
            return neighbours;
        }

        public void addNeighbour(Particle p) {
            this.neighbours.add(p);
        }

        public void addNeighbours(List<Particle> pList) {
            this.neighbours.addAll(pList);
        }

        public boolean isNeighbour(Particle p, double r_c){
            return Math.sqrt(Math.pow(this.getX() - p.getX(), 2) +
                    Math.pow(this.getY() - p.getY(), 2))
                    < (this.getR() +  p.getR() + r_c);
        }

        public boolean isPeriodicNeighbour(Particle p, double r_c, double L){
            double dx = Math.abs(this.x - p.x);
            if (dx > L / 2)
                dx = L - dx;

            double dy = Math.abs(this.y - p.y);
            if (dy > L / 2)
                dy = L - dy;

            return Math.sqrt(Math.pow(dx,2) + Math.pow(dy,2))
                    < (this.getR() +  p.getR() + r_c);
        }


    }
    private static final int CIM_LENGTH_SIDE = 10;
    private static final int CIM_PARTICLE_NUMBER = 10;
    private static final double CIM_PARTICLE_RADIUS_SIZE = 0.25;
    private static final int CIM_INTERACTION_RADIUS = 1;
    private static final int CIM_CELLS_PER_SIDE = 5;

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
            // Una a la derecha. Casos:
            /*
                1) pos X + 1 es MENOR al límite --> Da igual si es round o no, devuelvo ese
                2) pos X + 1 es IGUAL al limite Y deja redondo --> Devuelvo 0
                3) pos X + 1 es IGUAL al limite Y NO deja reodndo --> Devuelvo -1, indicando
                    que no necesita análisis
             */
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
            /*
            TODO!!!!!!!!
                Hay un error: Cuando hay contorno, seguimos evaluando la distancia euclidea. Esto obviamente
                no va a dar el resultado esperado, xq el cálculo se hace con la pos X y la pos Y.
                .
                Update: Creo que lo resolví con los métodos dentro de la clase.
                Si miramos, hay un método que permite tomar la distancia absoluta entre las particulas
                a la redonda (getPeriodicDistance).
             */
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

    private void CellIndexMethod(int N, double r, int L, int M, int r_c, boolean round){

        if(L/M <= r_c){
            throw new RuntimeException("L/M debe ser mayor exclusivo a r_c. Terminando ejecución");
        }
        //tenemos un espacio de LxL, y habría que randomizar N posiciones
        //cada partícula es un par ordenado, con lo que habría que randomizar n posiciones para cada dimensión

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
        for (Particle p : particleList){
            System.out.print(p.getIdx() + ":");
            for (Particle neighbour : p.getNeighbours()){
                System.out.print(" " + neighbour.getIdx());
            }
            System.out.print("\n");
        }
    }



    public static void main(String[] args) {
        boolean round = false;
        int N = CIM_PARTICLE_NUMBER;
        int L = CIM_LENGTH_SIDE;
        double r = CIM_PARTICLE_RADIUS_SIZE;
        int M = CIM_CELLS_PER_SIDE;
        int r_c = CIM_INTERACTION_RADIUS;
        CIM cim = new CIM();
        cim.CellIndexMethod(N, r, L, M, r_c, round);
    }

}
