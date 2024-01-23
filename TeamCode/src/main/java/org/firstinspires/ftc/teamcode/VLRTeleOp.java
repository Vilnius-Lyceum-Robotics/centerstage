package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.teamcode.hardware.Chassis;
import org.firstinspires.ftc.teamcode.hardware.ClawNArm;
import org.firstinspires.ftc.teamcode.hardware.Controls;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Plane;

@TeleOp(name = "VLRTeleOp")
public class VLRTeleOp extends LinearOpMode {
    public final Pose2d StartPos = new Pose2d(0, 0, Math.toRadians(0));

    @Override
    public void runOpMode() {
        Chassis chassis = new Chassis(hardwareMap);
        Controls controls = new Controls(gamepad1, hardwareMap.get(IMU.class, "imu"));

        Lift lift = new Lift(hardwareMap);

        Plane plane = new Plane(hardwareMap);

        ClawNArm clawNArm = new ClawNArm(hardwareMap);

        boolean lBumperDebounce = false;
        boolean rBumperDebounce = false;

        boolean frontDebounce = false;
        boolean frontPos = false;

        waitForStart();
        // Main loop
        while (opModeIsActive()) {
            // Controls
            Pose2d controllerInput = controls.getControls();


            chassis.setPower(1 - controls.getTrigger() * 0.5);
            telemetry.addData("input", "%6.1f %6.1f %6.1f %6.1f", controllerInput.position.x, controllerInput.position.y, controllerInput.heading.real, controllerInput.heading.imag);
            telemetry.update();
            double[] wheelSpeeds = chassis.drive(controllerInput);
            //mc.move(controllerInput.scale(1), 0.5);
            // telemetry.addData("Wheel speeds", "%6.1f %6.1f %6.1f %6.1f",
            //        wheelSpeeds[0], wheelSpeeds[1], wheelSpeeds[2], wheelSpeeds[3]);

            if (controls.getDpadUp()) {
                lift.up();
            } else if (controls.getDpadDown()) {
                lift.down();
            } else {
                lift.stop();
            }

            if (controls.getDpadLeft()) {
                if (!frontDebounce) {
                    if (!frontPos) {
                        clawNArm.ArmCarryPos();
                        frontPos = true;
                    } else {
                        clawNArm.ArmFront();
                        frontPos = false;
                    }
                    frontDebounce = true;
                }
            } else if (controls.getDpadRight()) {
                if (!frontDebounce) {
                    if (frontPos) {
                        clawNArm.ArmBack();
                        frontPos = false;
                    } else {
                        clawNArm.ArmCarryPos();
                        frontPos = true;
                    }
                    frontDebounce = true;
                }
            } else {
                frontDebounce = false;
            }


            if (controls.getLeftBumper()) {
                if (!lBumperDebounce) clawNArm.ToggleClawLeft();
                lBumperDebounce = true;
            } else lBumperDebounce = false;

            if (controls.getRightBumper()) {
                if (!rBumperDebounce) clawNArm.ToggleClawRight();
                rBumperDebounce = true;
            } else {
                rBumperDebounce = false;

            }

            if (controls.gamepad.right_trigger > 0.8) {
                plane.launch();
            }

            //camera.process();
            sleep(20);
        }
    }
}
// Why are you here? What reason do you have to procrastinate and be here? Be a good programmer and WORK!