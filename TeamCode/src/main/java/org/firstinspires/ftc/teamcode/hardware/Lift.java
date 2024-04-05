package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import com.sun.tools.javac.util.List;
import java.util.ArrayList;

public class Lift {
    private DcMotor liftMotor;
    private TouchSensor limitSwitch;
    private Claw claw;
    private int extendedComponentId;
    private static final ArrayList extensionValues = new ArrayList<Integer>(List.of(0, 100, 1160, 1500, 1900, 2300, 2700, 3100, 3500, 3900, 4300));

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
        if(extendedComponentId <= 1){
            claw.rotatorDown();
        }
        else{
            claw.rotatorUp();
        }
        liftMotor.setTargetPosition(extensionValues.get(extendedComponentId));
        liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        liftMotor.setPower(1);
    }
}