import models.EscapeSimulation;
import utils.FileUtils;

import java.io.FileWriter;
import java.io.IOException;

import static java.lang.System.exit;

public class PedestrianRunnable {
    public static void main(String[] args) {
        /*EscapeSimulation esc = new EscapeSimulation(200, 1.2);
        esc.createParticles();
        try(FileWriter output = new FileWriter(String.format("test_ovito.txt"))){
            for(int i = 0; i < 1000; i++){
                FileUtils.takeSystemSnapshotWCorners(output, esc.getParticles(), esc.getCorners(), i);
                esc.iterate();
            }
        }
        catch(IOException e){
            e.printStackTrace();
            exit(1);
        }*/
        int i = 9;
        try(FileWriter output = new FileWriter(String.format("test_ovito_%d.txt", i))) {
            try (FileWriter output2 = new FileWriter(String.format("punto_a_iter_%d.txt", i))) {
                EscapeSimulation esc = new EscapeSimulation(200, 1.2);
                esc.createParticles();
                while (esc.getRemainingParticles() > 0) {
                    FileUtils.takeSystemSnapshotWCorners(output, esc.getParticles(), esc.getCorners(), i);
                    output2.write(String.format("%f %d\n", esc.getTimeElapsed(), esc.getRemainingParticles()));
                    esc.iterate();
                }
            } catch (IOException e) {
                e.printStackTrace();
                exit(1);
            }
        }
        catch(IOException e){
            e.printStackTrace();
            exit(1);
        }
    }
}
