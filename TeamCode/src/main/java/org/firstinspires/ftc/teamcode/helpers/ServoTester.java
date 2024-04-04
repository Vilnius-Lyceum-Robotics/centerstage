package org.firstinspires.ftc.teamcode.helpers;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.hardware.Controls;

@TeleOp
public class ServoTester extends LinearOpMode {
    @Override
    public void runOpMode() {
        waitForStart();

        String[] servos = {"Arm", "LeftClaw", "RightClaw", "PlaneServo"};
        double[] powers = new double[servos.length];
        int i = 0;

        Controls gp = new Controls(gamepad1, hardwareMap.get(IMU.class, "imu"));

        while (!isStopRequested()) {
            telemetry.addData("Selected", servos[i]);
            telemetry.addData("Power", "%6.2f", powers[i]);
            telemetry.update();

            if (gp.getA()) {
                i = (i + 1) % servos.length;
            }

            if (gp.getDpadUp()) {
                powers[i] += 0.05;
            } else if (gp.getDpadDown()) {
                powers[i] -= 0.05;
            }

            powers[i] = Math.max(0, Math.min(powers[i], 1));
            Servo servo = hardwareMap.get(Servo.class, servos[i]);
            if (gp.getB()) servo.setPosition(powers[i]);
            sleep(100);
        }
    }
}
