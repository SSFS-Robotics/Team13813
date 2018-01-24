package org.kokicraft.ftc;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="KokiOpMode", group="TeleOp")
//@Disabled
public class KokiOpMode extends OpMode {
    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftWheel = null;
    private DcMotor rightWheel = null;
    private DcMotor lift = null;
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
    double up;

    @Override
    public void init() {
        telemetry.addData("Status", "Initialized");

        //set up hardware map
        leftWheel = hardwareMap.get(DcMotor.class, LEFT_WHEEL);
        rightWheel = hardwareMap.get(DcMotor.class, RIGHT_WHEEL);
        lift = hardwareMap.get(DcMotor.class, LIFT_MOTOR);

        //init mode
        leftWheel.setDirection(DcMotor.Direction.FORWARD);
        rightWheel.setDirection(DcMotor.Direction.REVERSE);
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
        up = gamepad2.left_stick_y;

        //calculate data
        leftWheelPower = Range.clip(drive + turn, -1.0, 1.0) ;
        rightWheelPower = Range.clip(drive - turn, -1.0, 1.0) ;
        upLiftPower = Range.clip(up, -1.0, 1.0);
        // leftPower  = -gamepad1.left_stick_y ;
        // rightPower = -gamepad1.right_stick_y ;

        // Send data to wheels
        leftWheel.setPower(leftWheelPower);
        rightWheel.setPower(rightWheelPower);
        lift.setPower(upLiftPower);

        // Show the elapsed game time and wheel power.
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Motors", "leftPower (%.2f), rightPower (%.2f)", leftWheelPower, rightWheelPower);
    }
    @Override
    public void stop() {
    }

}
