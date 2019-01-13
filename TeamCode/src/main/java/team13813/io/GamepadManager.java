package team13813.io;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.io.Serializable;

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
        float LF = gp1.left_stick_y - gp1.left_stick_x;
        float RF = gp1.right_stick_y + gp1.left_stick_x;
        float LB = gp1.left_stick_y + gp1.left_stick_x;
        float RB = gp1.right_stick_y - gp1.left_stick_x;
        //TODO: adjust sign
        forceFrontLeftMotor = Range.clip(LF, -1, 1);
        forceFrontRightMotor = Range.clip(RF, -1, 1);
        forceBackLeftMotor = Range.clip(LB, -1, 1);
        forceBackRightMotor = Range.clip(RB, -1, 1);

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
        float drop = gp1.y?1.0f:0.0f;
        forceLiftMotor = Range.clip(-lift + drop, -1, 1);
        //TODO: adjust sign

        /*
            For hand
         */
        float down = gp1.dpad_down?1.0f:0.0f;
        float up = gp1.dpad_up?1.0f:0.0f;
        forceArmLeftMotor = Range.clip(-down + up, -1, 1);
        forceArmRightMotor = Range.clip(-down + up, -1, 1);
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
        forceFrontRightServo = Range.clip(-rollOutRight + rollInRight, -1, 1)/2.0f+0.5f; // (0, 0.5)

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

    @Override
    public GamepadManager clone() {
        try{
            GamepadManager gamepadManager = (GamepadManager)super.clone();
            return gamepadManager;
        }catch(CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
