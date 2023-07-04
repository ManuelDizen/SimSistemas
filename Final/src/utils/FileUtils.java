package utils;

import models.Particle;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
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

    public void takePositionSnapshot(double x, double y, String name, double delta_t){
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(
                    new File(path + "/" + name + delta_t + ".txt"), true));
            String builder =
                    String.format(Locale.US, "%6.7e", x) + "    " +
                            String.format(Locale.US, "%6.7e", y) + "    ";
            writer.write(builder);
            writer.newLine();
            writer.flush();
            writer.close();
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    public static void purgeDirectory(File dir) {
        for (File file: dir.listFiles()) {
            if (file.isDirectory())
                purgeDirectory(file);
            file.delete();
        }
    }

    public static void takeSystemSnapshot(FileWriter fw, List<Particle> particles, int i){
        setHeaders(fw, particles.size(), i);
        try {
            for (Particle p : particles) {
                fw.write(String.format("%d %f %f %f %f %f %f\n", p.getIdx(),
                        p.getX(), p.getY(), 0 * 1.0,
                        p.getVx(), p.getVy(),
                        p.getRadius()));
            }
        }
        catch(IOException e){
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    public static void takeSystemSnapshotWCorners(FileWriter fw, List<Particle> particles, List<Particle> corners,
                                                  int i){
        setHeaders(fw, particles.size() + corners.size(), i);
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');

        // Crear un DecimalFormat utilizando el DecimalFormatSymbols modificado
        DecimalFormat decimalFormat = new DecimalFormat("#0.000000", symbols);

        // Establecer la configuración regional para utilizar el nuevo DecimalFormatSymbols
        decimalFormat.setDecimalFormatSymbols(symbols);

        try {
            for (Particle p : particles) {
                fw.write(String.format("%d %s %s %s %s %s %s\n", p.getIdx(),
                        decimalFormat.format(p.getX()), decimalFormat.format(p.getY()), decimalFormat.format(0 * 1.0),
                        decimalFormat.format(p.getVx()), decimalFormat.format(p.getVy()),
                        decimalFormat.format(p.getRadius())));
            }
            for(Particle p : corners){
                fw.write(String.format("%d %s %s %s %s %s %s\n", p.getIdx(),
                        decimalFormat.format(p.getX()), decimalFormat.format(p.getY()), decimalFormat.format(0 * 1.0),
                        decimalFormat.format(p.getVx()), decimalFormat.format(p.getVy()),
                        decimalFormat.format(p.getRadius())));
            }
        }
        catch(IOException e){
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    private static void setHeaders(FileWriter output, int N, int i){
        try {
            output.write(String.format("%d\nFrame %d\n", N, i));
        }
        catch(IOException e){
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

}
