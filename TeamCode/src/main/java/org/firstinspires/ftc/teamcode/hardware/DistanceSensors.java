package org.firstinspires.ftc.teamcode.hardware;

import com.outoftheboxrobotics.photoncore.PeriodicSupplier;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class DistanceSensors {
    private final DistanceSensor distanceSensorLeft;
    private final DistanceSensor distanceSensorRight;
    private static final double DISTANCE_BETWEEN_SENSORS = 9.17; // inches

    public AtomicReference<Double> leftDistance = new AtomicReference<>(0.0);
    public AtomicReference<Double> rightDistance = new AtomicReference<>(0.0);

    AtomicBoolean leftInFlight = new AtomicBoolean(false);
    AtomicBoolean rightInFlight = new AtomicBoolean(false);

    public DistanceSensors(HardwareMap hardwareMap){
        distanceSensorLeft = hardwareMap.get(DistanceSensor.class, "distanceSensorLeft");
        distanceSensorRight = hardwareMap.get(DistanceSensor.class, "distanceSensorRight");
    }

    public void process(){
        if(!leftInFlight.get()){
            leftInFlight.set(true);
            getRawLeft().thenAccept(distance -> {
                leftDistance.set(distance);
                leftInFlight.set(false);
            });
        }
        if(!rightInFlight.get()){
            rightInFlight.set(true);
            getRawRight().thenAccept(distance -> {
                rightDistance.set(distance);
                rightInFlight.set(false);
            });
        }
    }

    public CompletableFuture<Double> getRawLeft(){
        CompletableFuture<Double> completableFuture = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            double distance = distanceSensorLeft.getDistance(DistanceUnit.INCH);
            completableFuture.complete(distance);
            return distance;
        });

        return completableFuture;
    }

    public CompletableFuture<Double> getRawRight(){
        CompletableFuture<Double> completableFuture = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            double distance = distanceSensorRight.getDistance(DistanceUnit.INCH);
            completableFuture.complete(distance);
            return distance;
        });

        return completableFuture;
    }

    public double getMinDistance() {
        return Math.min(leftDistance.get(), rightDistance.get());
    }

    public double getMaxDistance(){
        return Math.max(leftDistance.get(), rightDistance.get());
    }

    public double getAngle(){
        return Math.atan((leftDistance.get() - rightDistance.get()) / DISTANCE_BETWEEN_SENSORS);
    }

}
