import models.CPM;
import models.Room;
import models.SFM;
import utils.FileUtils;

import java.io.FileWriter;
import java.io.IOException;

import static java.lang.System.exit;

public class FasterIsSlowerRunnable {

    public static void main(String[] args) {
        double[] vs = {2.0, 4.0, 6.0, 10.0, 12.0, 15.0, 20.0, 30.0};
        for(double v : vs) {
            Room room = new Room(100, 20, 20, 10);
            try(FileWriter output = new FileWriter("cringe8.txt")) {
                try (FileWriter output2 = new FileWriter("cringe9.txt")) {
                    SFM sfm = new SFM(1.2, v, room);
                    int i=0;
                    while (sfm.getRemainingParticles() > 0) {
                        //System.out.println("remaining: " + sfm.getRemainingParticles());
                        FileUtils.takeSystemSnapshotWCorners(output, sfm.getParticles(), sfm.getCorners(), i);
                        output2.write(String.format("%f %d\n", sfm.getTimeElapsed(), sfm.getRemainingParticles()));
                        sfm.iterate();
                        i++;
                    }
                    System.out.println("v - " + v + "time: " + sfm.getTimeElapsed());
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

}
