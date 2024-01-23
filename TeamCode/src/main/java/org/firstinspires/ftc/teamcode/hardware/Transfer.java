package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Transfer {
    Servo servo1;
    Servo servo2;
    Servo servoShaker;

    public Transfer(HardwareMap hardwareMap) {
        servo1 = hardwareMap.get(Servo.class, "TransferServo1");
        servo2 = hardwareMap.get(Servo.class, "TransferServo2");
        servoShaker = hardwareMap.get(Servo.class, "TransferServoShaker");
    }

    public void up() {
        servo1.setPosition(0.25);
        servo2.setPosition(0.7);
    }

    public void down() {
        servo1.setPosition(0.67);
        servo2.setPosition(0.33);
    }

    public void jiggle() {
        if (servoShaker.getPosition() == 1)
            servoShaker.setPosition(0);
        else servoShaker.setPosition(1);
    }
}
