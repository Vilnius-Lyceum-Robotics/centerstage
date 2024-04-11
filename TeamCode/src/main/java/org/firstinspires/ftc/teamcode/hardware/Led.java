package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Led {
    DigitalChannel red;
    DigitalChannel green;


    public enum Color {
        NONE,
        RED,
        GREEN,
        AMBER
    }

    private final AtomicReference<Color> prevColor = new AtomicReference<>(Color.NONE);

    private final AtomicReference<Color> currentColor = new AtomicReference<>(Color.NONE);

    private final AtomicReference<Integer> blinkMs = new AtomicReference<>(-1);

    private final AtomicBoolean inFlight = new AtomicBoolean(false);

    private ExecutorService es;

    public Led(HardwareMap hardwareMap, String name, ExecutorService executorService) {
        red = hardwareMap.get(DigitalChannel.class, "red" + name);
        green = hardwareMap.get(DigitalChannel.class, "green" + name);

        red.setMode(DigitalChannel.Mode.OUTPUT);
        green.setMode(DigitalChannel.Mode.OUTPUT);

        es = executorService;
    }

    public void setColor(Color color) {
        if (currentColor.get() == color) return;
        prevColor.set(currentColor.get());
        currentColor.set(color);
        blinkMs.set(-1);
    }

    public void blink(int ms) {
        blinkMs.set(ms);
    }

    private CompletableFuture<Integer> processRaw() {

        CompletableFuture<Integer> completableFuture = new CompletableFuture<>();

        es.submit(() -> {
            if (prevColor.get() != currentColor.get() && blinkMs.get() < 0) {
                switch (currentColor.get()) {
                    case AMBER:
                        red.setState(true);
                        green.setState(true);
                        break;
                    case RED:
                        red.setState(false);
                        green.setState(true);
                        break;
                    case GREEN:
                        red.setState(true);
                        green.setState(false);
                        break;
                    case NONE:
                        red.setState(false);
                        green.setState(false);
                        break;
                }
                prevColor.set(currentColor.get());
            } else if (blinkMs.get() >= 0) {
                if (System.currentTimeMillis() % (2L * blinkMs.get()) < blinkMs.get()) {
                    red.setState(true);
                    green.setState(true);
                } else {
                    switch (currentColor.get()) {
                        case AMBER:
                            red.setState(false);
                            green.setState(false);
                            break;
                        case RED:
                            red.setState(false);
                            green.setState(true);
                            break;
                        case GREEN:
                            red.setState(true);
                            green.setState(false);
                            break;
                        case NONE:
                            red.setState(true);
                            green.setState(true);
                            break;
                    }
                }
            }
            completableFuture.complete(0);
        });

        return  completableFuture;
    }

    public void process() {
        if (!inFlight.get()) {
            inFlight.set(true);
            processRaw().thenAccept(nulis -> {
                inFlight.set(false);
            });
        }
    }

}
