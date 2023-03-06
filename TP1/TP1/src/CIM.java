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

    private void calculateNeighbours(List<Particle> particles, List<List<List<Particle>>> particleMatrix, boolean round,
                                     int cellX, int cellY) {
        //List<Particle> sameCell = particleMatrix.get(cellX).get(cellY).subList(particleMatrix.get(cellX).get(cellY).indexOf(p), particleMatrix.get(cellX).get(cellY).size()-1);
        for(Particle p : particles) {
            for(int i=particles.indexOf(p); i<particles.size()-1; i++) {
                /*

                raiz((x1 - x2)² + (y1-y2)^2) < r_c + 2r

                 */
                if(/*cumplo condición para ser vecino*/true)
                    p.addNeighbour(particles.get(i));
            }
        }
    }

    private void CellIndexMethod(int N, int r, int L, int M, int r_c){

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
                    List<Particle> particlesToEvaluate = particleMatrix.get(i).get(j);
                    particlesToEvaluate.sort(new Comparator<Particle>() {
                        @Override
                        public int compare(Particle o1, Particle o2) {
                            double distX = o1.getX() - o2.getX();
                            double retVal = distX != 0 ? distX : (o1.getY() - o2.getY());
                            if (retVal > 0) {
                                return 1;
                            } else {
                                return -1;
                            }
                        }
                    });
                    calculateNeighbours(particlesToEvaluate, particleMatrix, false, i, j);
                }
            }
        }

    }



    public static void main(String[] args) {
        int L = CIM_LENGTH_SIDE;


    }

}
