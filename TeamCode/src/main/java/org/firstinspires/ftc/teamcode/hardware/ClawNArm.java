package org.firstinspires.ftc.teamcode.hardware;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.controller.PIDController;
import com.arcrobotics.ftclib.controller.PIDFController;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorGroup;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

@Config
public class ClawNArm {
    public static class Params {
        public double pUp = 0.0002;
        public double pDown = 0.00012;

        public double fDown = -0.00012;
        public double i = 0.15;
        public double d = 0;

        public double tolerance = 50;
    }

    public static Params PARAMS = new Params();
    public Servo clawL, clawR, clawRotator;
    public Motor rotatorL, rotatorR;

    public MotorGroup rotator;
    public PIDFController rotatorController;

    public Motor.Encoder encoder;
    public boolean clawLeft = false, clawRight = false;

    public boolean wasDown = true;

    public ClawNArm(HardwareMap hardwareMap) {
        rotatorL = new Motor(hardwareMap, "LeftRotator");
        rotatorR = new Motor(hardwareMap, "RightRotator");


        rotatorL.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        rotatorR.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);

        encoder = rotatorR.encoder;
        rotatorR.stopAndResetEncoder();

        rotator = new MotorGroup(rotatorL, rotatorR);

        rotatorController = new PIDFController(PARAMS.pUp, PARAMS.i, PARAMS.d, 0);
        rotatorController.setTolerance(PARAMS.tolerance);

        clawL = hardwareMap.get(Servo.class, "LeftClaw");
        clawR = hardwareMap.get(Servo.class, "RightClaw");

        clawRotator = hardwareMap.get(Servo.class, "Arm");
        //rotator = new PositionPIDMotor();


        // set the run mode

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
    public void CalculatePID() {
        if (!rotatorController.atSetPoint()) {
            double output = rotatorController.calculate(
                    encoder.getPosition()
            );
            rotator.set(output);
        } else {
            rotator.stopMotor();
        }
    }

    public void ArmFront() {
        //rotatorL.setPosition(1);
        //rotatorR.setPosition(0);

        clawRotator.setPosition(0.6);

        rotatorController.setSetPoint(0);
        rotatorController.setP(PARAMS.pDown);
        wasDown = true;
        CalculatePID();
    }


    public void ArmCarryPos() {
        //rotatorL.setPosition(0.88);
        //rotatorR.setPosition(0.12);

        clawRotator.setPosition(0.6);
        // 2800 graf more

        rotatorController.setSetPoint(600);
        if (wasDown || encoder.getPosition() < 450) {
            rotatorController.setP(PARAMS.pUp);
            rotatorController.setF(0);
        } else {
            rotatorController.setP(PARAMS.pDown);
            rotatorController.setF(-PARAMS.fDown);
            if (encoder.getPosition() < 2500) rotatorController.setF(-2.5 * PARAMS.fDown);
        }
        if (Math.abs(rotatorController.getPositionError()) < 100) {
            rotatorController.setF(0);
        }
        CalculatePID();
    }


    public void ArmBack() {
        //rotatorL.setPosition(0.1);
        //rotatorR.setPosition(0.9);

        clawRotator.setPosition(0.86);


        rotatorController.setSetPoint(3800);
        rotatorController.setP(PARAMS.pUp);
        rotatorController.setF(0);

        wasDown = false;
        CalculatePID();
    }

    public void ArmBackAuto() {
        //rotatorL.setPosition(0.1);
        //rotatorR.setPosition(0.9);

        clawRotator.setPosition(0);


        rotatorController.setSetPoint(3800);
        rotatorController.setP(PARAMS.pUp);
        rotatorController.setF(0);

        wasDown = false;
        CalculatePID();
    }

    public void ToggleClawLeft() {
        clawL.setPosition(clawLeft ? 0.6 : 1);
        clawLeft = !clawLeft;
    }

    public void ToggleClawRight() {
        clawR.setPosition(clawRight ? 0.4 : 0);
        clawRight = !clawRight;
    }
}
