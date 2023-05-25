import models.EscapeSimulation;
import utils.FileUtils;

import java.io.FileWriter;
import java.io.IOException;

import static java.lang.System.exit;

public class PedestrianRunnable {
    public static void main(String[] args) {
        EscapeSimulation esc = new EscapeSimulation(200, 1.2);
        esc.createParticles();
        try(FileWriter output = new FileWriter(String.format("test_ovito.txt"))){
            FileUtils.takeSystemSnapshot(output, esc.getParticles(), 0);
        }
        catch(IOException e){
            e.printStackTrace();
            exit(1);
        }
    }
}
