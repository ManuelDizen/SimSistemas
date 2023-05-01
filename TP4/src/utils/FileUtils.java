package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class FileUtils {

    private static final String path = "src/output";
    public void takePositionSnapshot(Particle p, String name, double delta_t){
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(
                    new File(path + "/" + name + delta_t + ".txt"), true));
            String builder =
                    String.format(Locale.US, "%6.7e", p.getX()) + "    " +
                            String.format(Locale.US, "%6.7e", p.getY()) + "    ";
            writer.write(builder);
            writer.newLine();
            writer.flush();
            writer.close();
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}
