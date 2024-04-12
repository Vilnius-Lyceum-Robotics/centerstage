package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class PullUp {
    DcMotor left;
    DcMotor right;

    enum State {
        DOWN(0),
        UP(3050),
        PULLUP(600);

        public final int value;

        private State(int val) {
            value = val;
        }
    }

    State state = State.DOWN;
    public boolean encodersEnabled = true;

    public PullUp(HardwareMap hardwareMap) {
        left = hardwareMap.get(DcMotor.class, "leftPullup");
        right = hardwareMap.get(DcMotor.class, "rightPullup");

        left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        down();
    }

    private void moveLeftMotor(int pos) {
        left.setTargetPosition(-pos);
        left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        left.setPower(1);
    }

    private void moveRightMotor(int pos) {
        right.setTargetPosition(pos);
        right.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        right.setPower(1);
    }

    public void up() {
        if (!encodersEnabled) return;
        moveLeftMotor(State.UP.value);
        moveRightMotor(State.UP.value);
        state = State.UP;
    }

    public void down() {
        if (!encodersEnabled) return;
        moveLeftMotor(State.DOWN.value);
        moveRightMotor(State.DOWN.value);
        state = State.DOWN;
    }

    public void pullUp() {
        if (!encodersEnabled) return;
        moveLeftMotor(State.PULLUP.value);
        moveRightMotor(State.PULLUP.value);
        state = State.PULLUP;
    }

    public void toggle() {
        if (!encodersEnabled) return;
        if (state == State.DOWN || state == State.PULLUP) up();
        else pullUp();

    }

    private void stopLeftMotor() {
        left.setPower(0);
        left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    private void stopRightMotor() {
        right.setPower(0);
        right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void disableEncoders() {
        encodersEnabled = false;
        left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void enableEncoders() {
        if (!encodersEnabled) {
            left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        }
        encodersEnabled = true;
    }

    public void toggleEncoders() {
        if (encodersEnabled) disableEncoders();
        else enableEncoders();
    }

    public void setManualPower(double power) {
        if (encodersEnabled) return;
        left.setPower(-power);
        right.setPower(power);
    }

    public void stop() {
        stopLeftMotor();
        stopRightMotor();
    }
}
