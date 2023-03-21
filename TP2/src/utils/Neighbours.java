package utils;

import models.Particle;

import java.util.ArrayList;
import java.util.List;

public class Neighbours {

    private static void locateInMatrix(Particle p, int L, int M, List<List<List<Particle>>> particleMatrix) {
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

    private static void calculateNeighbours(List<Particle> particles, List<List<List<Particle>>> particleMatrix,
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

    private static void initializeMatrix(List<List<List<Particle>>> particleMatrix, int cells){
        for(int i = 0; i < cells; i++){
            particleMatrix.add(i, new ArrayList<>());
            for(int j = 0; j < cells; j++){
                particleMatrix.get(i).add(j, new ArrayList<>());
            }
        }
    }

    public static void CellIndexMethod(List<Particle> particles, int N, int L, int M,
                                       double r_c, boolean round){

        if(L/M <= r_c){
            throw new RuntimeException("L/M debe ser mayor exclusivo a r_c. Terminando ejecución");
        }
        //tenemos un espacio de LxL, y habría que randomizar N posiciones
        //cada partícula es un par ordenado, con lo que habría que randomizar n posiciones
        // para cada dimensión

        List<List<List<Particle>>> particleMatrix = new ArrayList<>();
        initializeMatrix(particleMatrix, M);
        for(Particle p : particles) {
            locateInMatrix(p, L, M, particleMatrix);
        }

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

    }

}
