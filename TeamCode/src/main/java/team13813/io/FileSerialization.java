package team13813.io;

import android.content.Context;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

public class FileSerialization {
    public static boolean save(Context context, String filename, Object obj, Telemetry telemetry){
        try {
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(obj);
            os.close();
            fos.close();
            return true;
        } catch (IOException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            telemetry.addData("Error-IOException", exceptionAsString);
            return false;
        }
    }

    public static Object load(Context context, String fileName, Telemetry telemetry) {
        try {
            FileInputStream fis = context.openFileInput(fileName);
            ObjectInputStream is = new ObjectInputStream(fis);
            Object obj = is.readObject();
            is.close();
            fis.close();
            return obj;
        } catch (IOException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            telemetry.addData("Error-IOException", exceptionAsString);
            return null;
        } catch (ClassNotFoundException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            telemetry.addData("Error-ClassNotFoundException", exceptionAsString);
            return null;
        }
    }
}