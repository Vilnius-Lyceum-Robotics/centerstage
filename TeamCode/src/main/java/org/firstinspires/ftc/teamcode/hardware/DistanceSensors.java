package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.HardwareMap;

public class DistanceSensors {
    private final MyDistanceSensor distanceSensorLeft;
    private final MyDistanceSensor distanceSensorRight;
    private static final double DISTANCE_BETWEEN_SENSORS = 9.17; // inches
    private static final double EXCEEDING_DISTANCE = 17; // inches


    public DistanceSensors(HardwareMap hardwareMap) {
        distanceSensorLeft = new MyDistanceSensor(hardwareMap, "distanceSensorLeft");
        distanceSensorRight = new MyDistanceSensor(hardwareMap, "distanceSensorRight");
    }

    public boolean process() {
        distanceSensorLeft.fetchDistanceAsync();
        distanceSensorRight.fetchDistanceAsync();
        return true;
    }

    public double getMinDistance() {
        return Math.min(distanceSensorLeft.getDistance(), distanceSensorRight.getDistance());
    }

    public double getMaxDistance() {
        return Math.max(distanceSensorLeft.getDistance(), distanceSensorRight.getDistance());
    }

    public boolean makesSense() {
        return distanceSensorLeft.getDistance() <= EXCEEDING_DISTANCE && distanceSensorRight.getDistance() <= EXCEEDING_DISTANCE;
    }

    public double getAngle() {
        return Math.atan((distanceSensorLeft.getDistance() - distanceSensorRight.getDistance()) / DISTANCE_BETWEEN_SENSORS);
    }

}
