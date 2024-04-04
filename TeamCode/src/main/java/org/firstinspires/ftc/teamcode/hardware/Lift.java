package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Lift {
    DcMotor liftMotor;
    TouchSensor limitSwitch;
    private static final int TICKS_PER_REVOLUTION = 28;
    private static final int REVOLUTIONS_UNTIL_COMPONENT_EXTENSION = 1000;
    private int extendedComponentId;
    private static final ArrayList<Integer> extensionValues = new ArrayList<>(List.of(0, 1, 2));


    public Lift(HardwareMap hardwareMap) {
        liftMotor = hardwareMap.get(DcMotor.class, "liftMotor");
        liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        limitSwitch = hardwareMap.get(TouchSensor.class, "limitSwitch");
        // reset encoder
        liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        liftMotor.setTargetPosition(0);
        liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        extendedComponentId = 0;
    }

    public void extendComponent() {
        extendedComponentId++;
        if(extendedComponentId > extensionValues.size()) {
            return;
        }
        liftMotor.setTargetPosition(extensionValues.get(extendedComponentId - 1));
        liftMotor.setPower(1);
    }

    public void retract() {
        liftMotor.setTargetPosition(0);
        while (liftMotor.isBusy()) {
            if(limitSwitch.isPressed()) {
                liftMotor.setPower(0);
                break;
            }
        }
    }
}