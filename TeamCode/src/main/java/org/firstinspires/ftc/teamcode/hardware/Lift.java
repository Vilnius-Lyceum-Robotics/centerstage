package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.Arrays;
import java.util.ArrayList;

public class Lift {
    private DcMotor liftMotor;
    private TouchSensor limitSwitch;
    private Claw claw;
    private bool clawWasDown;
    private static final int CALL_INTERVAL = 20; // 20 milliseconds
    private static final int LIFT_TIMEOUT = 1000 * 20; // 20 seconds
    private int currentTimeout; 
    private int extendedComponentId;
    // private static final ArrayList extensionValues = new ArrayList<Integer>(List.of(0, 100, 1160, 1500, 1900, 2300, 2700, 3100, 3500, 3900, 4300));
    private static final ArrayList<Integer> extensionValues = new ArrayList<>(Arrays.asList(0, 100, 1160, 1500, 1900, 2300, 2700, 3100, 3500, 3900, 4300));

    public Lift(HardwareMap hardwareMap, Claw inheritedClaw) {
        liftMotor = hardwareMap.get(DcMotor.class, "liftMotor");
        liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        limitSwitch = hardwareMap.get(TouchSensor.class, "limitSwitch");
        claw = inheritedClaw;
        // reset encoder
        liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        extendedComponentId = 0;
    }

    public void extend(){
        if(extendedComponentId >= extensionValues.size() - 1) {
            return;
        }
        extendedComponentId++;
    }

    public void retract(){
        if(extendedComponentId <= 0) {
            return;
        }
        extendedComponentId--;
    }

    public void run() {
        if(extendedComponentId == 0 && limitSwitch.isPressed()) {
            liftMotor.setPower(0);
            liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            return;
        }
        if(extendedComponentId <= 1 && !clawWasDown){
            claw.rotatorDown();
            clawWasDown = true;
            currentTimeout = LIFT_TIMEOUT;
        }
        else if(clawWasDown){
            claw.rotatorUp();
            clawWasDown = false;
            currentTimeout = LIFT_TIMEOUT;
        }
        if(currentTimeout > 0){
            currentTimeout-=CALL_INTERVAL;
            return;
        }
        
        liftMotor.setTargetPosition(-((Integer) extensionValues.get(extendedComponentId)));
        liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        liftMotor.setPower(1);
    }


}