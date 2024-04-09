package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Led {
    private DigitalChannel leftLed;
    private DigitalChannel rightLed;

    private enum LedState {
        NONE,
        GREEN,
        RED
    }
    public Led(HardwareMap hardwareMap){
        leftLed = hardwareMap.get(DigitalChannel.class, "leftLed");
        rightLed = hardwareMap.get(DigitalChannel.class, "rightLed");
    }



}
