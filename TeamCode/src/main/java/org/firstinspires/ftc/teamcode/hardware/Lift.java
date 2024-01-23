package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Lift {
    int pulleyTeeth = 60;
    DcMotor motorLeft;
    DcMotor motorRight;

    public Lift(HardwareMap hardwareMap) {
        motorLeft = hardwareMap.get(DcMotor.class, "Lift");
    }

    public void up() {
        motorLeft.setPower(-0.9);
    }

    public void down() {
        motorLeft.setPower(0.9);
    }

    public void stop() {
        motorLeft.setPower(0);
    }
}
