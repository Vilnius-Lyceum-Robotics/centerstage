package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.concurrent.ExecutorService;

public class DistanceSensors {
    private final AsyncDistanceSensor distanceSensorLeft;
    private final AsyncDistanceSensor distanceSensorRight;
    private static final double DISTANCE_BETWEEN_SENSORS = 9.17; // inches
    private static final double EXCEEDING_DISTANCE = 17; // inches

    public DistanceSensors(ExecutorService es, HardwareMap hardwareMap) {
        distanceSensorLeft = new AsyncDistanceSensor(es, hardwareMap, "distanceSensorLeft");
        distanceSensorRight = new AsyncDistanceSensor(es, hardwareMap, "distanceSensorRight");
    }

    public boolean process() {
        distanceSensorLeft.process();
        distanceSensorRight.process();
        return true;
    }

    public double getLeftDistance() {
        return distanceSensorLeft.getDistance();
    }

    public double getRightDistance() {
        return distanceSensorRight.getDistance();
    }

    public double getMinDistance() {
        return Math.min(getLeftDistance(), getRightDistance());
    }

    public double getMaxDistance() {
        return Math.max(getLeftDistance(), getRightDistance());
    }

    public boolean makesSense() {
        return getLeftDistance() <= EXCEEDING_DISTANCE && getRightDistance() <= EXCEEDING_DISTANCE;
    }

    public double getAngle() {
        return Math.atan((getLeftDistance() - getRightDistance()) / DISTANCE_BETWEEN_SENSORS);
    }

}
