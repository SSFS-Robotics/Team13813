package org.firstinspires.ftc.team13813;

/**
 * Created by Koke_Cacao on 2018/1/18.
 */

import android.graphics.Bitmap;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.vuforia.HINT;
import com.vuforia.Image;
import com.vuforia.PIXEL_FORMAT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

@Autonomous(name = "KokiAuto", group = "Autonomous")
public class KokiAuto extends LinearOpMode {

    private boolean enCoderMode = true;
    // maybe the number could be wrong
    private final int encRotation = 1440;

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private ElapsedTime runtimeAfterStart = new ElapsedTime();
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

    private VuforiaLocalizer vuforiaLocalizer;
    private VuforiaLocalizer.Parameters parameters;
    private VuforiaTrackables visionTargets;
    private VuforiaTrackable target;
    private VuforiaTrackableDefaultListener listener;

    private RelicRecoveryVuMark vuMark = null;

    private Integer height;
    private Integer width;

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
        setupOpenCV();
        setupVuforia();
        lastKnownLocation = createMatrix(0, 0, 0, 0, 0, 0);
        waitForStart();

        // Start tracking the targets
        visionTargets.activate();

        while((opModeIsActive()) && (runtime.time() < 5000)) {
            runVuforia();

            // grabbing frames to mRgba for OpenCV
            VuforiaLocalizer.CloseableFrame frame = vuforiaLocalizer.getFrameQueue().take(); //takes the frame at the head of the queue
            long numImages = frame.getNumImages();
            for (int i = 0; i < numImages; i++) {
                if (frame.getImage(i).getFormat() == PIXEL_FORMAT.RGB565) {
                    rgb = frame.getImage(i);
                    height = rgb.getHeight();
                    width = rgb.getWidth();
                    break;
                }
            }
            Bitmap bm = Bitmap.createBitmap(rgb.getWidth(), rgb.getHeight(), Bitmap.Config.RGB_565);
            bm.copyPixelsFromBuffer(rgb.getPixels());
            Mat tmp = new Mat(rgb.getWidth(), rgb.getHeight(), CvType.CV_8UC4);
            Utils.bitmapToMat(bm, tmp);
            mRgba = tmp;

            //close the frame, prevents memory leaks and crashing
            bm.recycle();
            tmp.release();
            frame.close();
            telemetry.update();
            //idle to let hardware catch up
            idle();
        }

    }

    private void setupMotor() {
        leftWheel = hardwareMap.get(DcMotor.class, LEFT_WHEEL);
        rightWheel = hardwareMap.get(DcMotor.class, RIGHT_WHEEL);
        lift = hardwareMap.get(DcMotor.class, LIFT_MOTOR);

        if (enCoderMode) {
            leftWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        } else {
            leftWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        //init mode
        leftWheel.setDirection(DcMotor.Direction.FORWARD);
        rightWheel.setDirection(DcMotor.Direction.REVERSE);
        lift.setDirection(DcMotor.Direction.FORWARD);
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

}