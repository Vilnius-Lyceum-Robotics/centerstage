package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
public class DistanceSensors {
    private DistanceSensor distanceSensorLeft;
    private DistanceSensor distanceSensorRight;
    private static final double DISTANCE_BETWEEN_SENSORS = 0.1; // unit of choice
    public DistanceSensors(HardwareMap hardwareMap){
        distanceSensorLeft = hardwareMap.get(DistanceSensor.class, "distance_sensor_left");
        distanceSensorRight = hardwareMap.get(DistanceSensor.class, "distance_sensor_right");
    }

    public double getMinDistance(DistanceUnit unit){
        return Math.min(distanceSensorLeft.getDistance(unit), distanceSensorRight.getDistance(unit));
    }

    public double getMaxDistance(DistanceUnit unit){
        return Math.max(distanceSensorLeft.getDistance(unit), distanceSensorRight.getDistance(unit));
    }

    public double getAngle(DistanceUnit unit){
        return Math.sin((getMaxDistance(unit) - getMinDistance(unit)) / DISTANCE_BETWEEN_SENSORS);
    }

}
