package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

// what am I doing here?
public class Intake {
    private final DcMotor intakeMotor;
    private final Servo intakeServoLeft;
    private final Servo intakeServoRight;

    public Intake(HardwareMap hardwareMap) {
        intakeMotor = hardwareMap.get(DcMotor.class, "EncoderX");
        //intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeServoLeft = hardwareMap.get(Servo.class, "IntakeServoLeft");
        intakeServoRight = hardwareMap.get(Servo.class, "IntakeServoRight");
    }

    public void startIntakeMotor() {
        startIntakeMotor(false);
    }

    public void startIntakeMotor(boolean reverse) {
        intakeMotor.setPower(reverse ? -1 : 1);
    }

    public void stopIntakeMotor() {
        intakeMotor.setPower(0);
    }

    public void lowerIntakeServo() {
        intakeServoLeft.setPosition(0.305);
        intakeServoRight.setPosition(1-.32);
    }

    public void raiseIntakeServo() {
        intakeServoLeft.setPosition(0);
        intakeServoRight.setPosition(1);
    }

}
