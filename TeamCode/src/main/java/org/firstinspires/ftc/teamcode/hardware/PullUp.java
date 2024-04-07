package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class PullUp {
    DcMotor left;
    DcMotor right;
    private static final int PULLUP_HEIGHT = 1000;
    private boolean isUp = false;

    public PullUp(HardwareMap hardwareMap) {
        left = hardwareMap.get(DcMotor.class, "leftPullup");
        right = hardwareMap.get(DcMotor.class, "rightPullup");

        left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    private void moveLeftMotor(int pos){
        left.setTargetPosition(pos);
        left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        left.setPower(1);
    }

    private void moveRightMotor(int pos){
        right.setTargetPosition(pos);
        right.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        right.setPower(1);
    }

    public void up() {
        moveLeftMotor(PULLUP_HEIGHT);
        moveRightMotor(PULLUP_HEIGHT);
        isUp = true;
    }

    public void down() {
        moveLeftMotor(0);
        moveRightMotor(0);
        isUp = false;
    }

    public void toggle(){
        if(isUp){
            down();
        } else {
            up();
        }
    }

    private void stopLeftMotor(){
        left.setPower(0);
        left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }
    private void stopRightMotor(){
        right.setPower(0);
        right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void stop(){
        stopLeftMotor();
        stopRightMotor();
    }
}
