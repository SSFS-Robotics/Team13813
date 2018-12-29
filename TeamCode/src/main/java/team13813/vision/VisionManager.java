package team13813.vision;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;

import team13813.Configuration;
import team13813.state.GoldPositions;

public class VisionManager {
    private MasterVision vision;

    public VisionManager(HardwareMap hardwareMap, MasterVision.TFLiteAlgorithm infer) {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        parameters.vuforiaLicenseKey = "AYVp8HD/////AAAAmbWvbJCPAUEivTbJDYmkKlUhuPRlEN5MxRGtGpK68YAYgdTUSycNhLm/AQ2nxYbFwiX+eiXtdzMQg/h0/OO0uHdiq2AGB9qus774oqnqQ2DrzfdUARClxtcnFwJw3Ba/tyvP/gxWjMWetKcwfdDAjD+dilVMrqS7ePsZZPzjSaNB/kjaP3yQRTN1D/050KdnxwKicMkqhulqKv1miESfNBm7qQd3h9FZJoVZumqfytS7pMmqAjvSN7TGcQw7vxw7DJAECvRfoFhuszWNjwcF3rwRsQEXr1jynbJvhh8z4SJdJDqIK4EEroLLSpHVTYj9si4xULph02bAc2fUXDPMS/g7VfFZcgKuzFvZ/eR3ZHCm";

        vision = new MasterVision(parameters, hardwareMap, true, infer);
        vision.init();
    }//facing

    public void start() {
        vision.enable();
    }

    private void end() {
        vision.disable();
    }

    public GoldPositions fetch() {
        GoldPositions goldPosition = vision.getTfLite().getLastKnownSampleOrder();
        Configuration.setGoldPosition(goldPosition);
        return goldPosition;
    }

    public void disable() {
        vision.shutdown();
    }
}

