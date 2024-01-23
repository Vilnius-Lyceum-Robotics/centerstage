package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Plane {
    Servo planeServo;
    public Plane(HardwareMap hardwareMap) {
        planeServo = hardwareMap.get(Servo.class, "PlaneServo");
        planeServo.setPosition(0.6);
    }

    public void launch() {
        planeServo.setPosition(0.5);
    }
}
