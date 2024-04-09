package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Led {
    private DigitalChannel greenLed;
    private DigitalChannel redLed;

    private enum LedState {
        NONE,
        GREEN,
        RED,
        AMBER
    }
    public Led(HardwareMap hardwareMap, String ledName){
        String greenLedName = "green" + ledName;
        String redLedName = "red" + ledName;
        greenLed = hardwareMap.get(DigitalChannel.class, greenLedName);
        redLed = hardwareMap.get(DigitalChannel.class, redLedName);
    }

    public void setColor(LedState state){
        switch(state){
            case NONE:
                greenLed.setState(false);
                redLed.setState(false);
                break;
            case GREEN:
                greenLed.setState(true);
                redLed.setState(false);
                break;
            case RED:
                greenLed.setState(false);
                redLed.setState(true);
                break;
            case AMBER:
                greenLed.setState(true);
                redLed.setState(true);
                break;
        }
    }
}
