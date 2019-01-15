package team13813.io;

import android.content.Context;
import android.os.Environment;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.PrintWriter;
import java.io.StreamCorruptedException;
import java.io.StringWriter;

public class FileSerialization {
    public static boolean saveInternal(Context context, String filename, Object obj, Telemetry telemetry){
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

    public static boolean saveExternal(Object object, String filename, Telemetry telemetry) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();

            try {
                FileOutputStream file_output_stream = new FileOutputStream(path + "/" + filename);
                ObjectOutputStream object_output_stream = new ObjectOutputStream(file_output_stream);
                object_output_stream.writeObject(object);
                return true;
            } catch (IOException e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                String exceptionAsString = sw.toString();
                telemetry.addData("Error-IOException", exceptionAsString);
                return false;
            }
        }
        return false;
    }

    public static String readInternal(Context context, String filename, Telemetry telemetry) {
        int ch;
        try {
            StringBuilder sb = new StringBuilder("");
            FileInputStream fis = context.openFileInput(filename);
            while( (ch = fis.read()) != -1)
                sb.append((char)ch);
            return new String(sb);
        } catch (FileNotFoundException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            telemetry.addData("Error-FileNotFoundException", exceptionAsString);
        } catch (IOException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            telemetry.addData("Error-IOException", exceptionAsString);
        }
        return null;
    }

    public static Object loadInternal(Context context, String fileName, Telemetry telemetry) {
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

    public static Object loadExternal(Context context, String filename, Telemetry telemetry) {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        try {
            FileInputStream file_input_stream = context.openFileInput(path + "/" + filename);
            ObjectInputStream object_input_stream = new ObjectInputStream(file_input_stream);
            Object object = object_input_stream.readObject();
            object_input_stream.close();
            return object;
        } catch (FileNotFoundException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            telemetry.addData("Error-FileNotFoundException", exceptionAsString);
        } catch (OptionalDataException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            telemetry.addData("Error-OptionalDataException", exceptionAsString);
        } catch (StreamCorruptedException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            telemetry.addData("Error-StreamCorruptedException", exceptionAsString);
        } catch (IOException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            telemetry.addData("Error-IOException", exceptionAsString);
        } catch (ClassNotFoundException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            telemetry.addData("Error-ClassNotFoundException", exceptionAsString);
        }
        return null;
    }
    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public static void setClipboard(Context context, String text) {
//        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
//            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
//            assert clipboard != null;
//            clipboard.setText(text);
//        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            assert clipboard != null;
            clipboard.setPrimaryClip(clip);
//        }
    }
}