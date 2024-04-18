package org.firstinspires.ftc.teamcode.hardware;

import static java.lang.Thread.sleep;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.helpers.BooleanState;
import org.firstinspires.ftc.teamcode.helpers.ModeManager;

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
    private DistanceSensors distanceSensors;
    private static final int CALL_INTERVAL = 4; // ms
    private static final int LIFT_TIMEOUT = 210; // ms
    private ElapsedTime timer;
    private static final int FREE_DISTANCE = 6; // the distance from the backboard needed to freely use the lift (in inches)
    private int currentTimeout;
    private int extendedComponentId;
    private static final ArrayList<Integer> extensionValues = new ArrayList<>(Arrays.asList(15, 100, 1160, 1500, 1900, 2300, 2700, 3100, 3500));
    // dumb but idc
    private boolean firstTime = true;
    public AtomicBoolean shouldContinueAutonomousLoop = new AtomicBoolean(true);
    private boolean encodersEnabled = true;

    private int maxHeight = 1;

    public Lift(HardwareMap hardwareMap, Claw claw, DistanceSensors distanceSensors) {
        liftMotor = hardwareMap.get(DcMotor.class, "liftMotor");
        liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        limitSwitch = hardwareMap.get(TouchSensor.class, "limitSwitch");
        // reset encoder
        liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        extendedComponentId = 0;

        this.claw = claw;
        this.distanceSensors = distanceSensors;
        timer = new ElapsedTime();
    }

    public void extend() {
        if (extendedComponentId >= extensionValues.size() - 1) {
            return;
        }
        if (extendedComponentId == 1) {
            claw.setLeftPos(Claw.ClawState.CLOSED);
            claw.setRightPos(Claw.ClawState.CLOSED);
            ModeManager.setBackboardMode();
        }
        extendedComponentId++;
        if (extendedComponentId > maxHeight) maxHeight = extendedComponentId;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void retract() {
        if (extendedComponentId <= 0) {
            return;
        }
        if (extendedComponentId == 2) {
            claw.setLeftPos(Claw.ClawState.CLOSED);
            claw.setRightPos(Claw.ClawState.CLOSED);
            ModeManager.setNormalMode();
        }
        extendedComponentId--;
    }

    public void setExtension(int id) {
        if (id < 0 || id >= extensionValues.size()) {
            return;
        }
        if ((extendedComponentId < 2 && id >= 2) || (extendedComponentId >= 2 && id < 2)) {
            claw.setLeftPos(Claw.ClawState.CLOSED);
            claw.setRightPos(Claw.ClawState.CLOSED);
        }
        extendedComponentId = id;
    }

    public void process(int CI, BooleanState automaticLift) {
        if (extendedComponentId == 0 && limitSwitch.isPressed()) {
            liftMotor.setPower(0);
            liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            return;
        }
        if (extendedComponentId <= 1) {
            claw.rotatorDown();
            if (claw.getClawState() == Claw.ClawState.UP) {
                claw.setClawState(Claw.ClawState.DOWN);
//                currentTimeout = LIFT_TIMEOUT;
                  timer.reset();
            }
        } else {
            claw.rotatorUp();
            claw.setClawState(Claw.ClawState.UP);
        }
        if (LIFT_TIMEOUT >= timer.milliseconds()) {
            return;
        }

        liftMotor.setTargetPosition(-((Integer) extensionValues.get(extendedComponentId)));
        liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        liftMotor.setPower(1);
        if(firstTime){
            firstTime = false;
            return;
        }
        if (automaticLift != null && !firstTime && extendedComponentId == 0) {
            automaticLift.set(true);
        }
    }

    public int getPosition() {
        return extendedComponentId;
    }

    public void process() {
        process(CALL_INTERVAL, null);
    }

    public void process(int CI){
        process(CI, null);
    }

    public void process(BooleanState automaticLift) {
        process(CALL_INTERVAL, automaticLift);
    }

    public void disableEncoder() {
        encodersEnabled = false;
        liftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }
    public void enableEncoder() {
        if (!encodersEnabled) liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        encodersEnabled = true;
    }
    public void toggleEncoder() {
        if(encodersEnabled) disableEncoder();
        else enableEncoder();
    }
    public boolean encoderIsEnabled(){
        return encodersEnabled;
    }

    public void setManualPower(double power){
        if(encodersEnabled) return;
        liftMotor.setPower(power * 0.2);
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