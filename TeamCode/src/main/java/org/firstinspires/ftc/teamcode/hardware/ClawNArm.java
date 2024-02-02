package org.firstinspires.ftc.teamcode.hardware;

import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class ClawNArm {
    public Servo rotatorL, rotatorR, clawL, clawR, clawRotator;
    public Motor rotator;
    boolean clawLeft = false, clawRight = false;
    public ClawNArm(HardwareMap hardwareMap) {
        rotatorL = hardwareMap.get(Servo.class, "LeftLift");
        rotatorR = hardwareMap.get(Servo.class, "RightLift");

        clawL = hardwareMap.get(Servo.class, "LeftClaw");
        clawR = hardwareMap.get(Servo.class, "RightClaw");

        clawRotator = hardwareMap.get(Servo.class, "Arm");
        rotator = new Motor(hardwareMap, "Rotator");


        // set the run mode
        rotator.setRunMode(Motor.RunMode.PositionControl);
        rotator.setDistancePerPulse(1);
        rotator.setPositionTolerance(2);


        rotator.setVeloCoefficients(0.5, 0.004, 0);
        rotator.resetEncoder();

        //rotator.setFeedforwardCoefficients(0.92, 0.47, 0.3);

        clawL.setPosition(0.6);
        clawR.setPosition(0.4);
    }

    public void ArmFront() {
        rotatorL.setPosition(1);
        rotatorR.setPosition(0);

        clawRotator.setPosition(0.6);
        // -104
        rotator.setTargetDistance(0);
        rotator.set(1);
    }

    public void ArmCarryPos() {
        rotatorL.setPosition(0.88);
        rotatorR.setPosition(0.12);

        clawRotator.setPosition(0.6);
        // -100
        rotator.setTargetDistance(10);
        rotator.set(1);
    }

    public void ArmBack() {
        rotatorL.setPosition(0.1);
        rotatorR.setPosition(0.9);

        clawRotator.setPosition(0.92);

        //26
        rotator.setTargetDistance(26);
        rotator.set(1);
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
