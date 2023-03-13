import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class Generator {

    public static void generateInputFiles(int N, int cant) {
        for(int i=0; i<cant; i++) {
            try(FileWriter fw = new FileWriter("Dynamic" + N + "." + i + ".txt", false);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw))
            {
                out.println(0);
                for(int j=0; j<N; j++) {
                    StringBuilder aux = new StringBuilder();
                    aux.append(Math.random()* CIM.CIM_LENGTH_SIDE).append("\t").append(Math.random()*CIM.CIM_LENGTH_SIDE);
                    out.println(aux.toString());
                }
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void generateOutputFile(List<Particle> particles, int N, int file, double r_c, int M){
        try(FileWriter fw = new FileWriter("Vecinos-" + "M_" + M + "-" + "N_" + N + "-" +
                "rc_" + r_c + "." + file + ".txt", false);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println("Cell Index Method: ");
            for(Particle p : particles) {
                List<Particle> neighbours = p.getNeighbours();
                StringBuilder aux = new StringBuilder();
                for(Particle a : neighbours) {
                    String s = Integer.toString(a.getIdx());
                    aux.append(",").append(s);
                }
                out.println(p.getIdx() + aux.toString());
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generateResults(Map<Integer, List<Long>> cellTime, Map<Integer, Long> bruteTime) {
        try(FileWriter fw = new FileWriter("Resultados.txt", false);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println("Espacio de lado L=20, radio de interacción r_c=1, radio de partícula r=0.25");
            out.println("Averiguamos el tiempo promedio (de 5 pasadas con input distinto) en microsegundos\npara distintas cantidades de partículas y distintas distribuciones de grilla,\npara ambos métodos");

            for(int i=10; i<=200; i+=10) {
                
                if(i<50||i==80||i>190) {
                    List<Long> cellList = cellTime.get(i);
                    Long brute = bruteTime.get(i);

                    out.println("\nCantidad de partículas: " + i);

                    out.println("\nFuerza Bruta: " + brute);

                    out.println("\nCell Index Method: ");

                    for(int j=1; j<=cellList.size(); j++) {
                        out.println("\nGrilla " + j*2 + "x" + j*2 + ":\t\t" + cellList.get(j-1));
                    }
                }

            }

//            for(Particle p : particles) {
//                List<Particle> neighbours = p.getNeighbours();
//                StringBuilder aux = new StringBuilder();
//                for(Particle a : neighbours) {
//                    String s = Integer.toString(a.getIdx());
//                    aux.append(",").append(s);
//                }
//                out.println(p.getIdx() + aux.toString());
//            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
