package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.concurrent.ExecutorService;

public class ClawSensors {
    AsyncDistanceSensor clawSensorRight;
    AsyncDistanceSensor clawSensorLeft;
    private static final double RIGHT_TRIGGER_DISTANCE = 2.0; // inches
    private static final double LEFT_TRIGGER_DISTANCE = 1.6; // inches

    public ClawSensors(ExecutorService es, HardwareMap hardwareMap) {
        clawSensorRight = new AsyncDistanceSensor(es, hardwareMap, "clawSensorRight");
        clawSensorLeft = new AsyncDistanceSensor(es, hardwareMap, "clawSensorLeft");
    }

    public boolean process() {
        clawSensorRight.process();
        clawSensorLeft.process();
        return true;
    }

    public double getDistanceRight() {
        return clawSensorRight.getDistance();
    }

    public double getDistanceLeft() {
        return clawSensorLeft.getDistance();
    }

    public boolean isCloseLeft() {
        return clawSensorLeft.getDistance() <= LEFT_TRIGGER_DISTANCE;
    }

    public boolean isCloseRight() {
        return clawSensorRight.getDistance() <= RIGHT_TRIGGER_DISTANCE;
    }
}
