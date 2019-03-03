package team13813.io;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import team13813.util.Configuration;

public class GamepadManager implements Serializable, Cloneable {

    /*
        MOTOR
     */
    private double forceFrontLeftMotor;
    private double forceFrontRightMotor;
    private double forceBackLeftMotor;
    private double forceBackRightMotor;
    private double forceArmLeftMotor;
    private double forceArmRightMotor;
    private double forceLiftMotor;
//    private int positionArmMotor;
//    private boolean ifArmEncoder = false;

    public static Integer LIFTING_REVOLUTION = 4; //TODO: adjust lifting revolution
    /*
        SERVO
     */
    private float forceFrontLeftServo;
    private float forceFrontRightServo;
    private float forceClipServo = -1.0f;
    private float forceTouchServo = 0.5f;

    private transient Telemetry telemetry;

    public GamepadManager(Telemetry telemetry) {
        this.telemetry = telemetry;
    }

    public void update(Gamepad gp1, Gamepad gp2) {
        // TODO: Calculate the force from gamepads

        // when first gamepad is not controlling movement, second gamepad will take over
        float LF = gp2.left_stick_y - gp2.left_stick_x - gp2.right_stick_x;
        float RF = gp2.left_stick_y + gp2.left_stick_x + gp2.right_stick_x;
        float LB = gp2.left_stick_y + gp2.left_stick_x - gp2.right_stick_x;
        float RB = gp2.left_stick_y - gp2.left_stick_x + gp2.right_stick_x;

        Float[] decMax = {Math.abs(LF), Math.abs(RF), Math.abs(LB), Math.abs(RB)};
        List<Float> a = new ArrayList<>(Arrays.asList(decMax));
        float max = Range.clip(Collections.max(a), 1f, Float.MAX_VALUE);

        LF = (LF / max) * Configuration.ABSOLUTE_SPEED *0.6f;
        RF = (RF / max) * Configuration.ABSOLUTE_SPEED *0.6f;
        LB = (LB / max) * Configuration.ABSOLUTE_SPEED *0.6f;
        RB = (RB / max) * Configuration.ABSOLUTE_SPEED *0.6f;

        //TODO: adjust sign
        forceFrontLeftMotor = Range.clip(LF, -1f, 1f);
        forceFrontRightMotor = -Range.clip(RF, -1f, 1f);
        forceBackLeftMotor = Range.clip(LB, -1f, 1f);
        forceBackRightMotor = -Range.clip(RB, -1f, 1f);

        /*
            For lifting
         */
//        float lift = gp2.a?1.0f:0.0f;
//        float drop = gp2.b?1.0f:0.0f;
//        forceLiftMotor = Range.clip(-lift + drop, -0.4, 0.4);
//        //TODO: adjust sign

        /*
            For hand
         */
//        float down = gp1.dpad_down?1.0f:0.0f;
//        float up = gp1.dpad_up?1.0f:0.0f;
//        forceArmLeftMotor = Range.clip(-down + up, -0.1, 0.1);
//        forceArmRightMotor = -Range.clip(-down + up, -0.1, 0.1);
//        if (!ifArmEncoder) {
            float arm = gp1.left_stick_y;
            forceArmLeftMotor = Range.clip(arm, -0.3, 0.3);
            forceArmRightMotor = Range.clip(arm, -0.3, 0.3);
//        } else {
//            // TODO
//        }
//        if (gp1.a && !ifArmEncoder) {
//            ifArmEncoder = true;
//            positionArmMotor = 0;
//        } else if (gp1.b && ifArmEncoder) {
//            ifArmEncoder = false;
//            positionArmMotor = 0;
//        }
        //TODO: adjust sign

        /*
            For Servo
            triggers: float 0 (not pressed), float 1 (pressed) - lower
            bumpers: false (not pressed), true (pressed) - upper
         */
//        float rollIn = (gp1.left_bumper || gp1.right_bumper)?1.0f:0.0f;
//        float rollOut = Range.clip(gp1.left_trigger + gp1.right_trigger, -1, 1);
//        forceFrontLeftServo = Range.clip(-rollOut + rollIn, -1, 1);
//        forceFrontRightServo = Range.clip(-rollOut + rollIn, -1, 1);

//        float rollInLeft = (gp1.left_bumper)?1.0f:0.0f;
//        float rollInRight = (gp1.right_bumper)?1.0f:0.0f;
//        float rollOutLeft = Range.clip(gp1.left_trigger, -1, 1);
//        float rollOutRight = Range.clip(gp1.right_trigger, -1, 1);
//        forceFrontLeftServo = Range.clip(-rollOutLeft + rollInLeft, -1, 1)/2.0f+0.5f; // (0, 0.5)
//        forceFrontRightServo = -Range.clip(-rollOutRight + rollInRight, -1, 1)/2.0f+0.5f; // (0, 0.5)

        if (gp1.x && !gp1.y) {
            forceClipServo = -1.0f;
        } else if (!gp1.x && gp1.y) {
            forceClipServo = 0.7f;
        }
        forceClipServo = Range.clip(forceClipServo, -1.0f, 1.0f);

        if (gp1.dpad_down && !gp1.dpad_up) {
            forceTouchServo = 0f;
        } else if (!gp1.dpad_down && gp1.dpad_up) {
            forceTouchServo = 1f;
        } else if ((gp1.dpad_left || gp1.dpad_right)&&(forceTouchServo != 0.5f)) {
            forceTouchServo = 0.5f;
        }
//        forceTouchServo = gp1.right_stick_y;
        forceTouchServo = Range.clip(forceTouchServo, 0f, 1f);

        //TODO: adjust sign

    }
    public double getForceFrontLeftMotor() {
        return forceFrontLeftMotor;
    }

    public double getForceFrontRightMotor() {
        return forceFrontRightMotor;
    }

    public double getForceBackLeftMotor() {
        return forceBackLeftMotor;
    }

    public double getForceBackRightMotor() {
        return forceBackRightMotor;
    }

    public double getForceArmLeftMotor() {
        return forceArmLeftMotor;
    }

    public double getForceArmRightMotor() {
        return forceArmRightMotor;
    }

    public double getForceLiftMotor() {
        return forceLiftMotor;
    }

    public float getForceFrontLeftServo() {
        return forceFrontLeftServo;
    }

    public float getForceFrontRightServo() {
        return forceFrontRightServo;
    }

    public float getForceClipServo() {
        return forceClipServo;
    }

    public float getForceTouchServo() { return forceTouchServo;}

    @Override
    public GamepadManager clone() {
        try{
            return (GamepadManager)super.clone();
        }catch(CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
