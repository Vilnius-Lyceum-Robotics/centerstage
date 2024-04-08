package org.firstinspires.ftc.teamcode.hardware;

import static java.lang.Thread.sleep;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.Arrays;
import java.util.ArrayList;

public class Lift {
    private DcMotor liftMotor;
    private TouchSensor limitSwitch;
    private Claw claw;
    private boolean clawWasDown;
    private static final int CALL_INTERVAL = 4;
    private static final int LIFT_TIMEOUT = 2900;
    private int currentTimeout; 
    private int extendedComponentId;
    private static final ArrayList<Integer> extensionValues = new ArrayList<>(Arrays.asList(0, 100, 1160, 1500, 1900, 2300, 2700, 3100, 3500));

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
        if (extendedComponentId == 1) {
            claw.setLeftPos(Claw.ClawState.CLOSED);
            claw.setRightPos(Claw.ClawState.CLOSED);
        }
        extendedComponentId++;
    }

    public void retract(){
        if(extendedComponentId <= 0) {
            return;
        }
        if (extendedComponentId == 2) {
            claw.setLeftPos(Claw.ClawState.CLOSED);
            claw.setRightPos(Claw.ClawState.CLOSED);
        }
        extendedComponentId--;
    }

    public void setExtension(int id) {
        if (id < 0 || id >= extensionValues.size()) {
            return;
        }
        extendedComponentId = id;
    }

    public void process() {
        if(extendedComponentId == 0 && limitSwitch.isPressed()) {
            liftMotor.setPower(0);
            liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            return;
        }
        if (extendedComponentId <= 1) {
            claw.rotatorDown();
            if (!clawWasDown) {
                clawWasDown = true;
                currentTimeout = LIFT_TIMEOUT;
            }
        }
        else {
            claw.rotatorUp();
            clawWasDown = false;
        }
        if(currentTimeout > 0) {
            currentTimeout -= CALL_INTERVAL;
            return;
        }

        liftMotor.setTargetPosition(-((Integer) extensionValues.get(extendedComponentId)));
        liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        liftMotor.setPower(1);
    }

    public class AutonomousLift implements Action {
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            process();
            return true;
        }
    }

    public Action autonomous() {
        // Lift loop for autonomous using Roadrunner
        // https://rr.brott.dev/docs/v1-0/actions/

        return new AutonomousLift();
    }
}