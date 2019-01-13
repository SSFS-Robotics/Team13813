package team13813.motion;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import team13813.util.Configuration;
import team13813.io.GamepadManager;

public class MotionManager {

    /*
        MOTOR
     */
    private DcMotor leftFrontWheel = null;
    private DcMotor rightFrontWheel = null;
    private DcMotor leftBackWheel = null;
    private DcMotor rightBackWheel = null;
    private DcMotor liftMotor = null;
    private DcMotor armLeftMotor = null;
    private DcMotor armRightMotor = null;

    /*
        SERVO
     */
    private Servo leftFrontServo = null;
    private Servo rightFrontServo = null;

    /*
        SENSOR
     */
    private DigitalChannel touch = null;
    private DistanceSensor leftDistance = null;
    private DistanceSensor rightDistance = null;
    private ColorSensor color = null;

    /*
        OTHER
     */
    private final float hsvValues[] = {0F, 0F, 0F};
    private final float values[] = hsvValues;
    private View relativeLayout;

    private Telemetry telemetry;

    public MotionManager(Telemetry telemetry, HardwareMap hardwareMap) {
        this.telemetry = telemetry;

        try {
            //motor
            this.leftFrontWheel = hardwareMap.get(DcMotor.class, Configuration.FRONT_LEFT_MOTOR);
            this.rightFrontWheel = hardwareMap.get(DcMotor.class, Configuration.FRONT_RIGHT_MOTOR);
            this.leftBackWheel = hardwareMap.get(DcMotor.class, Configuration.BACK_LEFT_MOTOR);
            this.rightBackWheel = hardwareMap.get(DcMotor.class, Configuration.BACK_RIGHT_MOTOR);
            this.liftMotor = hardwareMap.get(DcMotor.class, Configuration.LIFT_MOTOR);
            this.armLeftMotor = hardwareMap.get(DcMotor.class, Configuration.ARM_LEFT_MOTOR);
            this.armRightMotor = hardwareMap.get(DcMotor.class, Configuration.ARM_RIGHT_MOTOR);
            //servo
            this.leftFrontServo = hardwareMap.get(Servo.class, Configuration.FRONT_LEFT_SERVO);
            this.rightFrontServo = hardwareMap.get(Servo.class, Configuration.FRONT_RIGHT_SERVO);
            //TODO: rightServo.setDirection(Servo.Direction.REVERSE);

            //touch
            this.touch = hardwareMap.get(DigitalChannel.class, Configuration.TOUCH_SENSOR);
            this.touch.setMode(DigitalChannel.Mode.INPUT);
            //distance
            this.leftDistance = hardwareMap.get(DistanceSensor.class, Configuration.LEFT_DISTANCE_SENSOR);
            this.rightDistance = hardwareMap.get(DistanceSensor.class, Configuration.RIGHT_DISTANCE_SENSOR);
            //color
            this.color = hardwareMap.get(ColorSensor.class, Configuration.COLOR_SENSOR);
            int relativeLayoutId = hardwareMap.appContext.getResources().getIdentifier("RelativeLayout", "id", hardwareMap.appContext.getPackageName());
            this.relativeLayout = ((Activity) hardwareMap.appContext).findViewById(relativeLayoutId);
        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
            telemetry.addData("WARNING", "IllegalArgumentException. Not all programmed hardweres are initialized.");
        }

    }

