package org.firstinspires.ftc.team13813;

/**
 * Created by Koke_Cacao on 2018/1/18.
 */

import android.graphics.Bitmap;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.vuforia.HINT;
import com.vuforia.Image;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcontroller.internal.*;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

@Disabled
@Autonomous(name = "KokiAuto", group = "Autonomous")
public class KokiAuto extends LinearOpModeCamera {

    private boolean encoderMode = true;
    // maybe the number could be wrong, tetrix:1440, andymark:1120
    private final int encRotation = 1440;

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private ElapsedTime runtimeAfterStart = new ElapsedTime();

    private DcMotor leftWheel = null;
    private DcMotor rightWheel = null;
    private DcMotor lift = null;
    private Servo leftServo = null;
    private Servo rightServo = null;
    private Servo upServo = null;
    private Servo downServo = null;

    String LEFT_WHEEL = "m2";
    String RIGHT_WHEEL = "m3";
    String LIFT_MOTOR = "m1";
    String LEFT_SERVO = "sev1";
    String RIGHT_SERVO = "sev2";
    String DOWN_SERVO = "sev3";
    String UP_SERVO = "sev4";

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
    float upServoLeft;
    float upServoRight;
    float upServoPosition;
    float downServoLeft;
    float downServoRight;
    float downServoPosition;

    float leftClawInputAdjust = 0.123f;
    float rightClawInputAdjust = 0.240f;

    private VuforiaLocalizer vuforiaLocalizer;
    private VuforiaLocalizer.Parameters parameters;
    private VuforiaTrackables visionTargets;
    private VuforiaTrackable target;
    private VuforiaTrackableDefaultListener listener;

    private RelicRecoveryVuMark vuMark = null;

    private Integer height = 45;
    private Integer width = 80;

    private Image rgb;
    private Mat mRgba;
    private Mat mIntermediateMat;
    private Mat mGray;
    private Mat mHSV;
    private Mat mThresholded;
    private Mat mThresholded2;
    private Mat array255;
    private Mat distance;

