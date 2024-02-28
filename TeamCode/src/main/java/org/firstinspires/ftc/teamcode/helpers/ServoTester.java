package org.firstinspires.ftc.teamcode.helpers;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class ServoTester extends LinearOpMode {
    @Override
    public void runOpMode() {
        waitForStart();

        String[] servos = {"LeftLift", "RightLift", "Arm", "LeftClaw", "RightClaw", "PlaneServo"};
        double[] powers = new double[servos.length];
        int i = 0;

        GamepadEx gp = new GamepadEx(gamepad1);

        while (!isStopRequested()) {
            telemetry.addData("Selected", servos[i]);
            telemetry.addData("Power", "%6.2f", powers[i]);
            telemetry.update();

            if (gp.getButton(GamepadKeys.Button.A)) {
                i = (i + 1) % servos.length;
            }

            if (gp.getButton(GamepadKeys.Button.DPAD_UP)) {
                powers[i] += 0.05;
            } else if (gp.getButton(GamepadKeys.Button.DPAD_DOWN)) {
                powers[i] -= 0.05;
            }

            powers[i] = Math.max(0, Math.min(powers[i], 1));
            Servo servo = hardwareMap.get(Servo.class, servos[i]);
            if (gp.getButton(GamepadKeys.Button.B)) servo.setPosition(powers[i]);
            sleep(100);
        }
    }
}
