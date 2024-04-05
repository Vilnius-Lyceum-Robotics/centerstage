package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.sun.tools.javac.util.List;

import java.util.ArrayList;

public class Lift {
    DcMotor liftMotor;
    TouchSensor limitSwitch;
    private int extendedComponentId;
    private static final ArrayList extensionValues = new ArrayList<>(List.of(0, 1, 2));


    public Lift(HardwareMap hardwareMap) {
        liftMotor = hardwareMap.get(DcMotor.class, "liftMotor");
        liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        limitSwitch = hardwareMap.get(TouchSensor.class, "limitSwitch");
        // reset encoder
        liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        extendedComponentId = 0;
    }

    public void extend(){
        if(extendedComponentId > extensionValues.size()) {
            return;
        }
        extendedComponentId++;
    }

    public void retract(){
        if(extendedComponentId < 0) {
            return;
        }
        extendedComponentId--;
    }

    public void run() {
        if(extendedComponentId == 0 && liftMotor.getCurrentPosition() == 0){
            return;
        }
        else if(extendedComponentId == 0 && limitSwitch.isPressed()) {
            liftMotor.setPower(0);
            liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        }
        liftMotor.setTargetPosition(extensionValues.indexOf(extendedComponentId));
        liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        liftMotor.setPower(1);
    }
}