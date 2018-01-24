package org.kokicraft.ftc;

import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegister;

public class FtcOpModeRegister implements OpModeRegister {

    public void register(OpModeManager manager) {
        manager.register("KokiOpMode", KokiOpMode.class);
    }
}
