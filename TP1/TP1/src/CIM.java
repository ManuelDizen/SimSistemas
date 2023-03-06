import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CIM {

    private static class Particle{

        private double x;
        private double y;
        private int r;
        private List<Particle> neighbours;

        public int getIdx() {
            return idx;
        }

        public void setIdx(int idx) {
            this.idx = idx;
        }

        private int idx;

        public Particle(int idx, double x, double y, int r){
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

        public  int getR() {
            return r;
        }

        public  void setR(int r) {
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


    }
    private static final int CIM_LENGTH_SIDE = 20;
    private static final int CIM_CELLS_PER_SIDE = 5;
    private static final int CIM_CELL_SIZE = CIM_LENGTH_SIDE/CIM_CELLS_PER_SIDE;

    private void locateInMatrix(Particle p, int L, int M, List<List<List<Particle>>> particleMatrix) {
        int idxX = (int) Math.floor(p.getX()/M);
        int idxY = (int) Math.floor(p.getY()/M);
        if(particleMatrix.get(idxX) == null)
            particleMatrix.add(idxX, new ArrayList<>());
        if(particleMatrix.get(idxX).get(idxY) == null){
            particleMatrix.get(idxX).add(idxY, new ArrayList<>());
        }
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
                                     boolean round, int cellX, int cellY, int r_c) {
        //List<Particle> sameCell = particleMatrix.get(cellX).get(cellY).subList(particleMatrix.get(cellX).get(cellY).indexOf(p), particleMatrix.get(cellX).get(cellY).size()-1);
        for(Particle p : particles) {
            for (int i = particles.indexOf(p) + 1; i < particles.size(); i++) {
                /*
                Condición de vecino:
                raiz((x1 - x2)² + (y1-y2)^2) < r_c + 2r
                 */
                if (
                        Math.pow(p.getX() - particles.get(i).getX(), 2) +
                                Math.pow(p.getY() - particles.get(i).getY(), 2)
                                < (2 * p.getR()) + r_c)
                    p.addNeighbour(particles.get(i));
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
                    if (
                            Math.pow(p.getX() - k.getX(), 2) +
                                    Math.pow(p.getY() - k.getY(), 2)
                                    < (2 * p.getR()) + r_c)
                        p.addNeighbour(k);
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
                    if (
                            Math.pow(p.getX() - k.getX(), 2) +
                                    Math.pow(p.getY() - k.getY(), 2)
                                    < (2 * p.getR()) + r_c)
                        p.addNeighbour(k);
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
                    if (
                            Math.pow(p.getX() - k.getX(), 2) +
                                    Math.pow(p.getY() - k.getY(), 2)
                                    < (2 * p.getR()) + r_c)
                        p.addNeighbour(k);
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
                    if (
                            Math.pow(p.getX() - k.getX(), 2) +
                                    Math.pow(p.getY() - k.getY(), 2)
                                    < (2 * p.getR()) + r_c)
                        p.addNeighbour(k);
                }
            }

        }

    }

    private void CellIndexMethod(int N, int r, int L, int M, int r_c, boolean round){

        if(L/M <= r_c){
            throw new RuntimeException("L/M debe ser mayor exclusivo a r_c. Terminando ejecución");
        }
        //tenemos un espacio de LxL, y habría que randomizar N posiciones
        //cada partícula es un par ordenado, con lo que habría que randomizar n posiciones para cada dimensión

        List<Particle> particleList = new ArrayList<Particle>();
        List<List<List<Particle>>> particleMatrix = new ArrayList<>();

        for(int i=0; i<N; i++) {
            Particle p = new Particle(i, Math.random() * L, Math.random() * L, r);
            particleList.add(p);
            locateInMatrix(p, L, M, particleMatrix);
        }

        for(int i=0; i<M; i++) {
            for(int j=0; j<M; j++) {
                List<Particle> auxList = particleMatrix.get(i).get(j);
                if(auxList != null && !auxList.isEmpty()) {
                    //TODO: Esto está dos veces. ¿Se podrá sacar?
                    List<Particle> particlesToEvaluate = particleMatrix.get(i).get(j);
                    if(particlesToEvaluate != null) {
                        particlesToEvaluate.sort((o1, o2) -> {
                            double distX = o1.getX() - o2.getX();
                            double retVal = distX != 0 ? distX : (o1.getY() - o2.getY());
                            return retVal > 0 ? 1 : (retVal < 0 ? -1 : 0);
                        });
                    }
                    calculateNeighbours(particlesToEvaluate, particleMatrix, round, i, j, r_c);
                }
            }
        }

    }



    public static void main(String[] args) {
        boolean round = false;
        int N = 1;
        int L = 1;
        int r = 1;
        int M = 1;
        int r_c = 1;
        CIM cim = new CIM();
        cim.CellIndexMethod(N, r, L, M, r_c, round);
    }

}
