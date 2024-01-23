package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class ClawNArm {
    public Servo rotatorL, rotatorR, clawL, clawR, clawRotator;
    boolean clawLeft = false, clawRight = false;
    public ClawNArm(HardwareMap hardwareMap) {
        rotatorL = hardwareMap.get(Servo.class, "LeftLift");
        rotatorR = hardwareMap.get(Servo.class, "RightLift");

        clawL = hardwareMap.get(Servo.class, "LeftClaw");
        clawR = hardwareMap.get(Servo.class, "RightClaw");

        clawRotator = hardwareMap.get(Servo.class, "Arm");

        clawL.setPosition(0.6);
        clawR.setPosition(0.4);
    }

    public void ArmFront() {
        rotatorL.setPosition(1);
        rotatorR.setPosition(0);

        clawRotator.setPosition(0.6);
    }

    public void ArmCarryPos() {
        rotatorL.setPosition(0.88);
        rotatorR.setPosition(0.12);

        clawRotator.setPosition(0.6);
    }

    public void ArmBack() {
        rotatorL.setPosition(0.1);
        rotatorR.setPosition(0.9);

        clawRotator.setPosition(0.92);
    }

    public void ToggleClawLeft() {
        clawL.setPosition(clawLeft ? 0.6 : 1);
        clawLeft = !clawLeft;
    }

    public void ToggleClawRight() {
        clawR.setPosition(clawRight ? 0.4  : 0);
        clawRight = !clawRight;
    }
}
