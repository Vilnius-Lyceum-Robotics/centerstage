package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class AsyncDistanceSensor {
    private final DistanceSensor distanceSensor;
    private final AtomicReference<Double> distanceValue = new AtomicReference<>(0.0);
    private final AtomicBoolean inFlight = new AtomicBoolean(false);

    private ExecutorService es;

    public AsyncDistanceSensor(ExecutorService executorService, HardwareMap hardwareMap, String sensorName){
        distanceSensor = hardwareMap.get(DistanceSensor.class, sensorName);
        es = executorService;
    }

    private CompletableFuture<Double> getRawDistanceAsync(){
        CompletableFuture<Double> completableFuture = new CompletableFuture<>();

        es.submit(() -> {
            Thread.sleep(5);
            double distance = distanceSensor.getDistance(DistanceUnit.INCH);
            completableFuture.complete(distance);
            return distance;
        });

        return completableFuture;
    }

    public void process() {
        if (!inFlight.get()) {
            inFlight.set(true);
            getRawDistanceAsync().thenAccept(distance ->{
                distanceValue.set(distance);
                inFlight.set(false);
            });
        }
    }

    public double getDistance() {
        return distanceValue.get();
    }
}
