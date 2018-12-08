/* Copyright (c) 2018 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.team13813;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import java.util.List;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

/**
 * This 2018-2019 OpMode illustrates the basics of using the TensorFlow Object Detection API to
 * determine the position of the gold and silver minerals.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list.
 *
 * IMPORTANT: In order to use this OpMode, you need to obtain your own Vuforia license key as
 * is explained below.
 */
@TeleOp(name = "Concept: TensorFlow Object Detection Webcam", group = "Concept")
public class KokiAutoNext extends OpMode {
    private boolean enCoderMode = true;

    // Declare OpMode members.
    // 1000ticks = 1sec

    String state = ""

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


    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";
    private static final String VUFORIA_KEY = "AYVp8HD/////AAAAmbWvbJCPAUEivTbJDYmkKlUhuPRlEN5MxRGtGpK68YAYgdTUSycNhLm/AQ2nxYbFwiX+eiXtdzMQg/h0/OO0uHdiq2AGB9qus774oqnqQ2DrzfdUARClxtcnFwJw3Ba/tyvP/gxWjMWetKcwfdDAjD+dilVMrqS7ePsZZPzjSaNB/kjaP3yQRTN1D/050KdnxwKicMkqhulqKv1miESfNBm7qQd3h9FZJoVZumqfytS7pMmqAjvSN7TGcQw7vxw7DJAECvRfoFhuszWNjwcF3rwRsQEXr1jynbJvhh8z4SJdJDqIK4EEroLLSpHVTYj9si4xULph02bAc2fUXDPMS/g7VfFZcgKuzFvZ/eR3ZHCm";
    private VuforiaLocalizer vuforia;
    private TFObjectDetector tfod;

    /**
     * Initialize the Vuforia localization engine.
     */
    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the Tensor Flow Object Detection engine.
    }

    /**
     * Initialize the Tensor Flow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);
    }



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



        initVuforia();

        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            initTfod();
        } else {
            telemetry.addData("Sorry!", "This device is not compatible with TFOD");
        }
    }

    @Override
    public void init_loop() {

    }

    @Override
    public void start() {
        resetStartTime(); // reset time in father OpMode
        if (tfod != null) {
            tfod.activate();
        }
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


        detectObject();
    }

    @Override
    public void stop() {

        if (tfod != null) {
            tfod.shutdown();
        }
    }

    private void detectObject() {
        if (tfod != null) {
            // getUpdatedRecognitions() will return null if no new information is available since
            // the last time that call was made.
            List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
            if (updatedRecognitions != null) {
                telemetry.addData("# Object Detected", updatedRecognitions.size());
                if (updatedRecognitions.size() == 3) {
                    int goldMineralX = -1;
                    int silverMineral1X = -1;
                    int silverMineral2X = -1;
                    for (Recognition recognition : updatedRecognitions) {
                        if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                            goldMineralX = (int) recognition.getLeft();
                        } else if (silverMineral1X == -1) {
                            silverMineral1X = (int) recognition.getLeft();
                        } else {
                            silverMineral2X = (int) recognition.getLeft();
                        }
                    }
                    if (goldMineralX != -1 && silverMineral1X != -1 && silverMineral2X != -1) {
                        if (goldMineralX < silverMineral1X && goldMineralX < silverMineral2X) {
                            telemetry.addData("Gold Mineral Position", "Left");
                        } else if (goldMineralX > silverMineral1X && goldMineralX > silverMineral2X) {
                            telemetry.addData("Gold Mineral Position", "Right");
                        } else {
                            telemetry.addData("Gold Mineral Position", "Center");
                        }
                    }
                }
                telemetry.update();
            }
        }
    }

}