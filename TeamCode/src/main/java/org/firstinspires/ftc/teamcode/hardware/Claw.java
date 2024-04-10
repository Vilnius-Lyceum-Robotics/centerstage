package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Claw {
    Servo left, right, rotator;

    public enum ClawState {
        OPEN,
        CLOSED,
        UP,
        DOWN
    }

    ClawState stateLeft = ClawState.CLOSED;
    ClawState stateRight = ClawState.CLOSED;
    private ClawState clawState;

    public Claw(HardwareMap hardwareMap) {
        left = hardwareMap.get(Servo.class, "leftClaw");
        right = hardwareMap.get(Servo.class, "rightClaw");
        rotator = hardwareMap.get(Servo.class, "rotator");
        setLeftPos(ClawState.CLOSED);
        setRightPos(ClawState.CLOSED);
    }

    public void setClawState(ClawState state) {
        clawState = state;
    }

    public ClawState getClawState() {
        return clawState;
    }

    public void setUpClaw() {
        setClawState(ClawState.UP);
    }

    public void setDownClaw() {
        setClawState(ClawState.DOWN);
    }

    public void toggleLeft() {
        if(clawState == ClawState.DOWN) {
            if (stateLeft == ClawState.CLOSED) setLeftPos(ClawState.OPEN);
            else setLeftPos(ClawState.CLOSED);
        }
        else{
            if (stateRight == ClawState.CLOSED) setRightPos(ClawState.OPEN);
            else setRightPos(ClawState.CLOSED);
        }
    }

    public void toggleRight() {
        if (clawState == ClawState.DOWN) {
            if (stateRight == ClawState.CLOSED) setRightPos(ClawState.OPEN);
            else setRightPos(ClawState.CLOSED);
        }
        else{
            if (stateLeft == ClawState.CLOSED) setLeftPos(ClawState.OPEN);
            else setLeftPos(ClawState.CLOSED);
        }
    }

    public void setRightPos(ClawState state) {
        if (state == ClawState.CLOSED) right.setPosition(0.25);
        else right.setPosition(0);
        stateRight = state;
    }

    public void setLeftPos(ClawState state) {
        if (state == ClawState.CLOSED) left.setPosition(0.75);
        else left.setPosition(1);
        stateLeft = state;
    }

    public void rotatorDown() {
        rotator.setPosition(0.05);
    }

    public void rotatorUp() {
        rotator.setPosition(0.8);
    }
}
