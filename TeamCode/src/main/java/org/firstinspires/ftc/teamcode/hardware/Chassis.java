package org.firstinspires.ftc.teamcode.hardware;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Chassis {
    DcMotor MotorLeftBack;
    DcMotor MotorLeftFront;
    DcMotor MotorRightBack;
    DcMotor MotorRightFront;

    private double power = 0.2;
    private double xPower = 1;

    public Chassis(HardwareMap hardwareMap) {

        MotorLeftBack = hardwareMap.get(DcMotor.class, "LeftBack");
        MotorLeftFront = hardwareMap.get(DcMotor.class, "RightFront");
        MotorRightBack = hardwareMap.get(DcMotor.class, "RightBack");
        MotorRightFront = hardwareMap.get(DcMotor.class, "LeftFront");

        MotorLeftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        MotorLeftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        MotorRightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        MotorRightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        MotorLeftBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        MotorLeftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        MotorRightBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        MotorRightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        MotorLeftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        MotorLeftBack.setDirection(DcMotorSimple.Direction.REVERSE);


        stop(); // Just in case
    }

    public void stop() {
        MotorLeftBack.setPower(0);
        MotorLeftFront.setPower(0);
        MotorRightBack.setPower(0);
        MotorRightFront.setPower(0);
    }

    public void drive(Pose2d vector) {
        double[] wheelSpeeds = new double[4]; // Front left, front right, back right, back left

        wheelSpeeds[0] = vector.heading.imag - vector.position.x * xPower + vector.position.y; // Back Left
        wheelSpeeds[1] = -vector.heading.imag - vector.position.x * xPower + vector.position.y; // Front Left
        wheelSpeeds[2] = -vector.heading.imag + vector.position.x * xPower + vector.position.y; // Back Right
        wheelSpeeds[3] = vector.heading.imag + vector.position.x * xPower + vector.position.y; // Front Right


        // Normalizing speeds if any of them exceed the maximum speed of 1
        double max = Math.abs(wheelSpeeds[0]);

        for (double wheelSpeed : wheelSpeeds) max = Math.max(max, Math.abs(wheelSpeed));
        if (max > 1) {
            for (int i = 0; i < wheelSpeeds.length; i++) {
                wheelSpeeds[i] = wheelSpeeds[i] / max;
            }
        }

        MotorLeftBack.setPower(-wheelSpeeds[0] * power);
        MotorLeftFront.setPower(wheelSpeeds[1] * power);
        MotorRightBack.setPower(-wheelSpeeds[2] * power);
        MotorRightFront.setPower(wheelSpeeds[3] * power);
    }

    public void setPower(double pwr) {
        power = pwr;
    }

    public void setXPower(double xPwr) {
        xPower = xPwr;
    }
}
