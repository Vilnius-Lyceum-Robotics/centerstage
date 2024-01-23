package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Lift {
    DcMotor liftMotor;

    public Lift(HardwareMap hardwareMap) {
        liftMotor = hardwareMap.get(DcMotor.class, "Lift");
    }

    public void up() {
        liftMotor.setPower(-0.9);
    }

    public void down() {
        liftMotor.setPower(0.9);
    }

    public void stop() {
        liftMotor.setPower(0);
    }
}
