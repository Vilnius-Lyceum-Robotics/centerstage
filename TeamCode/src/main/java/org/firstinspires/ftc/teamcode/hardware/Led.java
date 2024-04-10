package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.helpers.Constants;

public class Led {
    private DigitalChannel greenLed;
    private DigitalChannel redLed;

    private enum LedColor {
        NONE,
        GREEN,
        RED,
        AMBER
    }
    private LedColor currentColor;
    private LedColor prevColor;
    private LedColor newColor;
    private boolean isBlinking;
    private int blinkDuration;
    private int blinkCount;

    private ElapsedTime blinkTimer = new ElapsedTime();
    public Led(HardwareMap hardwareMap, String ledName){
        String greenLedName = "green" + ledName;
        String redLedName = "red" + ledName;
        greenLed = hardwareMap.get(DigitalChannel.class, greenLedName);
        redLed = hardwareMap.get(DigitalChannel.class, redLedName);

        currentColor = LedColor.NONE;
        isBlinking = false;
    }
    public void setColor(LedColor state){
        currentColor = state;
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

    public void process() {
        if (isBlinking) {
            if (blinkCount > 0) {
                if (blinkTimer.milliseconds() >= blinkDuration) {
                    // Toggle LED color
                    if(currentColor == newColor){
                        setColor(prevColor);
                    } else {
                        setColor(newColor);
                    }
                    blinkCount--;
                    blinkTimer.reset();
                }
            } else{
                isBlinking = false;
                setColor(prevColor);
            }
        }
    }

    public void blink(LedColor newColor, int duration, int count){
        prevColor = currentColor;
        this.newColor = newColor;
        blinkTimer.reset();
        blinkCount = count;
        blinkDuration = duration;
        isBlinking = true;
    }

}
