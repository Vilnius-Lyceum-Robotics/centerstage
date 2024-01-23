package org.firstinspires.ftc.teamcode.hardware;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;

import java.util.Map;

public class Controls {
    public Gamepad gamepad;
    public IMU imu;

    Map<String, Boolean> debounceMap;

    public Controls(Gamepad gp, IMU i) {
        gamepad = gp;
        imu = i;
        imu.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.RIGHT, RevHubOrientationOnRobot.UsbFacingDirection.FORWARD)));
        imu.resetYaw();

        debounceMap = new java.util.HashMap<>();
        debounceMap.put("A", false);
        debounceMap.put("B", false);
        debounceMap.put("X", false);
        debounceMap.put("Y", false);
        debounceMap.put("DpadUp", false);
        debounceMap.put("DpadDown", false);
        debounceMap.put("DpadLeft", false);
        debounceMap.put("DpadRight", false);
        debounceMap.put("LeftBumper", false);
        debounceMap.put("RightBumper", false);
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

        return new Pose2d(gamepad.left_stick_y * Math.sin(z) + gamepad.left_stick_x * Math.cos(z), -gamepad.left_stick_y * Math.cos(z) + gamepad.left_stick_x * Math.sin(z), gamepad.right_stick_x);
    }

    public boolean getA() {
        return gamepad.a;
    }

    public boolean getA(boolean debounce) {
        if (debounce) {
            if (gamepad.a && !debounceMap.get("A")) {
                debounceMap.put("A", true);
                return true;
            } else if (!gamepad.a) debounceMap.put("A", false);
        } else return gamepad.a;

        return false;
    }

    public boolean getB() {
        return gamepad.b;
    }

    public boolean getB(boolean debounce) {
        if (debounce) {
            if (gamepad.b && !debounceMap.get("B")) {
                debounceMap.put("B", true);
                return true;
            } else if (!gamepad.b) debounceMap.put("B", false);
        } else return gamepad.b;

        return false;
    }

    public boolean getX() {
        return gamepad.x;
    }

    public boolean getX(boolean debounce) {
        if (debounce) {
            if (gamepad.x && !debounceMap.get("X")) {
                debounceMap.put("X", true);
                return true;
            } else if (!gamepad.x) debounceMap.put("X", false);
        } else return gamepad.x;

        return false;
    }

    public boolean getY() {
        return gamepad.y;
    }

    public boolean getY(boolean debounce) {
        if (debounce) {
            if (gamepad.y && !debounceMap.get("Y")) {
                debounceMap.put("Y", true);
                return true;
            } else if (!gamepad.y) debounceMap.put("Y", false);
        } else return gamepad.y;

        return false;
    }

    public boolean getDpadUp() {
        return gamepad.dpad_up;
    }

    public boolean getDpadUp(boolean debounce) {
        if (debounce) {
            if (gamepad.dpad_up && !debounceMap.get("DpadUp")) {
                debounceMap.put("DpadUp", true);
                return true;
            } else if (!gamepad.dpad_up) debounceMap.put("DpadUp", false);
        } else return gamepad.dpad_up;

        return false;
    }

    public boolean getDpadDown() {
        return gamepad.dpad_down;
    }

    public boolean getDpadDown(boolean debounce) {
        if (debounce) {
            if (gamepad.dpad_down && !debounceMap.get("DpadDown")) {
                debounceMap.put("DpadDown", true);
                return true;
            } else if (!gamepad.dpad_down) debounceMap.put("DpadDown", false);
        } else return gamepad.dpad_down;

        return false;
    }

    public boolean getDpadLeft() {
        return gamepad.dpad_left;
    }

    public boolean getDpadLeft(boolean debounce) {
        if (debounce) {
            if (gamepad.dpad_left && !debounceMap.get("DpadLeft")) {
                debounceMap.put("DpadLeft", true);
                return true;
            } else if (!gamepad.dpad_left) debounceMap.put("DpadLeft", false);
        } else return gamepad.dpad_left;

        return false;
    }

    public boolean getDpadRight() {
        return gamepad.dpad_right;
    }

    public boolean getDpadRight(boolean debounce) {
        if (debounce) {
            if (gamepad.dpad_right && !debounceMap.get("DpadRight")) {
                debounceMap.put("DpadRight", true);
                return true;
            } else if (!gamepad.dpad_right) debounceMap.put("DpadRight", false);
        } else return gamepad.dpad_right;

        return false;
    }

    public boolean getLeftBumper() {
        return gamepad.left_bumper;
    }

    public boolean getLeftBumper(boolean debounce) {
        if (debounce) {
            if (gamepad.left_bumper && !debounceMap.get("LeftBumper")) {
                debounceMap.put("LeftBumper", true);
                return true;
            } else if (!gamepad.left_bumper) debounceMap.put("LeftBumper", false);
        } else return gamepad.left_bumper;

        return false;
    }

    public boolean getRightBumper() {
        return gamepad.right_bumper;
    }

    public boolean getRightBumper(boolean debounce) {
        if (debounce) {
            if (gamepad.right_bumper && !debounceMap.get("RightBumper")) {
                debounceMap.put("RightBumper", true);
                return true;
            } else if (!gamepad.right_bumper) debounceMap.put("RightBumper", false);
        } else return gamepad.right_bumper;

        return false;
    }

    public double getLeftTrigger() {
        return gamepad.left_trigger;
    }

    public double getRightTrigger() {
        return gamepad.right_trigger;
    }
}