    private OpenGLMatrix lastKnownLocation;
    private OpenGLMatrix phoneLocation;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(hardwareMap.appContext) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    // Load native library after(!) OpenCV initialization
                    //System.loadLibrary("mixed_sample");
//                    mOpenCvCameraView.enableView();
                } break;
                default: {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    private static final String VUFORIA_KEY = "AYVp8HD/////AAAAmbWvbJCPAUEivTbJDYmkKlUhuPRlEN5MxRGtGpK68YAYgdTUSycNhLm/AQ2nxYbFwiX+eiXtdzMQg/h0/OO0uHdiq2AGB9qus774oqnqQ2DrzfdUARClxtcnFwJw3Ba/tyvP/gxWjMWetKcwfdDAjD+dilVMrqS7ePsZZPzjSaNB/kjaP3yQRTN1D/050KdnxwKicMkqhulqKv1miESfNBm7qQd3h9FZJoVZumqfytS7pMmqAjvSN7TGcQw7vxw7DJAECvRfoFhuszWNjwcF3rwRsQEXr1jynbJvhh8z4SJdJDqIK4EEroLLSpHVTYj9si4xULph02bAc2fUXDPMS/g7VfFZcgKuzFvZ/eR3ZHCm";
    private static final String TRAINED_MODEL = "RelicVuMark";
    private static final VuforiaLocalizer.CameraDirection CAMERA_DIRECTION = VuforiaLocalizer.CameraDirection.BACK;
    private static final boolean useExtendedTracking = false;
    private static final Integer relicID = 0;
    private static final boolean cameraTesting = true;

    private float robotX = 0;
    private float robotY = 0;
    private float robotAngle = 0;

    //added overide, do not know if it should be here
    @Override
    public void runOpMode() throws InterruptedException {

        setupMotor();
        setupVuforia();
        lastKnownLocation = createMatrix(0, 0, 0, 0, 0, 0);

        // Start tracking the targets in Vuforia
        visionTargets.activate();

//        camera = camera.open(1);
//        startCamera();
//        setupOpenCV();

        // you can try to use .addLine
        telemetry.addData("Status", "initialized"); // add data to driver station
        telemetry.update(); // update the message
        waitForStart(); // waiting for start, the program will stop here until we press start

        runtime.reset(); // reset the time elipsed

//        if (imageReady()) {
//            telemetry.addData("Status", "Image ready");
//            telemetry.update();
//            height = yuvImage.getHeight();
//            width = yuvImage.getWidth();
//        }

        while (opModeIsActive()) {
            //fetch data
//            drive = -gamepad1.left_stick_y; //left analog stick vertical axis
//            turn = gamepad1.right_stick_x; //left analog stick horizontal axis
//            up = gamepad2.left_trigger;
//            down = (gamepad2.left_bumper) ? -1f:0f;
//            armForce = (up + down)/2;
//            leftClaw = (gamepad2.left_stick_y+0.5f-leftClawInputAdjust)/2;
//            rightClaw = (gamepad2.right_stick_y+0.5f-rightClawInputAdjust)/2;

//            upServoLeft = gamepad2.right_trigger;
//            upServoRight = (gamepad2.right_bumper) ? -1f:0f;
//            upServoPosition = (upServoLeft+upServoRight)/2;

//            downServoLeft = (gamepad2.y) ? 1f:0f;
//            downServoRight = (gamepad2.a) ? -1f:0f;
//            downServoPosition = (downServoLeft+downServoRight)/2;



            if((opModeIsActive()) && (runtime.seconds() >= 0) && (runtime.seconds() <= 12)) {
                if ((int)runtime.seconds() == 0) {
                    leftClaw = -0.5f;
                    rightClaw = -0.5f;
//                    leftServo.setPosition(-0.5);
//                    rightServo.setPosition(-0.5);
                }
            }
            if((opModeIsActive()) && (runtime.seconds() >= 2) && (runtime.seconds() <= 3)) {
                if ((int)runtime.seconds() == 2) {
//                    liftWithEncoder(0.5f);
                }
                if ((int)runtime.seconds() == 3) {
//                    liftWithEncoder(0);
                }
            }
            //left
            if((opModeIsActive()) && (runtime.seconds() >= 1) && (runtime.seconds() < 3)) {
                if ((int)runtime.seconds() == 1) {
                    upServo.setDirection(Servo.Direction.REVERSE);
                    upServoPosition = 0.5f;
//                    -0.5f/2 if leftleft
                }
            }
            //up
            if((opModeIsActive()) && (runtime.seconds() >= 3) && (runtime.seconds() < 4)) {
                if ((int)runtime.seconds() == 3) {
                    downServo.setDirection(Servo.Direction.FORWARD);
                    downServoPosition = 0;
//                    downServo.setPosition(1);
                }
            }
            //right!!!
            if((opModeIsActive()) && (runtime.seconds() >= 4) && (runtime.seconds() < 5)) {
                if ((int)runtime.seconds() == 4) {
                    downServo.setDirection(Servo.Direction.REVERSE);
                    downServoPosition = -1/2;
//                    downServo.setPosition(1);
                }
            }
            //down!!!!
            if((opModeIsActive()) && (runtime.seconds() >= 5) && (runtime.seconds() < 6)) {
                if ((int)runtime.seconds() == 5) {
                    upServo.setDirection(Servo.Direction.REVERSE);
                    upServoPosition = 0;
//                    upServo.setPosition(-1);
                }
            }
            //left
            if((opModeIsActive()) && (runtime.seconds() >= 6) && (runtime.seconds() < 7)) {
                if ((int)runtime.seconds() == 6) {
                    upServo.setDirection(Servo.Direction.REVERSE);
                    upServoPosition = -0.5f/2;
//                    -0.5f/2 if leftleft
                }
            }
            //up
            if((opModeIsActive()) && (runtime.seconds() >= 7) && (runtime.seconds() < 8)) {
                if ((int)runtime.seconds() == 7) {
                    downServo.setDirection(Servo.Direction.FORWARD);
                    downServoPosition = 0;
//                    downServo.setPosition(1);
                }
            }
            //drive
//            if((opModeIsActive()) && (runtime.seconds() >= 8) && (runtime.seconds() < 10)) {
//                if ((int)runtime.seconds() == 8) {
//                    driveWithEncoders(5, 0.5);
//                }
//            }
            if((opModeIsActive()) && (runtime.seconds() >= 10) && (runtime.seconds() <= 11)) {
                if ((int)runtime.seconds() == 10) {
//                    liftWithEncoder(-0.5f);
                }
                if ((int)runtime.seconds() == 11) {
//                    liftWithEncoder(0);
                }
            }


            //calculate data
            leftWheelPower = Range.clip(drive + turn, -1.0, 1.0) ;
            rightWheelPower = Range.clip(drive - turn, -1.0, 1.0) ;
            upLiftPower = Range.clip(armForce, -1.0, 1.0);

            // send data to wheels
            leftWheel.setPower(leftWheelPower);
            rightWheel.setPower(rightWheelPower);
            lift.setPower(upLiftPower);

            // send data to servos
            leftServo.setPosition(leftClaw);
            rightServo.setPosition(rightClaw);
            upServo.setPosition(upServoPosition);
            downServo.setPosition(downServoPosition);

            // Show the elapsed game time and wheel power.
//        telemetry.addData("Status", "Run Time: " + runtime.toString());
//        telemetry.addData("Motors", "leftPower (%.2f), rightPower (%.2f)", leftWheelPower, rightWheelPower);
//        telemetry.addData("Servo: ", String.valueOf(armForce));
//        telemetry.addData("PowerLeftHand: ", String.valueOf(gamepad2.left_stick_y));
//        telemetry.addData("PowerRightHand: ", String.valueOf(gamepad2.right_stick_y));
            telemetry.update();
            //8:50, panel 1
//
//            leftWheel.setPower(leftWheelPower);
//            rightWheel.setPower(rightWheelPower);
//            lift.setPower(upLiftPower);
//            leftServo.setPosition(leftClaw);
//            rightServo.setPosition(rightClaw);
//            upServo.setPosition(upServoPosition);
//            downServo.setPosition(downServoPosition);
//
//            telemetry.addData("Status", "looping");
//            telemetry.addData("Runtime", runtime.seconds());
//            telemetry.update();
//            middle();
//
//            if((opModeIsActive()) && (runtime.time() > 0) && (runtime.time() < 16)) {
//                pick();
//
//                telemetry.addData("Status", "picking");
//                telemetry.addData("Runtime", runtime.seconds());
//                telemetry.update();
//            }
//            if((opModeIsActive()) && (runtime.time() > 2) && (runtime.time() < 4)) {
//                servoDown();
//                telemetry.addData("Status", "down");
//                telemetry.update();
//            }
//            if((opModeIsActive()) && (runtime.seconds() > 4) && (runtime.seconds() < 6)) {
//                kickLeft();
//                telemetry.addData("Status", "kick");
//                telemetry.update();
//            }
//
//            if((opModeIsActive()) && (runtime.seconds() > 6) && (runtime.seconds() < 8)) {
//                servoUp();
//                telemetry.addData("Status", "up");
//                telemetry.update();
//            }
//            if((opModeIsActive()) && (runtime.seconds() > 8) && (runtime.seconds() < 10)) {
//                kickRight();
//                telemetry.addData("Status", "back");
//                telemetry.update();
//            }
//
//            if((opModeIsActive()) && (runtime.seconds() > 10) && (runtime.seconds() < 16)) {
//                driveWithEncoders(1, 0.5);
//            }
//
//            if((opModeIsActive()) && (runtime.seconds() > 16) && (runtime.seconds() < 20)) {
//                turnLeft(1, 0.5);
//            }
        }
//        release();
//        looping();
    }

    private void looping() throws InterruptedException {
            while((opModeIsActive()) && (runtime.time() < 5000)) {
            runVuforia();
//             grabbing frames to mRgba for OpenCV

//            telemetry.addData("Status", "taking pictures");
            telemetry.update();
            vuforiaLocalizer.setFrameQueueCapacity(1);
            VuforiaLocalizer.CloseableFrame frame = vuforiaLocalizer.getFrameQueue().take(); //takes the frame at the head of the queue
            long numImages = frame.getNumImages();

//            telemetry.addData("Status", "I got a picture");
            telemetry.update();
            for (int i = 0; i < numImages; i++) {

//                telemetry.addData("Status", "Running...");
                telemetry.update();
//                if (frame.getImage(i).getFormat() == PIXEL_FORMAT.RGB565) {
                rgb = frame.getImage(i);
                height = rgb.getHeight();
                width = rgb.getWidth();
                telemetry.addData("Status", String.valueOf(height) + ", " + String.valueOf(width));
                telemetry.update();
//                    break;
//                }
            }

            // Bitmap.Config.RGB_565
            // Bitmap.Config.ARGB_8888
            Bitmap bm = Bitmap.createBitmap(rgb.getBufferWidth()/2, rgb.getBufferHeight()/2, Bitmap.Config.RGB_565);
//            Integer bmbyte = bm.getByteCount(); //14720 or 14400(height)

            ByteBuffer buffer = rgb.getPixels();

//            Integer bufferbyte = buffer.capacity();//3680
//            telemetry.addData("Status", "Testing testing..." + bmbyte + ", " + bufferbyte);
            telemetry.update();
            bm.copyPixelsFromBuffer(buffer);


            telemetry.addData("Status", "Testing good");
            telemetry.update();

//            Mat tmp = new Mat(rgb.getBufferWidth()/2, rgb.getBufferHeight()/2, CvType.CV_8UC4);

//            Utils.bitmapToMat(bm, tmp);
//
            mRgba = bitmapToMatrix(bm);


            telemetry.addData("Status", "okay");
            telemetry.update();
            //close the frame, prevents memory leaks and crashing

            setupOpenCV();
            frame.close();
            //idle to let hardware catch up
            idle();
        }
    }

    private void setupMotor() {
        //set up hardware map
        leftWheel = hardwareMap.get(DcMotor.class, LEFT_WHEEL);
        rightWheel = hardwareMap.get(DcMotor.class, RIGHT_WHEEL);
        lift = hardwareMap.get(DcMotor.class, LIFT_MOTOR);
        leftServo = hardwareMap.servo.get(LEFT_SERVO);
        rightServo = hardwareMap.servo.get(RIGHT_SERVO);
        upServo = hardwareMap.servo.get(UP_SERVO);
        downServo = hardwareMap.servo.get(DOWN_SERVO);

        if (encoderMode) {
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

        //right
        upServo.setDirection(Servo.Direction.FORWARD);
        //down
        downServo.setDirection(Servo.Direction.FORWARD);
    }
    private void setupVuforia() {
        if (cameraTesting) {
            parameters = new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId);
        } else {
            parameters = new VuforiaLocalizer.Parameters();
        }
        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = CAMERA_DIRECTION;
        parameters.useExtendedTracking = useExtendedTracking;

        vuforiaLocalizer = ClassFactory.createVuforiaLocalizer(parameters);

        visionTargets = vuforiaLocalizer.loadTrackablesFromAsset(TRAINED_MODEL);
        Vuforia.setHint(HINT.HINT_MAX_SIMULTANEOUS_IMAGE_TARGETS, 4);

        // Setup the target to be tracked
        target = visionTargets.get(relicID); // 0 corresponds to the wheels target
        target.setName("ID TEST 0");
        target.setLocation(createMatrix(0, 500, 0, 90, 0, 90));

        // Set phone location on robot
        phoneLocation = createMatrix(0, 225, 0, 90, 0, 0);

        // Setup listener and inform it of phone information
        listener = (VuforiaTrackableDefaultListener) target.getListener();
        listener.setPhoneInformation(phoneLocation, parameters.cameraDirection);
    }
    private void setupOpenCV() {
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity.getContext(), mLoaderCallback);
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mHSV = new Mat(height, width, CvType.CV_8UC4);
        mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);
        mGray = new Mat(height, width, CvType.CV_8UC1);
        array255 = new Mat(height, width, CvType.CV_8UC1);
        distance = new Mat(height, width, CvType.CV_8UC1);
        mThresholded = new Mat(height, width, CvType.CV_8UC1);
        mThresholded2 = new Mat(height, width, CvType.CV_8UC1);
    }

    // you should not interrupt this method
    public void driveWithEncoders(double revolutions, double power) throws InterruptedException {
        // How far are we to move, in ticks instead of revolutions?
        int ticks = (int)Math.round(revolutions * encRotation);
        leftWheelPower = power;
        rightWheelPower = power;

        // Tell the motors where we are going
        leftWheel.setTargetPosition(leftWheel.getCurrentPosition() + ticks);
        rightWheel.setTargetPosition(rightWheel.getCurrentPosition() + ticks);

        // Set them a-going
        leftWheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightWheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // Give them the power level we want them to move at
        leftWheel.setPower(leftWheelPower);
        rightWheel.setPower(rightWheelPower);

        // Wait until they are done
        while (opModeIsActive() && (leftWheel.isBusy() || rightWheel.isBusy())) {
            telemetry.update();
            idle();
        }

        // Always leave the screen looking pretty
        telemetry.update();
    }
    public void liftWithEncoder(float power) throws InterruptedException {
        // How far are we to move, in ticks instead of revolutions?
        int ticks = (int)Math.round(1 * encRotation);
        armForce = power;

        // Tell the motors where we are going
        lift.setTargetPosition(lift.getCurrentPosition() + ticks);

        // Set them a-going
        lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // Give them the power level we want them to move at
        lift.setPower(armForce);

        // Wait until they are done
        while (opModeIsActive() && (lift.isBusy() || lift.isBusy())) {
            telemetry.update();
            idle();
        }
        lift.setPower(0);
        // Always leave the screen looking pretty
        telemetry.update();
    }
    public void turnRight(double revolutions, double power) throws InterruptedException {
        // How far are we to move, in ticks instead of revolutions?
        int ticks = (int)Math.round(revolutions * encRotation);
        leftWheelPower = power;
        rightWheelPower = -power;

        // Tell the motors where we are going
        leftWheel.setTargetPosition(leftWheel.getCurrentPosition() + ticks);
        rightWheel.setTargetPosition(rightWheel.getCurrentPosition() + ticks);

        // Set them a-going
        leftWheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightWheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // Give them the power level we want them to move at
        leftWheel.setPower(leftWheelPower);
        rightWheel.setPower(rightWheelPower);

        // Wait until they are done
        while (opModeIsActive() && (leftWheel.isBusy() || rightWheel.isBusy())) {
            telemetry.update();
            idle();
        }

        // Always leave the screen looking pretty
        telemetry.update();
    }
    public void turnLeft(double revolutions, double power) throws InterruptedException {
        // How far are we to move, in ticks instead of revolutions?
        int ticks = (int)Math.round(revolutions * encRotation);
        leftWheelPower = -power;
        rightWheelPower = power;

        // Tell the motors where we are going
        leftWheel.setTargetPosition(leftWheel.getCurrentPosition() + ticks);
        rightWheel.setTargetPosition(rightWheel.getCurrentPosition() + ticks);

        // Set them a-going
        leftWheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightWheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // Give them the power level we want them to move at
        leftWheel.setPower(leftWheelPower);
        rightWheel.setPower(rightWheelPower);

        // Wait until they are done
        while (opModeIsActive() && (leftWheel.isBusy() || rightWheel.isBusy())) {
            telemetry.update();
            idle();
        }

        // Always leave the screen looking pretty
        telemetry.update();
    }
    public void kickLeft() {
        upServo.setDirection(Servo.Direction.REVERSE);
        upServo.setPosition(-1/2);
    }
    public void kickRight() {
        upServo.setDirection(Servo.Direction.FORWARD);
        upServo.setPosition(1/2);
    }
    public void middle() {
        upServo.setDirection(Servo.Direction.FORWARD);
        upServo.setPosition(0);
    }
    public void servoDown() {
        downServo.setPosition(1/2);
    }
    public void servoUp() {
        downServo.setPosition(-1/2);
    }
    public void pick() {
        leftClaw = (-1 +0.5f-0.1f)/2;
        rightClaw = (-1+0.5f-0.2f)/2;
    }
    public void release() {
        leftClaw = (1 +0.5f-0.1f)/2;
        rightClaw = (1+0.5f-0.2f)/2;
    }

    public Mat runOpenCV(Mat mRgba) {
        List<Mat> lhsv = new ArrayList<Mat>(3);
        Mat circles = new Mat();
        array255.setTo(new Scalar(255));

        Imgproc.cvtColor(mRgba, mHSV, Imgproc.COLOR_RGB2HSV, 4);

        // 1=red, 2=blue
        Scalar hsv_min = new Scalar(0, 125, 125, 0);
        Scalar hsv_max = new Scalar(6, 243.859375, 248.15625, 0);
        Scalar hsv_min2 = new Scalar(185 / 2, 125, 125, 0);
        Scalar hsv_max2 = new Scalar(274 / 2, 240.0, 246.96875, 0);
        Core.inRange(mHSV, hsv_min, hsv_max, mThresholded);
        Core.inRange(mHSV, hsv_min2, hsv_max2, mThresholded2);

        Core.bitwise_or(mThresholded, mThresholded2, mThresholded);

        Core.split(mHSV, lhsv); // We get 3 2D one channel Mats
        Mat S = lhsv.get(1);
        Mat V = lhsv.get(2);
        Core.subtract(array255, S, S);
        Core.subtract(array255, V, V);
        S.convertTo(S, CvType.CV_32F);
        V.convertTo(V, CvType.CV_32F);
        Core.magnitude(S, V, distance);
        Core.inRange(distance, new Scalar(0.0), new Scalar(200.0), mThresholded2);
        Core.bitwise_and(mThresholded, mThresholded2, mThresholded);
        Imgproc.GaussianBlur(mThresholded, mThresholded, new Size(9, 9), 0, 0);

        Imgproc.HoughCircles(mThresholded, circles, Imgproc.CV_HOUGH_GRADIENT, 2, mThresholded.height() / 4, 500, 50, 0, 0);
        return mRgba;
    }
    private void detectRedBall() {
        Scalar hsv_min = new Scalar(0, 125, 125, 0);
        Scalar hsv_max = new Scalar(6, 243.859375, 248.15625, 0);
        Core.inRange(mHSV, hsv_min, hsv_max, mThresholded);
        detectBall(mThresholded);
    }
    private void detectBlueBall() {

        Scalar hsv_min = new Scalar(185 / 2, 125, 125, 0);
        Scalar hsv_max = new Scalar(274 / 2, 240.0, 246.96875, 0);
        Core.inRange(mHSV, hsv_min, hsv_max, mThresholded2);
        detectBall(mThresholded2);
    }
    private void detectBall(Mat mThresholded) {
        Mat circles = new Mat();
        array255.setTo(new Scalar(255));

        List<Mat> lhsv = new ArrayList<Mat>(3);
        Core.split(mHSV, lhsv); // We get 3 2D one channel Mats
        Mat S = lhsv.get(1);
        Mat V = lhsv.get(2);
        Core.subtract(array255, S, S);
        Core.subtract(array255, V, V);
        S.convertTo(S, CvType.CV_32F);
        V.convertTo(V, CvType.CV_32F);
        Core.magnitude(S, V, distance);
        Core.inRange(distance, new Scalar(0.0), new Scalar(200.0), mThresholded2);
        Core.bitwise_and(mThresholded, mThresholded2, mThresholded);
        Imgproc.GaussianBlur(mThresholded, mThresholded, new Size(9, 9), 0, 0);
        Imgproc.HoughCircles(mThresholded, circles, Imgproc.CV_HOUGH_GRADIENT, 2, mThresholded.height() / 4, 500, 50, 0, 0);
        if (circles.cols() > 0) {
            for (int x=0; x < Math.min(circles.cols(), 5); x++ ) {
                double circleVec[] = circles.get(0, x);
                if (circleVec == null) {
                    break;
                }

                Point center = new Point((int) circleVec[0], (int) circleVec[1]);
                int radius = (int) circleVec[2];

                Imgproc.circle(mRgba, center, 3, new Scalar(255, 255, 255), 5);
                Imgproc.circle(mRgba, center, radius, new Scalar(255, 255, 255), 2);
            }
        }
    }
    private void runVuforia() {
        detectRelic();
        detectLocation();
    }
    private void detectLocation() {
        // Ask the listener for the latest information on where the robot is
        OpenGLMatrix latestLocation = listener.getUpdatedRobotLocation();

        // The listener will sometimes return null, so we check for that to prevent errors
        if(latestLocation != null) {
            lastKnownLocation = latestLocation;
        }

        float[] coordinates = lastKnownLocation.getTranslation().getData();

        robotX = coordinates[0];
        robotY = coordinates[1];
        robotAngle = Orientation.getOrientation(lastKnownLocation, AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES).thirdAngle;

        // Send information about whether the target is visible, and where the robot is
        //telemetry.addData("Tracking " + target.getName(), listener.isVisible());
        telemetry.addData("Location:", matrixToString(lastKnownLocation));
    }
    private void detectRelic() {
        vuMark = RelicRecoveryVuMark.from(target);
        if (vuMark != RelicRecoveryVuMark.UNKNOWN) {
            telemetry.addData("Mark:", "%s", vuMark);
            //we do not need this code since we do not need to track position
//            OpenGLMatrix pose = ((VuforiaTrackableDefaultListener)target.getListener()).getPose();
//            telemetry.addData("Pose:", matrixToString(pose));
//
//            if (pose != null) {
//                VectorF trans = pose.getTranslation();
//                Orientation rot = Orientation.getOrientation(pose, AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES);
//
//                // Extract the X, Y, and Z components of the offset of the target relative to the robot
//                double tX = trans.get(0);
//                double tY = trans.get(1);
//                double tZ = trans.get(2);
//
//                // Extract the rotational components of the target relative to the robot
//                double rX = rot.firstAngle;
//                double rY = rot.secondAngle;
//                double rZ = rot.thirdAngle;
//            }
        } else {
            telemetry.addData("Mark:", "null");
        }
    }

    // Creates a matrix for determining the locations and orientations of objects
    // Units are millimeters for x, y, and z, and degrees for u, v, and w
    private OpenGLMatrix createMatrix(float x, float y, float z, float u, float v, float w) {
        return OpenGLMatrix.translation(x, y, z).
                multiplied(Orientation.getRotationMatrix(
                        AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES, u, v, w));
    }

    // Formats a matrix into a readable string
    private String matrixToString(OpenGLMatrix matrix) {
        if (matrix != null)
            return matrix.formatAsTransform();
        return "null";
    }

    // Formats a matrix into a readable string
    private String formatMatrix(OpenGLMatrix matrix)
    {
        return matrix.formatAsTransform();
    }

    private Mat bitmapToMatrix(Bitmap bitmap) {
        Mat tmp = new Mat(height, width, CvType.CV_8UC4);

        telemetry.addData("Status", "Alright");
        telemetry.update();
        Utils.bitmapToMat(bitmap, tmp);
        return tmp;
    }
}