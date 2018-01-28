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
    private boolean enCoderMode = false;

    // Declare OpMode members.
    // 1000 = 1sec
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftWheel = null;
    private DcMotor rightWheel = null;
    private DcMotor lift = null;
    private Servo leftServo = null;
    private Servo rightServo = null;

    String LEFT_WHEEL = "m2";
    String RIGHT_WHEEL = "m3";
    String LIFT_MOTOR = "m1";
    String LEFT_SERVO = "sev1";
    String RIGHT_SERVO = "sev2";

    double leftWheelPower;
    double rightWheelPower;
    double upLiftPower;

    double drive;
    double turn;
    float up;
    float down;
    float armForce;
    float leftClaw;
    float rightClaw;

    float leftClawInputAdjust = 0;
    float rightClawInputAdjust = 0;

    @Override
    public void init() {
        //set up hardware map
        leftWheel = hardwareMap.get(DcMotor.class, LEFT_WHEEL);
        rightWheel = hardwareMap.get(DcMotor.class, RIGHT_WHEEL);
        lift = hardwareMap.get(DcMotor.class, LIFT_MOTOR);
        leftServo = hardwareMap.servo.get(LEFT_SERVO);
        rightServo = hardwareMap.servo.get(RIGHT_SERVO);

        if (enCoderMode) {
            leftWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        } else {
            leftWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        //init mode
        leftServo.setDirection(Servo.Direction.FORWARD);
        rightServo.setDirection(Servo.Direction.REVERSE);
        leftWheel.setDirection(DcMotor.Direction.REVERSE);
        rightWheel.setDirection(DcMotor.Direction.FORWARD);
        lift.setDirection(DcMotor.Direction.FORWARD);

        telemetry.addData("Status", "Initialized Yoooo!");
    }

    @Override
    public void init_loop() {
    }
    @Override
    public void start() {
        runtime.reset();
    }

    @Override
    public void loop() {

        //fetch data
        drive = -gamepad1.left_stick_y; //left analog stick vertical axis
        turn = gamepad1.right_stick_x; //left analog stick horizontal axis
        up = gamepad2.left_trigger;
        down = (gamepad2.left_bumper) ? -1f:0f;
        armForce = (up + down)/2;
        leftClaw = (gamepad2.left_stick_y+0.5f+leftClawInputAdjust)/2;
        rightClaw = (gamepad2.right_stick_y+0.5f+rightClawInputAdjust)/2;

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

        if(gamepad2.x) {
            leftClawInputAdjust = gamepad2.left_stick_y;
            telemetry.addData("Adjust: ", String.valueOf(leftClawInputAdjust), String.valueOf(rightClawInputAdjust));
            telemetry.update();
        }
        if(gamepad2.b) {
            rightClawInputAdjust = gamepad2.right_stick_y;
            telemetry.addData("Adjust: ", String.valueOf(leftClawInputAdjust), String.valueOf(rightClawInputAdjust));
            telemetry.update();
        }

        // Show the elapsed game time and wheel power.
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Motors", "leftPower (%.2f), rightPower (%.2f)", leftWheelPower, rightWheelPower);
        telemetry.addData("Servo: ", String.valueOf(armForce));
        telemetry.update();
    }
    @Override
    public void stop() {
    }

}
