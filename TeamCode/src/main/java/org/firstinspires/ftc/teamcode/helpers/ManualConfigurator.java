package org.firstinspires.ftc.teamcode.helpers;

import static java.lang.Thread.sleep;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ManualConfigurator {
    Telemetry telemetry;
    GamepadEx gamepadEx;

    public ManualConfigurator(Telemetry telemetry, GamepadEx gamepadEx) {
        this.telemetry = telemetry;
        this.gamepadEx = gamepadEx;
    }

    private void telemetryPrint(String message) {
        telemetry.addData("CFG", message);
        telemetry.update();
    }

    private boolean selectOption(String trueOption, String falseOption, GamepadKeys.Button trueButton, GamepadKeys.Button falseButton) throws InterruptedException {
        telemetryPrint(trueOption + " (" + trueButton + ") or " + falseOption + " (" + falseButton + ")?");
        while (true) {
            if (gamepadEx.getButton(trueButton)) {
                telemetryPrint("Picked " + trueOption + " - true");
                sleep(1000);
                return true;
            } else if (gamepadEx.getButton(falseButton)) {
                telemetryPrint("Picked " + falseOption + " - false");
                sleep(1000);
                return false;
            }
            sleep(20);
        }
    }

    public boolean leftRightSelect(String trueOption, String falseOption) throws InterruptedException {
        telemetryPrint(trueOption + " (left) or " + falseOption + " (right)?");
        return selectOption(trueOption, falseOption, GamepadKeys.Button.DPAD_LEFT, GamepadKeys.Button.DPAD_RIGHT);
    }

    public boolean upDownSelect(String trueOption, String falseOption) throws InterruptedException {
        telemetryPrint(trueOption + " (up) or " + falseOption + " (down)?");
        return selectOption(trueOption, falseOption, GamepadKeys.Button.DPAD_UP, GamepadKeys.Button.DPAD_DOWN);
    }
}
