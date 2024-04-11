package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public class LedOld {
    private DigitalChannel greenLed;
    private DigitalChannel redLed;

    public enum LedColor {
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
    private boolean blinkForever;

    private ElapsedTime blinkTimer = new ElapsedTime();
    public LedOld(HardwareMap hardwareMap, String ledName){
        String greenLedName = "green" + ledName;
        String redLedName = "red" + ledName;
        greenLed = hardwareMap.get(DigitalChannel.class, greenLedName);
        redLed = hardwareMap.get(DigitalChannel.class, redLedName);

        greenLed.setMode(DigitalChannel.Mode.OUTPUT);
        redLed.setMode(DigitalChannel.Mode.OUTPUT);

        currentColor = LedColor.NONE;
        prevColor = LedColor.NONE;
        newColor = LedColor.NONE;
        isBlinking = false;
    }
    public void setCurrentColor(LedColor state){
        currentColor = state;
    }

    public void test(){
        greenLed.setState(true);
        redLed.setState(false);
    }
    private void setActiveColor(LedColor state){
        //if(currentColor == state) return;
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
            if (blinkCount > 0 || blinkForever) {
                if (blinkTimer.milliseconds() >= blinkDuration) {
                    // Toggle LED color
                    if(currentColor == newColor){
                        //setCurrentColor(prevColor);
                        setActiveColor(prevColor);
                    } else {
                        //setCurrentColor(newColor);
                        setActiveColor(newColor);
                    }
                    blinkCount = blinkForever ? 0 : blinkCount - 1;
                    blinkTimer.reset();
                }
            } else{
                isBlinking = false;
                setActiveColor(prevColor);
                //setCurrentColor(prevColor);
            }
        } else{
            setActiveColor(currentColor);
        }
    }

    public void blinkForEver(LedColor newColor, int duration){
        initBlinking(newColor, duration, true, 0);
    }

    public void blinkForCount(LedColor newColor, int duration, int count){
        initBlinking(newColor, duration, false, count);
    }

    private void initBlinking(LedColor newColor, int duration, boolean forever, int count){
        blinkForever = forever;
        prevColor = currentColor;
        this.newColor = newColor;
        blinkTimer.reset();
        blinkCount = count;
        blinkDuration = duration;
        isBlinking = true;
    }

    public void stopBlinking(){
        isBlinking = false;
        blinkCount = 0;
        //setActiveColor(prevColor);
        setCurrentColor(prevColor);
    }
}
