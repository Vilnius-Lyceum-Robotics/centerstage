package org.firstinspires.ftc.teamcode.helpers;

public class ModeManager {
    public enum Mode {
        NORMAL,
        BACKBOARD
    }

    private static Mode currentMode = Mode.NORMAL;

    public ModeManager(){
        currentMode = Mode.NORMAL;
    }

    private static void setMode(Mode mode){
        currentMode = mode;
    }

    public static void setNormalMode(){
        setMode(Mode.NORMAL);
    }
    public static void setBackboardMode(){
        setMode(Mode.BACKBOARD);
    }
    public static void toggleMode(){
        if(currentMode == Mode.NORMAL){
            setBackboardMode();
        }
        else{
            setNormalMode();
        }
    }
    public static Mode getMode(){
        return currentMode;
    }
}
