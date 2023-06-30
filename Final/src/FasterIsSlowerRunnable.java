import models.CPM;
import models.Room;
import utils.FileUtils;

import java.io.FileWriter;
import java.io.IOException;

import static java.lang.System.exit;

public class FasterIsSlowerRunnable {

    public static void main(String[] args) {
        Room room = new Room(1000, 20, 20, 10);

        int i = 9;
        try(FileWriter output = new FileWriter("cringe5.txt")) {
            try (FileWriter output2 = new FileWriter("cringe2.txt")) {
                CPM cpm = new CPM(1.2, room);
                FileUtils.takeSystemSnapshotWCorners(output, cpm.getParticles(), cpm.getCorners(), i);
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
