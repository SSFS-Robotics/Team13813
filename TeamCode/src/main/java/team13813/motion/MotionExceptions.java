package team13813.motion;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class MotionExceptions {
    public static <T> T getWithException(HardwareMap hardwareMap, Class<? extends T> classOrInterface, String deviceName) {
        try {
            return hardwareMap.get(classOrInterface, deviceName);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static void setModeWithException(DcMotor motor, DcMotor.RunMode mode) {
        try {
            motor.setMode(mode);
        } catch (NullPointerException e) {
        }
    }

    public static void setPowerWithException(DcMotor motor, double power) {
        try {
            motor.setPower(power);
        } catch (NullPointerException e) {
        }
    }
    public static void setTargetPositionWithException(DcMotor motor, int position) {
        try {
            motor.setTargetPosition(position);
        } catch (NullPointerException e) {

        }
    }
    public static void setPositionWithException(Servo servo, double position) {
        try {
            servo.setPosition(position);
        } catch (NullPointerException e) {

        }
    }
    public static boolean getStateWithException(DigitalChannel touch) {
        try {
            return touch.getState();
        } catch (NullPointerException e) {
            return true;
        }
    }
    public static double getDistanceWithException(DistanceSensor distance, DistanceUnit distanceUnit) {
        try {
            return distance.getDistance(distanceUnit);
        } catch (NullPointerException e) {
            return ((double)0);
        }
    }
    public static int alphaWithException(ColorSensor color) {
        try {
            return color.alpha();
        } catch (NullPointerException e) {
            return 1;
        }
    }
    public static int redWithException(ColorSensor color) {
        try {
            return color.red();
        } catch (NullPointerException e) {
            return 1;
        }
    }
    public static int greenWithException(ColorSensor color) {
        try {
            return color.green();
        } catch (NullPointerException e) {
            return 1;
        }
    }
    public static int blueWithException(ColorSensor color) {
        try {
            return color.blue();
        } catch (NullPointerException e) {
            return 1;
        }
    }
    public static void setModeWithException(DigitalChannel touch, DigitalChannel.Mode mode) {
        try {
            touch.setMode(mode);
        } catch (NullPointerException e) {

        }
    }
    public static int getCurrentPositionWithException(DcMotor motor) {
        try {
            return motor.getCurrentPosition();
        } catch (NullPointerException e) {
            return 0;
        }
    }
}
