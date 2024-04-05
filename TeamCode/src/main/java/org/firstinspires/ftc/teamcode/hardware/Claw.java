package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Claw {
    Servo left, right, rotator;

    enum ClawState {
        OPEN,
        CLOSED
    }

    ClawState stateLeft = ClawState.CLOSED;
    ClawState stateRight = ClawState.CLOSED;

    public Claw(HardwareMap hardwareMap) {
        left = hardwareMap.get(Servo.class, "leftClaw");
        right = hardwareMap.get(Servo.class, "rightClaw");
        rotator = hardwareMap.get(Servo.class, "rotator");


    }

    public void toggleLeft() {
        if (stateLeft == ClawState.CLOSED) {
            left.setPosition(0.5);
            stateLeft = ClawState.OPEN;
        } else {
            left.setPosition(0);
            stateLeft = ClawState.CLOSED;
        }
    }

    public void toggleRight() {
        if (stateRight == ClawState.CLOSED) {
            right.setPosition(0.5);
            stateRight = ClawState.OPEN;
        } else {
            right.setPosition(0);
            stateRight = ClawState.CLOSED;
        }
    }

    public void rotatorDown() {
        rotator.setPosition(0.5);
    }

    public void rotatorUp() {
        rotator.setPosition(0);
    }
}
