package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Led {
    private DigitalChannel greenLed;
    private DigitalChannel redLed;

    private enum LedColor {
        NONE,
        GREEN,
        RED,
        AMBER
    }
    private LedColor currentColor = LedColor.NONE;
    public Led(HardwareMap hardwareMap, String ledName){
        String greenLedName = "green" + ledName;
        String redLedName = "red" + ledName;
        greenLed = hardwareMap.get(DigitalChannel.class, greenLedName);
        redLed = hardwareMap.get(DigitalChannel.class, redLedName);
    }

    public void setColor(LedColor state){
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
        currentColor = state;
    }
    public void blink(LedColor newColor, int duration, int count) {
        LedColor previousColor = currentColor;
        Thread thread = new Thread(() -> {
            try {
                for (int i = 0; i < count; i++) {
                    setColor(newColor);
                    Thread.sleep(duration);
                    setColor(previousColor);
                    Thread.sleep(duration);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                setColor(previousColor);
            }
        });
        thread.start();
    }
}
