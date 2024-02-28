package org.firstinspires.ftc.teamcode.helpers;

import static java.lang.Thread.sleep;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class PreGameConfigurator {
    Telemetry telemetry;
    GamepadEx gamepadEx;

    public PreGameConfigurator(Telemetry telemetry) {
        this.telemetry = telemetry;
    }

    public boolean leftRightSelect(String trueOption, String falseOption) throws InterruptedException {
        telemetry.addData("CFG", trueOption + " (left) or " + falseOption + " (right)?");
        telemetry.update();
        while (true) {
            if (gamepadEx.getButton(GamepadKeys.Button.DPAD_LEFT)) {
                telemetry.addData("CFG", "Picked " + trueOption + " - true");
                telemetry.update();
                sleep(1000);
                return true;
            } else if (gamepadEx.getButton(GamepadKeys.Button.DPAD_RIGHT)) {
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
            if (gamepadEx.getButton(GamepadKeys.Button.DPAD_UP)) {
                telemetry.addData("CFG", "Picked " + trueOption + " - true");
                telemetry.update();
                sleep(1000);
                return true;
            } else if (gamepadEx.getButton(GamepadKeys.Button.DPAD_DOWN)) {
                telemetry.addData("CFG", "Picked " + falseOption + " - false");
                telemetry.update();
                sleep(1000);
                return false;
            }
            sleep(20);
        }
    }
}
