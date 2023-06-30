import models.CPM;
import models.Room;
import models.SFM;
import utils.FileUtils;

import java.io.FileWriter;
import java.io.IOException;

import static java.lang.System.exit;

public class FasterIsSlowerRunnable {

    public static void main(String[] args) {
        Room room = new Room(2, 20, 20, 10);

        int i = 2;
        try(FileWriter output = new FileWriter("cringe6.txt")) {
            try (FileWriter output2 = new FileWriter("cringe7.txt")) {
                SFM sfm = new SFM(1.2, 2, room);
                while (/*sfm.getRemainingParticles() > 0*/i>0) {
                    //System.out.println("remaining: " + cpm.getRemainingParticles());
                    FileUtils.takeSystemSnapshotWCorners(output, sfm.getParticles(), sfm.getCorners(), i);
                    output2.write(String.format("%f %d\n", sfm.getTimeElapsed(), sfm.getRemainingParticles()));
                    sfm.iterate();
                    i--;
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
