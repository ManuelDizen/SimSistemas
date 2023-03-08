import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class Generator {

    public static void generateOutputFile(List<Particle> particles, int N, double r_c){
        try(FileWriter fw = new FileWriter("Vecinos-" + "N:" + N + "-" +
                "rc:" + r_c + ".txt", false);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {

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
}