    public void updateWithException(GamepadManager gamepadManager) {
        if (Configuration.ENCODER) {
            MotionExceptions.setModeWithException(leftFrontWheel, DcMotor.RunMode.RUN_USING_ENCODER);
//            leftFrontWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            MotionExceptions.setModeWithException(rightFrontWheel, DcMotor.RunMode.RUN_USING_ENCODER);
//            rightFrontWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            MotionExceptions.setModeWithException(leftBackWheel, DcMotor.RunMode.RUN_USING_ENCODER);
//            leftBackWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            MotionExceptions.setModeWithException(rightBackWheel, DcMotor.RunMode.RUN_USING_ENCODER);
//            rightBackWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        } else {
            MotionExceptions.setModeWithException(leftFrontWheel, DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//            leftFrontWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            MotionExceptions.setModeWithException(rightFrontWheel, DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//            rightFrontWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            MotionExceptions.setModeWithException(leftBackWheel, DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//            leftBackWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            MotionExceptions.setModeWithException(rightBackWheel, DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//            rightBackWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
        MotionExceptions.setPowerWithException(leftFrontWheel, gamepadManager.getForceFrontLeftMotor());
//        leftFrontWheel.setPower(gamepadManager.getForceFrontLeftMotor());
        MotionExceptions.setPowerWithException(rightFrontWheel, gamepadManager.getForceFrontRightMotor());
//        rightFrontWheel.setPower(gamepadManager.getForceFrontRightMotor());
        MotionExceptions.setPowerWithException(leftBackWheel, gamepadManager.getForceBackLeftMotor());
//        leftBackWheel.setPower(gamepadManager.getForceBackLeftMotor());
        MotionExceptions.setPowerWithException(rightBackWheel, gamepadManager.getForceBackRightMotor());
//        rightBackWheel.setPower(gamepadManager.getForceBackRightMotor());

        telemetry.addData("WheelPower", String.format("leftFrontWheel: %.2f", gamepadManager.getForceFrontLeftMotor()));
        telemetry.addData("WheelPower", String.format("rightFrontWheel: %.2f", gamepadManager.getForceFrontRightMotor()));
        telemetry.addData("WheelPower", String.format("leftBackWheel: %.2f", gamepadManager.getForceBackLeftMotor()));
        telemetry.addData("WheelPower", String.format("rightBackWheel: %.2f", gamepadManager.getForceBackRightMotor()));

        if (Configuration.ENCODER) {
            MotionExceptions.setModeWithException(liftMotor, DcMotor.RunMode.RUN_USING_ENCODER);
//            liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            MotionExceptions.setModeWithException(liftMotor, DcMotor.RunMode.RUN_TO_POSITION);
//            liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            MotionExceptions.setModeWithException(armLeftMotor, DcMotor.RunMode.RUN_USING_ENCODER);
//            armLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            MotionExceptions.setModeWithException(armRightMotor, DcMotor.RunMode.RUN_USING_ENCODER);
//            armRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        } else {
            MotionExceptions.setModeWithException(liftMotor, DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//            liftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            MotionExceptions.setModeWithException(armLeftMotor, DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//            armLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            MotionExceptions.setModeWithException(armRightMotor, DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//            armRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        MotionExceptions.setPowerWithException(liftMotor, 0.75);
//        liftMotor.setPower(0.75); //TODO: check power
        MotionExceptions.setTargetPositionWithException(liftMotor, Configuration.ANDYMARK_TICKS_PER_REV * GamepadManager.LIFTING_REVOLUTION * (int) gamepadManager.getForceLiftMotor());
//        liftMotor.setTargetPosition(Configuration.ANDYMARK_TICKS_PER_REV * GamepadManager.LIFTING_REVOLUTION * (int) gamepadManager.getForceLiftMotor()); //TODO: make sure the motor is from andymark
        telemetry.addData("LiftPosition", String.format("liftMotor: %.2f", gamepadManager.getForceLiftMotor()));

        MotionExceptions.setPowerWithException(armLeftMotor, gamepadManager.getForceArmLeftMotor());
//        armLeftMotor.setPower(gamepadManager.getForceArmLeftMotor());
        MotionExceptions.setPowerWithException(armRightMotor, gamepadManager.getForceArmRightMotor());
        armRightMotor.setPower(gamepadManager.getForceArmRightMotor());
        telemetry.addData("ArmPower", String.format("armLeftMotor: %.2f", gamepadManager.getForceArmLeftMotor()));
        telemetry.addData("ArmPower", String.format("armRightMotor: %.2f", gamepadManager.getForceArmRightMotor()));

        //TODO: 0=0degree, 1=180degree, 0.5=90degree
        MotionExceptions.setPositionWithException(leftFrontServo, 0.5);
//        leftFrontServo.setPosition(0.5); //TODO: it seems like 0.5 will cause continuous rotational servo to stop. I am not sure about that. Try 0.5 out before implementing other things.
        MotionExceptions.setPositionWithException(rightFrontServo, 0.5);
//        rightFrontServo.setPosition(0.5); //TODO: it seems like 0.5 will cause continuous rotational servo to stop. I am not sure about that. Try 0.5 out before implementing other things.
        telemetry.addData("ServoPosition", String.format("leftFrontServo: %.2s", gamepadManager.getForceFrontLeftServo()));
        telemetry.addData("ServoPosition", String.format("rightFrontServo: %.2s", gamepadManager.getForceFrontRightServo()));

        //touch
        if (MotionExceptions.getStateWithException(touch)) {
//        if (touch.getState()) {
            telemetry.addData("Touch", "Is Not Pressed");
        } else {
            telemetry.addData("Touch", "Is Pressed");
        }


        //distance
//        telemetry.addData("Distance", String.format("%.01f mm", leftDistance.getDistance(DistanceUnit.MM)));

        telemetry.addData("Distance", String.format("%.01f cm", MotionExceptions.getDistanceWithException(leftDistance, DistanceUnit.CM)));
//        telemetry.addData("Distance", String.format("%.01f cm", leftDistance.getDistance(DistanceUnit.CM)));

//        telemetry.addData("Distance", String.format("%.01f m", leftDistance.getDistance(DistanceUnit.METER)));
//        telemetry.addData("Distance", String.format("%.01f in", leftDistance.getDistance(DistanceUnit.INCH)));
//        telemetry.addData("Distance", String.format("%.01f mm", rightDistance.getDistance(DistanceUnit.MM)));

        telemetry.addData("Distance", String.format("%.01f cm", MotionExceptions.getDistanceWithException(rightDistance, DistanceUnit.CM)));
//        telemetry.addData("Distance", String.format("%.01f cm", rightDistance.getDistance(DistanceUnit.CM)));

//        telemetry.addData("Distance", String.format("%.01f m", rightDistance.getDistance(DistanceUnit.METER)));
//        telemetry.addData("Distance", String.format("%.01f in", rightDistance.getDistance(DistanceUnit.INCH)));

        // TODO: read sensor, encoder
        //color
        Color.RGBToHSV((int) (MotionExceptions.redWithException(color) * 255),
                (int) (MotionExceptions.greenWithException(color) * 255),
                (int) (MotionExceptions.blueWithException(color) * 255),
                hsvValues);
//        Color.RGBToHSV((int) (color.red() * 255),
//                (int) (color.green() * 255),
//                (int) (color.blue() * 255),
//                hsvValues);

        telemetry.addData("Alpha", MotionExceptions.alphaWithException(color));
        telemetry.addData("Red  ", MotionExceptions.redWithException(color));
        telemetry.addData("Green", MotionExceptions.greenWithException(color));
        telemetry.addData("Blue ", MotionExceptions.blueWithException(color));
//        telemetry.addData("Alpha", color.alpha());
//        telemetry.addData("Red  ", color.red());
//        telemetry.addData("Green", color.green());
//        telemetry.addData("Blue ", color.blue());
        telemetry.addData("Hue", hsvValues[0]);
        relativeLayout.post(new Runnable() {
            public void run() {
                relativeLayout.setBackgroundColor(Color.HSVToColor(0xff, values));
            }
        });
    }

    public void update(GamepadManager gamepadManager) { // use gamepad command, sensor input, vision input, and Configuration to control robot
        /*
            RUN_USING_ENCODER: set power
            RUN_WITHOUT_ENCODER: set current
            RUN_TO_POSITION: run to pos and hold. Note that you still need to set mode to RUN_USING_ENCODER first and setPower()
            STOP_AND_RESET_ENCODER: stop moving and reset encoder value to 0

            setTargetPosition = revolution * ticks/revolution
         */
        if (Configuration.ENCODER) {
            leftFrontWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightFrontWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            leftBackWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightBackWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        } else {
            leftFrontWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            rightFrontWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            leftBackWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            rightBackWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
        leftFrontWheel.setPower(gamepadManager.getForceFrontLeftMotor());
        rightFrontWheel.setPower(gamepadManager.getForceFrontRightMotor());
        leftBackWheel.setPower(gamepadManager.getForceBackLeftMotor());
        rightBackWheel.setPower(gamepadManager.getForceBackRightMotor());

        telemetry.addData("WheelPower", String.format("leftFrontWheel: %.2f", gamepadManager.getForceFrontLeftMotor()));
        telemetry.addData("WheelPower", String.format("rightFrontWheel: %.2f", gamepadManager.getForceFrontRightMotor()));
        telemetry.addData("WheelPower", String.format("leftBackWheel: %.2f", gamepadManager.getForceBackLeftMotor()));
        telemetry.addData("WheelPower", String.format("rightBackWheel: %.2f", gamepadManager.getForceBackRightMotor()));

        if (Configuration.ENCODER) {
            liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            armLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            armRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        } else {
            liftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            armLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            armRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        liftMotor.setPower(0.75); //TODO: check power
        liftMotor.setTargetPosition(Configuration.ANDYMARK_TICKS_PER_REV * GamepadManager.LIFTING_REVOLUTION * (int) gamepadManager.getForceLiftMotor()); //TODO: make sure the motor is from andymark
        telemetry.addData("LiftPosition", String.format("liftMotor: %.2f", gamepadManager.getForceLiftMotor()));

        armLeftMotor.setPower(gamepadManager.getForceArmLeftMotor());
        armRightMotor.setPower(gamepadManager.getForceArmRightMotor());
        telemetry.addData("ArmPower", String.format("armLeftMotor: %.2f", gamepadManager.getForceArmLeftMotor()));
        telemetry.addData("ArmPower", String.format("armRightMotor: %.2f", gamepadManager.getForceArmRightMotor()));

        //TODO: 0=0degree, 1=180degree, 0.5=90degree
        leftFrontServo.setPosition(0.5); //TODO: it seems like 0.5 will cause continuous rotational servo to stop. I am not sure about that. Try 0.5 out before implementing other things.
        rightFrontServo.setPosition(0.5); //TODO: it seems like 0.5 will cause continuous rotational servo to stop. I am not sure about that. Try 0.5 out before implementing other things.
        telemetry.addData("ServoPosition", String.format("leftFrontServo: %.2s", gamepadManager.getForceFrontLeftServo()));
        telemetry.addData("ServoPosition", String.format("rightFrontServo: %.2s", gamepadManager.getForceFrontRightServo()));

        //touch
        if (touch.getState()) {
            telemetry.addData("Touch", "Is Not Pressed");
        } else {
            telemetry.addData("Touch", "Is Pressed");
        }


        //distance
//        telemetry.addData("Distance", String.format("%.01f mm", leftDistance.getDistance(DistanceUnit.MM)));
        telemetry.addData("Distance", String.format("%.01f cm", leftDistance.getDistance(DistanceUnit.CM)));
//        telemetry.addData("Distance", String.format("%.01f m", leftDistance.getDistance(DistanceUnit.METER)));
//        telemetry.addData("Distance", String.format("%.01f in", leftDistance.getDistance(DistanceUnit.INCH)));
//        telemetry.addData("Distance", String.format("%.01f mm", rightDistance.getDistance(DistanceUnit.MM)));
        telemetry.addData("Distance", String.format("%.01f cm", rightDistance.getDistance(DistanceUnit.CM)));
//        telemetry.addData("Distance", String.format("%.01f m", rightDistance.getDistance(DistanceUnit.METER)));
//        telemetry.addData("Distance", String.format("%.01f in", rightDistance.getDistance(DistanceUnit.INCH)));

        // TODO: read sensor, encoder
        //color
        Color.RGBToHSV((int) (color.red() * 255),
                (int) (color.green() * 255),
                (int) (color.blue() * 255),
                hsvValues);
        telemetry.addData("Alpha", color.alpha());
        telemetry.addData("Red  ", color.red());
        telemetry.addData("Green", color.green());
        telemetry.addData("Blue ", color.blue());
        telemetry.addData("Hue", hsvValues[0]);
        relativeLayout.post(new Runnable() {
            public void run() {
                relativeLayout.setBackgroundColor(Color.HSVToColor(0xff, values));
            }
        });
    }
}
