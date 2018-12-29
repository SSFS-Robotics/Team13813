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

import team13813.Configuration;
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
    private float hsvValues[] = {0F, 0F, 0F};
    private final float values[] = hsvValues;
    private final View relativeLayout;

    private Telemetry telemetry;

    public MotionManager(Telemetry telemetry, HardwareMap hardwareMap) {
        this.telemetry = telemetry;

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

    }

    public void update(GamepadManager gamepadManager) { // use gamepad command, sensor input, vision input, and Configuration to control robot

        /*
            RUN_USING_ENCODER: set power
            RUN_WITHOUT_ENCODER: set current
            RUN_TO_POSITION: run to pos and hold. Note that you still need to set mode to RUN_USING_ENCODER first and setPower()
            STOP_AND_RESET_ENCODER: stop moving and reset encoder value to 0

            setTargetPosition = revolution * ticks/revolution
         */
        leftFrontWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFrontWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftBackWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBackWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftFrontWheel.setPower(gamepadManager.getForceFrontLeftMotor());
        rightFrontWheel.setPower(gamepadManager.getForceFrontRightMotor());
        leftBackWheel.setPower(gamepadManager.getForceBackLeftMotor());
        rightBackWheel.setPower(gamepadManager.getForceBackRightMotor());

        liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        armLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        armRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        liftMotor.setPower(0.75); //TODO: check power
        liftMotor.setTargetPosition(Configuration.ANDYMARK_TICKS_PER_REV * GamepadManager.LIFTING_REVOLUTION * (int)gamepadManager.getForceLiftMotor()); //TODO: make sure the motor is from andymark
        armLeftMotor.setPower(gamepadManager.getForceArmLeftMotor());
        armRightMotor.setPower(gamepadManager.getForceArmRightMotor());

        //TODO: 0=0degree, 1=180degree, 0.5=90degree
        leftFrontServo.setPosition(0.5); //TODO: it seems like 0.5 will cause continuous rotational servo to stop. I am not sure about that. Try 0.5 out before implementing other things.
        rightFrontServo.setPosition(0.5); //TODO: it seems like 0.5 will cause continuous rotational servo to stop. I am not sure about that. Try 0.5 out before implementing other things.


        //touch
//        if (digitalTouch.getState() == true) {
//            telemetry.addData("Digital Touch", "Is Not Pressed");
//        } else {
//            telemetry.addData("Digital Touch", "Is Pressed");
//        }


        //distance
//        telemetry.addData("range", String.format("%.01f mm", leftDistance.getDistance(DistanceUnit.MM)));
//        telemetry.addData("range", String.format("%.01f cm", leftDistance.getDistance(DistanceUnit.CM)));
//        telemetry.addData("range", String.format("%.01f m", leftDistance.getDistance(DistanceUnit.METER)));
//        telemetry.addData("range", String.format("%.01f in", leftDistance.getDistance(DistanceUnit.INCH)));

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
