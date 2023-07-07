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

    /*
    ESTUDIO:
    1. Con v=2, realizar tres corridas para cada caso:
        * N=200 y d=1.2
        * N=260 y d=1.8
        * N=320 y d=2.4
        * N=380 y d=3.0
        * N=440 y d=3.6
        * N=500 y d=4.2
    Hacer curvas de descarga, caudal, caudal medio y específico (esto análogo al TP5, luego comparar resultados
    con los del CPM).
    2. Con N y d fijos, variar v
    v = {2.0, 4.0, 6.0, 8.0, 10.0, 12.0}
    Hacer caudal vs v, tiempo total vs v. 
     */

    private static double runSfm(double v) {
            double toRet = 0;
            int N = 200;
            double d = 1.2;
            Room room = new Room(N, 20, 20, 10);
            try(FileWriter output = new FileWriter("test_ovito_" + v + "-" + N + "-" + d + ".txt")) {
                try (FileWriter output2 = new FileWriter("punto_a_iter_" + v + "-" + N + "-" + d +".txt")) {
                    SFM sfm = new SFM(d, v, room);
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
