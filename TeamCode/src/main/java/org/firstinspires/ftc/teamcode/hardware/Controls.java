package org.firstinspires.ftc.teamcode.hardware;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.ImuOrientationOnRobot;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;

public class Controls {
    public Gamepad gamepad;
    public IMU imu;

    public Controls(Gamepad gp, IMU i) {
        gamepad = gp;
        imu = i;
        imu.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.RIGHT, RevHubOrientationOnRobot.UsbFacingDirection.FORWARD)));
        imu.resetYaw();
    }

    public Pose2d getControls() {
        return new Pose2d(gamepad.left_stick_x, -gamepad.left_stick_y, gamepad.right_stick_x);
    }

    public Pose2d getCorrectedControls() {
        double z = -imu.getRobotOrientation(
                AxesReference.INTRINSIC,
                AxesOrder.XYZ,
                AngleUnit.RADIANS
        ).thirdAngle;

        System.out.println(z);
        return new Pose2d(gamepad.left_stick_y * Math.sin(z) + gamepad.left_stick_x * Math.cos(z), -gamepad.left_stick_y * Math.cos(z) + gamepad.left_stick_x * Math.sin(z), gamepad.right_stick_x);
    }

    public boolean getA() {
        return gamepad.a;
    }

    public boolean getB() {
        return gamepad.b;
    }

    public boolean getX() {
        return gamepad.x;
    }

    public boolean getY() {
        return gamepad.y;
    }

    public boolean getDpadUp() {
        return gamepad.dpad_up;
    }

    public boolean getDpadDown() {
        return gamepad.dpad_down;
    }

    public boolean getDpadLeft() {
        return gamepad.dpad_left;
    }

    public boolean getDpadRight() {
        return gamepad.dpad_right;
    }

    public boolean getLeftBumper() {
        return gamepad.left_bumper;
    }

    public boolean getRightBumper() {
        return gamepad.right_bumper;
    }

    public double getTrigger() {
        return gamepad.left_trigger;
    }
}
