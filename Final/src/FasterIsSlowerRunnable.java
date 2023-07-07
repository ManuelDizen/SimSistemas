import models.CPM;
import models.Room;
import models.SFM;
import utils.FileUtils;

import java.io.FileWriter;
import java.io.IOException;

import static java.lang.System.exit;

public class FasterIsSlowerRunnable {


    public static void main(String[] args) {
        double d1 = runSfm(2);
        System.out.println("\n\n\n d1: " + d1);

    }

    private static double runSfm(double v) {
            double toRet = 0;
            Room room = new Room(225, 20, 20, 10);
            try(FileWriter output = new FileWriter("test_ovito_" + v + ".txt")) {
                try (FileWriter output2 = new FileWriter("punto_a_iter_" + v + ".txt")) {
                    SFM sfm = new SFM(2, v, room);
                    int i=0;
                    while (sfm.getRemainingParticles() > 0) {
                        System.out.println("remaining: " + sfm.getRemainingParticles() + " - time: " + sfm.getTimeElapsed());
                        FileUtils.takeSystemSnapshotWCorners(output, sfm.getParticles(), sfm.getCorners(), i);
                        output2.write(String.format("%f %d\n", sfm.getTimeElapsed(), sfm.getRemainingParticles()));
                        sfm.iterate();
                        i++;
                    }
                    toRet = sfm.getTimeElapsed();
                    if(toRet > 70)
                        return toRet;
                } catch (IOException e) {
                    e.printStackTrace();
                    exit(1);
                }
            }
            catch(IOException e) {
                e.printStackTrace();
                exit(1);
            }

            return toRet;

    }

    
}
