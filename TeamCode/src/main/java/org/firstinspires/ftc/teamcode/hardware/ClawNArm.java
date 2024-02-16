package org.firstinspires.ftc.teamcode.hardware;

import com.arcrobotics.ftclib.controller.PIDController;
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
        rotator = new PositionPIDMotor(hardwareMap, "Rotator");


        // set the run mode
        rotator.setRunMode(Motor.RunMode.PositionControl);
        rotator.setDistancePerPulse(0.09);
        rotator.setPositionTolerance(1);



        rotator.setPositionCoefficient(0.2);
        rotator.resetEncoder();

        //rotator.setFeedforwardCoefficients(0.92, 0.47, 0.3);

        clawL.setPosition(0.6);
        clawR.setPosition(0.4);
    }

    /*public void PIDcontrollerThing(double setpointer) {
        double kP = 0;
        double kI = 0;
        double kD = 0;
        PIDController pid = new PIDController(kP, kI, kD);
        pid.setSetPoint(setpointer);
        rotator.setTargetPosition((int)setpointer);
        while (!pid.atSetPoint()) {
            double output = pid.calculate(rotator.getCurrentPosition());
            rotator.set(output);
        }

    }*/
    public void ArmFront() {
        //rotatorL.setPosition(1);
        //rotatorR.setPosition(0);

        clawRotator.setPosition(0.6);
        // -104
        rotator.set(0.2);
        rotator.setTargetPosition(5);
        //PIDcontrollerThing(5);
    }


    public void ArmCarryPos() {
        //rotatorL.setPosition(0.88);
        //rotatorR.setPosition(0.12);

        clawRotator.setPosition(0.6);
        // -100
        rotator.set(0.8);
        rotator.setTargetPosition(10);

        //PIDcontrollerThing(10);
    }



    public void ArmBack() {
        //rotatorL.setPosition(0.1);
        //rotatorR.setPosition(0.9);

        clawRotator.setPosition(0.92);

        //26
        rotator.set(1);
        rotator.setTargetPosition(120);
        //PIDcontrollerThing(110);



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
