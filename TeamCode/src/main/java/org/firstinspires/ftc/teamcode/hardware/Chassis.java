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
    final double LENGTH = 0.8; // Length from wheel to wheel of robot (lengthwise), meters
    final double WIDTH = 1.0; // Length from wheel to wheel of robot (widthwise), meters
    final double WHEEL_RADIUS = 3; // Radius of wheel (cm)

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

    public double[] drive(Pose2d vector) {
        double[] wheelSpeeds = new double[4]; // Front left, front right, back right, back left


        wheelSpeeds[0] = vector.heading.imag - vector.position.x + vector.position.y; // Back Left
        wheelSpeeds[1] = -vector.heading.imag - vector.position.x + vector.position.y; // Front Left
        wheelSpeeds[2] = -vector.heading.imag + vector.position.x + vector.position.y; // Back Right
        wheelSpeeds[3] = vector.heading.imag + vector.position.x + vector.position.y; // Front Right


        // Normalizing speeds if any of them exceed the maximum speed of 1
        double max = Math.abs(wheelSpeeds[0]);

        for (double wheelSpeed : wheelSpeeds) {
            max = Math.max(max, Math.abs(wheelSpeed));
        }
        System.out.println(wheelSpeeds[0] + " " + wheelSpeeds[1] + " " + wheelSpeeds[2] + " " + wheelSpeeds[3]);
        if (max > 1) {
            for (int i = 0; i < wheelSpeeds.length; i++) {
                wheelSpeeds[i] = wheelSpeeds[i] / max;
            }
        }
        System.out.println(wheelSpeeds[0] + " " + wheelSpeeds[1] + " " + wheelSpeeds[2] + " " + wheelSpeeds[3]);
        MotorLeftBack.setPower(wheelSpeeds[0] * power);
        MotorLeftFront.setPower(-wheelSpeeds[1] * power);
        MotorRightBack.setPower(wheelSpeeds[2] * power);
        MotorRightFront.setPower(-wheelSpeeds[3] * power);

        return wheelSpeeds;
    }

    public void setPower(double pwr) {
        power = pwr;
    }


}
