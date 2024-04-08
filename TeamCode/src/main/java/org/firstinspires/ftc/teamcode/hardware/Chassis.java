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
    DistanceSensors distanceSensors;
    private static final int calibrationDistance = 12; // inches
    private static final double stoppingDistance = 2; // inches

    private enum Mode {
        NORMAL,
        BACKBOARD
    }

    public Mode currentMode;

    private double power = 0.2;
    private double xPower = 1;

    public Chassis(HardwareMap hardwareMap, DistanceSensors distanceSensors) {

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

        this.distanceSensors = distanceSensors;

        currentMode = Mode.NORMAL;

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

        double leftPower;
        double rightPower;
        if(currentMode == Mode.NORMAL){
            leftPower = power;
            rightPower = power;
        }
        else if(currentMode == Mode.BACKBOARD){

            double leftDistance = distanceSensors.leftDistance.get();
            double rightDistance = distanceSensors.rightDistance.get();

            double greaterDistance = Math.max(leftDistance, rightDistance);
            double lesserDistance = Math.min(leftDistance, rightDistance);
            double decelCoefficient;

            if(lesserDistance > calibrationDistance){
                decelCoefficient = 1;
            } else {
                decelCoefficient = 1 - Math.pow(3, stoppingDistance-lesserDistance);
            }

            leftPower = leftDistance / greaterDistance * decelCoefficient;
            rightPower = rightDistance / greaterDistance * decelCoefficient;
        } else{
            leftPower = 0;
            rightPower = 0;
        }

        MotorLeftBack.setPower(-wheelSpeeds[0] * rightPower);
        MotorLeftFront.setPower(wheelSpeeds[1] * leftPower);
        MotorRightBack.setPower(-wheelSpeeds[2] * leftPower);
        MotorRightFront.setPower(wheelSpeeds[3] * rightPower
    }

    public void setMode(Mode mode){
        currentMode = mode;
    }

    public void setNormalMode(){
        currentMode = Mode.NORMAL;
    }

    public void setBackboardMode(){
        currentMode = Mode.BACKBOARD;
    }

    public void toggleMode(){
        if(currentMode == Mode.NORMAL){
            currentMode = Mode.BACKBOARD;
        } else{
            currentMode = Mode.NORMAL;
        }
    }
    public void setPower(double pwr) {
        power = pwr;
    }

    public void setXPower(double xPwr) {
        xPower = xPwr;
    }
}
