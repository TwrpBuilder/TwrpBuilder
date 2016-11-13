package github.grace5921.TwrpBuilder.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by sumit on 5/11/16.
 */

public class ShellExecuter {
    public static String command;

    public static final  String runAsRoot()
    {

        try {
            // Executes the command.
            Process process = Runtime.getRuntime().exec(command);
            // Reads stdout.
            // NOTE: You can write to stdin of the command using
            //       process.getOutputStream().
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            int read;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
            reader.close();

            // Waits for the command to finish.
            process.waitFor();

            return output.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e)

        {
            throw new RuntimeException(e);
        }
    }
    public static boolean hasSelinux()
    {
        return new File("/sys/fs/selinux/enforce").isFile();
    }
    public static boolean hasRoot()
    {
        return new File("/system/xbin/su").isFile();
    }
    public static List<String> SuperSu(){
        return Shell.SH.run(command);
    }
    public static boolean hasGpu(){return new File("/sys/class/kgsl/kgsl-3d0/gpuclk").isFile();}
    public static boolean hasFastCharge(){return new File("/sys/kernel/fast_charge/force_fast_charge").isFile();}

}
