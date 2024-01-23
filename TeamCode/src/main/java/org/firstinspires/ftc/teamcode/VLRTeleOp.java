package org.firstinspires.ftc.teamcode;

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
    @Override
    public void runOpMode() {
        Chassis chassis = new Chassis(hardwareMap);
        Controls controls = new Controls(gamepad1, hardwareMap.get(IMU.class, "imu"));

        Lift lift = new Lift(hardwareMap);
        Plane plane = new Plane(hardwareMap);
        ClawNArm clawNArm = new ClawNArm(hardwareMap);

        int armPosition = 0; // 0 - front, 1 - carry, 2 - back

        waitForStart();

        while (opModeIsActive()) {
            chassis.setPower(1 - controls.getLeftTrigger() * 0.5);
            chassis.drive(controls.getControls());

            if (controls.getDpadUp()) lift.up();
            else if (controls.getDpadDown()) lift.down();
            else lift.stop();

            if (controls.getDpadLeft(true)) armPosition -= 1;
            else if (controls.getDpadRight(true)) armPosition += 1;
            armPosition = Math.max(0, Math.min(2, armPosition));

            if (armPosition == 2) clawNArm.ArmBack();
            else if (armPosition == 1) clawNArm.ArmCarryPos();
            else clawNArm.ArmFront();

            if (controls.getLeftBumper(true)) clawNArm.ToggleClawLeft();
            if (controls.getRightBumper(true)) clawNArm.ToggleClawRight();

            if (controls.getRightTrigger() > 0.8) plane.launch();

            sleep(20);
        }
    }
}