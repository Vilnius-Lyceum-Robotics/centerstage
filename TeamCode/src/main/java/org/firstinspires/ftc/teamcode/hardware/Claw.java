package org.firstinspires.ftc.teamcode.hardware;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.helpers.BooleanState;

public class Claw {
    Servo left, right, rotator;
    public enum ClawState {
        OPEN,
        CLOSED,
        UP,
        DOWN
    }

    public enum Hand {
        LEFT,
        RIGHT
    }

    ClawState stateLeft = ClawState.CLOSED;
    ClawState stateRight = ClawState.CLOSED;
    private ClawState clawState = ClawState.DOWN;
    private boolean closeAutomatically = true;
    private final ElapsedTime manualTime = new ElapsedTime();

    private final int manualMs = 2000;

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
    public boolean isClosed(Hand hand){
        if(hand == Hand.LEFT) return stateLeft == ClawState.CLOSED;
        else return stateRight == ClawState.CLOSED;
    }
    public boolean leftIsClosed() {
        return stateLeft == ClawState.CLOSED;
    }
    public boolean rightIsClosed() {
        return stateRight == ClawState.CLOSED;
    }
    public void setUpClaw() {
        setClawState(ClawState.UP);
    }

    public void setDownClaw() {
        setClawState(ClawState.DOWN);
    }

    public boolean isAutomatic(){
        if (!closeAutomatically && manualTime.milliseconds() > manualMs) closeAutomatically = true;
        return closeAutomatically;
    }

    public void setAutomatic() {
        closeAutomatically = true;
    }

    public void setManual() {
        closeAutomatically = false;
        manualTime.reset();
    }

    public void toggleLeft() {
        setManual();
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
        setManual();
        if (clawState == ClawState.DOWN) {
            if (stateRight == ClawState.CLOSED) setRightPos(ClawState.OPEN);
            else setRightPos(ClawState.CLOSED);
        }
        else{
            if (stateLeft == ClawState.CLOSED) setLeftPos(ClawState.OPEN);
            else setLeftPos(ClawState.CLOSED);
        }
    }

    public void setPos(Hand hand, ClawState state){
        if(hand == Hand.LEFT) setLeftPos(state);
        else setRightPos(state);
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
    // led blink stop if its not in carry position (jei ne 0, stop blink)

    public void manageClaw(Led led, Claw.Hand hand, ClawSensors clawSensors, Lift lift, BooleanState autoOpen) {
        if (isClosed(hand) && clawSensors.isClose(hand)) {
            led.setColor(Led.Color.NONE);
        } else if (!isClosed(hand) && clawSensors.isClose(hand)) {
            if (isAutomatic() && lift.getPosition() == 0) {
                setPos(hand, Claw.ClawState.CLOSED);
            }
            led.setColor(Led.Color.GREEN);
        } else if (isClosed(hand)) {

            if (lift.getPosition() == 0 && autoOpen.get()) {
                setPos(hand, Claw.ClawState.OPEN);
                autoOpen.set(false);
            } else if (lift.getPosition() != 0) {
                autoOpen.set(true);
            }

            led.setColor(Led.Color.RED);
        } else {
            led.setColor(Led.Color.AMBER);
        }
    }
}
