package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Arm {
    Servo tiltServo;
    Servo armServo;

    public Arm(HardwareMap hardwareMap) {
        tiltServo = hardwareMap.get(Servo.class, "ArmTiltServo");
        armServo = hardwareMap.get(Servo.class, "ArmServo");
    }

    public void grab() {
        armServo.setPosition(0);
    }

    public void ungrab() {
        armServo.setPosition(.25);
    }

    public void tiltBack() {
        tiltServo.setPosition(0);
    }

    public void tiltForward() {
        tiltServo.setPosition(1);
    }

    // todo
}
