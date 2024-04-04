package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class PullUp {
    DcMotor left;
    DcMotor right;
    public PullUp(HardwareMap hardwareMap) {
        left = hardwareMap.get(DcMotor.class, "leftPullup");
        right = hardwareMap.get(DcMotor.class, "rightPullup");

        left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void up() {
        left.setPower(1);
        right.setPower(-1);
    }

    public void down() {
        left.setPower(-1);
        right.setPower(1);
    }

    public void stop() {
        left.setPower(0);
        right.setPower(0);
    }
}
