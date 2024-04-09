package org.firstinspires.ftc.teamcode.hardware;

import static java.lang.Thread.sleep;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class Lift {
    private DcMotor liftMotor;
    private TouchSensor limitSwitch;
    private Claw claw;
    private Chassis chassis;
    private DistanceSensors distanceSensors;
    private static final int CALL_INTERVAL = 4; // ms
    private static final int LIFT_TIMEOUT = 2900; // ms * CALL_INTERVAL
    private static final int FREE_DISTANCE = 6; // // the distance from the backboard needed to freely use the lift (in inches)
    private int currentTimeout; 
    private int extendedComponentId;
    private static final ArrayList<Integer> extensionValues = new ArrayList<>(Arrays.asList(0, 100, 1160, 1500, 1900, 2300, 2700, 3100, 3500));

    public AtomicBoolean shouldContinueAutonomousLoop = new AtomicBoolean(true);

    public Lift(HardwareMap hardwareMap, Claw claw, Chassis chassis, DistanceSensors distanceSensors){
        liftMotor = hardwareMap.get(DcMotor.class, "liftMotor");
        liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        limitSwitch = hardwareMap.get(TouchSensor.class, "limitSwitch");
        // reset encoder
        liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        extendedComponentId = 0;

        this.claw = claw;
        this.chassis = chassis;
        this.distanceSensors = distanceSensors;
    }

    public void extend(){
        if(extendedComponentId >= extensionValues.size() - 1) {
            return;
        }
        if (extendedComponentId == 1) {
            claw.setLeftPos(Claw.ClawState.CLOSED);
            claw.setRightPos(Claw.ClawState.CLOSED);
            chassis.setBackboardMode();
        }
        extendedComponentId++;
    }

    public void retract(){
        if(extendedComponentId <= 0 || (distanceSensors.getMinDistance() >= FREE_DISTANCE && chassis.getMode() == Chassis.Mode.BACKBOARD)) {
            return;
        }
        if (extendedComponentId == 2) {
            claw.setLeftPos(Claw.ClawState.CLOSED);
            claw.setRightPos(Claw.ClawState.CLOSED);
            chassis.setNormalMode();
        }
        extendedComponentId--;
    }

    public void setExtension(int id) {
        if (id < 0 || id >= extensionValues.size()) {
            return;
        }
        extendedComponentId = id;
    }

    public void process(int CI) {
        if(extendedComponentId == 0 && limitSwitch.isPressed()) {
            liftMotor.setPower(0);
            liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            return;
        }
        if (extendedComponentId <= 1) {
            claw.rotatorDown();
            if (claw.getClawState() == Claw.ClawState.UP) {
                claw.setClawState(Claw.ClawState.DOWN);
                currentTimeout = LIFT_TIMEOUT;
            }
        }
        else {
            claw.rotatorUp();
            claw.setClawState(Claw.ClawState.UP);
        }
        if(currentTimeout > 0) {
            currentTimeout -= CI;
            return;
        }

        liftMotor.setTargetPosition(-((Integer) extensionValues.get(extendedComponentId)));
        liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        liftMotor.setPower(1);
    }

    public void process() {
        process(CALL_INTERVAL);
    }

    public class AutonomousLift implements Action {
        Supplier distanceProcessor;
        public AutonomousLift(Supplier distanceProcess) {
            super();
            distanceProcessor = distanceProcess;
        }
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            process(40);
            distanceProcessor.get();
            return shouldContinueAutonomousLoop.get();
        }
    }

    public Action autonomous(Supplier distanceProcess) {
        // Lift loop for autonomous using Roadrunner
        // https://rr.brott.dev/docs/v1-0/actions/

        // Also run distance sensor processing in the same loop
        // Saves on having to write this action two times
        // todo maybe export to a seperate class made just for this purpose?

        shouldContinueAutonomousLoop.set(true);
        return new AutonomousLift(distanceProcess);
    }
}