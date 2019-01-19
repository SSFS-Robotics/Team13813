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

    public static Integer LIFTING_REVOLUTION = 4; //TODO: adjust lifting revolution
    /*
        SERVO
     */
    private float forceFrontLeftServo;
    private float forceFrontRightServo;
    private float forceClipServo = 1.0f;

    private transient Telemetry telemetry;

    public GamepadManager(Telemetry telemetry) {
        this.telemetry = telemetry;
    }

    public void update(Gamepad gp1, Gamepad gp2) {
        // TODO: Calculate the force from gamepads

        /*
            for Mecanum Wheels

            Left Stick: control straight move
            Right Stick: control straight turn
          */
//        float LF =                      gp1.left_stick_y - gp1.left_stick_x;
//        float RF = gp1.right_stick_y + gp1.left_stick_y + gp1.left_stick_x;
//        float LB =                      gp1.left_stick_y + gp1.left_stick_x;
//        float RB = gp1.right_stick_y + gp1.left_stick_y - gp1.left_stick_x;

        // straight movement + rotation
        /*
        right qian = hou
        hou = qian

        left qian =you
        hou = zuo
         */

        // when first gamepad is not controlling movement, second gamepad will take over
        float LF = gp2.left_stick_y + gp2.left_stick_x - gp2.right_stick_y;
        float RF = gp2.left_stick_y - gp2.left_stick_x + gp2.right_stick_y;
        float LB = gp2.left_stick_y - gp2.left_stick_x - gp2.right_stick_y;
        float RB = gp2.left_stick_y + gp2.left_stick_x + gp2.right_stick_y;

        Float[] decMax = {Math.abs(LF), Math.abs(RF), Math.abs(LB), Math.abs(RB)};
        List<Float> a = new ArrayList<>(Arrays.asList(decMax));
        float max = Range.clip(Collections.max(a), 1f, Float.MAX_VALUE);

        LF = (LF / max) * Configuration.ABSOLUTE_SPEED;
        RF = (RF / max) * Configuration.ABSOLUTE_SPEED;
        LB = (LB / max) * Configuration.ABSOLUTE_SPEED;
        RB = (RB / max) * Configuration.ABSOLUTE_SPEED;

        //TODO: adjust sign
        forceFrontLeftMotor = Range.clip(LF, -1f, 1f);
        forceFrontRightMotor = -Range.clip(RF, -1f, 1f);
        forceBackLeftMotor = Range.clip(LB, -1f, 1f);
        forceBackRightMotor = -Range.clip(RB, -1f, 1f);
        /*
            To the Left:
            V------A


            A------V

            To the Right:
            A------V


            V------A

            To Left Back -> Right Front
            V------0        A------0


            0------V        0------A

            To Left Front -> Right Back
            0------A        0------V


            A------0        V------0

         */


//        /*
//            for Two Wheels
//
//            Left Stick:
//            Right Stick:
//         */
//        float LB = gp1.left_stick_y + gp1.right_stick_x;
//        float RB = gp1.left_stick_y -gp1.right_stick_x;
//        forceBackLeftMotor = Range.clip(LB, -1, 1);
//        forceBackRightMotor = Range.clip(RB, -1, 1);


        /*
            For lifting
         */
        float lift = gp1.a?1.0f:0.0f;
        float drop = gp1.b?1.0f:0.0f;
        forceLiftMotor = Range.clip(-lift + drop, -0.2, 0.2);
        //TODO: adjust sign

        /*
            For hand
         */
//        float down = gp1.dpad_down?1.0f:0.0f;
//        float up = gp1.dpad_up?1.0f:0.0f;
//        forceArmLeftMotor = Range.clip(-down + up, -0.1, 0.1);
//        forceArmRightMotor = -Range.clip(-down + up, -0.1, 0.1);
        float arm = gp1.left_stick_y;
        forceArmLeftMotor = -Range.clip(arm, -0.02, 0.02);
        forceArmRightMotor = Range.clip(arm, -0.02, 0.02);
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

        float rollInLeft = (gp1.left_bumper)?1.0f:0.0f;
        float rollInRight = (gp1.right_bumper)?1.0f:0.0f;
        float rollOutLeft = Range.clip(gp1.left_trigger, -1, 1);
        float rollOutRight = Range.clip(gp1.right_trigger, -1, 1);
        forceFrontLeftServo = Range.clip(-rollOutLeft + rollInLeft, -1, 1)/2.0f+0.5f; // (0, 0.5)
        forceFrontRightServo = -Range.clip(-rollOutRight + rollInRight, -1, 1)/2.0f+0.5f; // (0, 0.5)

        if (gp1.x && !gp1.y) {
            forceClipServo = 0;
        } else if (!gp1.x && gp1.y) {
            forceClipServo = 1;
        }
        forceClipServo = Range.clip(forceClipServo, 0.5f, 1f);

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
