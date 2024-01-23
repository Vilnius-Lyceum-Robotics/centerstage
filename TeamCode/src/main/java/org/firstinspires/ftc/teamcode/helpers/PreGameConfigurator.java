package org.firstinspires.ftc.teamcode.helpers;

import static java.lang.Thread.sleep;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Controls;

public class PreGameConfigurator {
    Telemetry telemetry;
    Controls controls;

    public PreGameConfigurator(Telemetry telemetry, Controls controls) {
        this.telemetry = telemetry;
        this.controls = controls;
    }

    public boolean leftRightSelect(String trueOption, String falseOption) throws InterruptedException {
        telemetry.addData("CFG", trueOption + " (left) or " + falseOption + " (right)?");
        telemetry.update();
        while (true) {
            if (controls.getDpadLeft()) {
                telemetry.addData("CFG", "Picked " + trueOption + " - true");
                telemetry.update();
                sleep(1000);
                return true;
            } else if (controls.getDpadRight()) {
                telemetry.addData("CFG", "Picked " + falseOption + " - false");
                telemetry.update();
                sleep(1000);
                return false;
            }
            sleep(20);
        }
    }

    public boolean upDownSelect(String trueOption, String falseOption) throws InterruptedException {
        telemetry.addData("CFG", trueOption + " (up) or " + falseOption + " (down)?");
        telemetry.update();
        while (true) {
            if (controls.getDpadUp()) {
                telemetry.addData("CFG", "Picked " + trueOption + " - true");
                telemetry.update();
                sleep(1000);
                return true;
            } else if (controls.getDpadDown()) {
                telemetry.addData("CFG", "Picked " + falseOption + " - false");
                telemetry.update();
                sleep(1000);
                return false;
            }
            sleep(20);
        }
    }
}
