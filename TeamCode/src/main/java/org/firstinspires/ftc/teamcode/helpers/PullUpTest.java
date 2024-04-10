package org.firstinspires.ftc.teamcode.helpers;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp
public class PullUpTest extends LinearOpMode {
    @Override
    public void runOpMode() {
        waitForStart();

        DcMotor left = hardwareMap.get(DcMotor.class, "leftPullup");
        DcMotor right = hardwareMap.get(DcMotor.class, "rightPullup");

        left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        left.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        right.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        int pos = 0;

        while (!isStopRequested()) {
            if (gamepad1.dpad_up) {
                pos += 10;
            } else if (gamepad1.dpad_down) {
                pos -= 10;
            }
            telemetry.addData("Target position", pos);
            telemetry.addData("Value", "%d %d", left.getCurrentPosition(), right.getCurrentPosition());
            telemetry.update();

            left.setTargetPosition(pos);
            left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            left.setPower(1);

            right.setTargetPosition(-pos);
            right.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            right.setPower(1);
        }
    }
}
