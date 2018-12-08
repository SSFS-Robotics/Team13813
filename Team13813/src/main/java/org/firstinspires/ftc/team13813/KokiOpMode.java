package org.firstinspires.ftc.team13813;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="KokiOpMode", group="TeleOp")
//@Disabled
public class KokiOpMode extends OpMode {
    private boolean enCoderMode = true;

    // Declare OpMode members.
    // 1000ticks = 1sec
//    private ElapsedTime runtime = new ElapsedTime(); # no

    // motor name configuration
    String LEFT_WHEEL = "m2";
    String RIGHT_WHEEL = "m3";
    String LIFT_MOTOR = "m1";
    String LEFT_SERVO = "sev1";
    String RIGHT_SERVO = "sev2";
    String DOWN_SERVO = "sev3";
    String UP_SERVO = "sev4";

    // motor configuration
    private DcMotor leftWheel = null;
    private DcMotor rightWheel = null;
    private DcMotor lift = null;
    private Servo leftServo = null;
    private Servo rightServo = null;
    private Servo upServo = null;
    private Servo downServo = null;

    double leftWheelPower;
    double rightWheelPower;
    double upLiftPower;

    // data storage from gamepad
    double drive;
    double turn;
    float up;
    float down;
    float armForce;
    float leftClaw;
    float rightClaw;
    float upServoLeft;
    float upServoRight;
    float upServoPosition;
    float downServoLeft;
    float downServoRight;
    float downServoPosition;

    float leftClawInputAdjust = 0.123f;
    float rightClawInputAdjust = 0.240f;

    // variables in the father class
    // time = 0.0;
    // gamepad1;
    // gamepad2;
    // telemetry;
    // hardwareMap;
    // startTime;

    // functions in the father class
    // OpMode()
    // init() This method will be called once when the INIT button is pressed.
    // init_loop() This method will be called repeatedly once when the INIT button is pressed.
    // start() This method will be called once when this op mode is running.
    // loop() This method will be called repeatedly in a loop while this op mode is running.
    // stop() This method will be called when this op mode is first disabled
    // requestOpModeStop() Shutdown the current OpMode
    // resetStartTime() set time to 0
    // internalPreInit(); internalPostInitLoop(); internalPostLoop();


    @Override
    public void init() {
        //set up hardware map
        leftWheel = hardwareMap.get(DcMotor.class, LEFT_WHEEL);
        rightWheel = hardwareMap.get(DcMotor.class, RIGHT_WHEEL);
        lift = hardwareMap.get(DcMotor.class, LIFT_MOTOR);
        leftServo = hardwareMap.servo.get(LEFT_SERVO);
        rightServo = hardwareMap.servo.get(RIGHT_SERVO);
        upServo = hardwareMap.servo.get(UP_SERVO);
        downServo = hardwareMap.servo.get(DOWN_SERVO);

        if (enCoderMode) {
            lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        } else {
            lift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        //init mode
        leftServo.setDirection(Servo.Direction.FORWARD);
        rightServo.setDirection(Servo.Direction.REVERSE);
        leftWheel.setDirection(DcMotor.Direction.REVERSE);
        rightWheel.setDirection(DcMotor.Direction.FORWARD);
        lift.setDirection(DcMotor.Direction.FORWARD);

        telemetry.addData("Status", "Initialized!");
        telemetry.addData("Encoder", "NULL");
    }

    @Override
    public void init_loop() {
    }

    @Override
    public void start() {
        resetStartTime(); // reset time in father OpMode
    }

    @Override
    public void loop() {

        //fetch data
        drive = -gamepad1.left_stick_y; //left analog stick vertical axis
        turn = gamepad1.right_stick_x; //left analog stick horizontal axis
        up = gamepad2.left_trigger;
        down = (gamepad2.left_bumper) ? -1f:0f;
        armForce = (up + down)/2;
        leftClaw = (gamepad2.left_stick_y+0.5f-leftClawInputAdjust)/2;
        rightClaw = (gamepad2.right_stick_y+0.5f-rightClawInputAdjust)/2;

//        upServoLeft = gamepad2.right_trigger;
//        upServoRight = (gamepad2.right_bumper) ? -1f:0f;
//        upServoPosition = (upServoLeft+upServoRight)/2;
//
//        downServoLeft = (gamepad2.y) ? 1f:0f;
//        downServoRight = (gamepad2.a) ? -1f:0f;
//        downServoPosition = (downServoLeft+downServoRight)/2;

        //calculate data
        leftWheelPower = Range.clip(drive + turn, -1.0, 1.0) ;
        rightWheelPower = Range.clip(drive - turn, -1.0, 1.0) ;
        upLiftPower = Range.clip(armForce, -1.0, 1.0);

        // Send data to wheels
        leftWheel.setPower(leftWheelPower);
        rightWheel.setPower(rightWheelPower);
        lift.setPower(upLiftPower);
        leftServo.setPosition(leftClaw);
        rightServo.setPosition(rightClaw);
        upServo.setPosition(upServoPosition);
        downServo.setPosition(downServoPosition);

        if(gamepad2.x) {
            leftClawInputAdjust = -gamepad2.left_stick_y;
            telemetry.addData("Adjust: ", String.valueOf(leftClawInputAdjust), ", ", String.valueOf(rightClawInputAdjust));
            telemetry.update();
        }
        if(gamepad2.b) {
            rightClawInputAdjust = -gamepad2.right_stick_y;
            telemetry.addData("Adjust: ", String.valueOf(leftClawInputAdjust), ", ", String.valueOf(rightClawInputAdjust));
            telemetry.update();
        }

        // Show the elapsed game time and wheel power.
        telemetry.addData("Status", "Run Time: " + String.valueOf(time));
        telemetry.addData("Motors", "leftPower (%.2f), rightPower (%.2f)", leftWheelPower, rightWheelPower);
        telemetry.addData("Servo: ", String.valueOf(armForce));
        telemetry.addData("PowerLeftHand: ", String.valueOf(gamepad2.left_stick_y));
        telemetry.addData("PowerRightHand: ", String.valueOf(gamepad2.right_stick_y));
        telemetry.addData("Encoder", lift.getCurrentPosition());
        telemetry.update();
    }

    @Override
    public void stop() {
    }

}
