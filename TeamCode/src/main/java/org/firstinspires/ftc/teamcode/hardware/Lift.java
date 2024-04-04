package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Lift {
    DcMotor liftMotor;
    private static final int TICKS_PER_REVOLUTION = 28;
    private static final int REVOLUTIONS_UNTIL_COMPONENT_EXTENSION = 1000;
    private int extendedComponentId;

    public Lift(HardwareMap hardwareMap) {
        liftMotor = hardwareMap.get(DcMotor.class, "liftMotor");
        liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        // reset encoder
        liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        liftMotor.setTargetPosition(0);
        liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        
        extendedComponentId = 0;
    }

    public void extendComponent(){
        extendedComponentId++;
        liftMotor.setTargetPosition(extendedComponentId * REVOLUTIONS_UNTIL_COMPONENT_EXTENSION * TICKS_PER_REVOLUTION);
        liftMotor.setPower(1);
    }

    public void retract() {
        liftMotor.setTargetPosition(0);
        while(liftMotor.isBusy()){
            // if has already reached end of extension, stop
        }
    }
}