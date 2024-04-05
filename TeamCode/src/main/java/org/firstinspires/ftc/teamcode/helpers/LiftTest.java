package org.firstinspires.ftc.teamcode.helpers;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp
public class LiftTest extends LinearOpMode {
    @Override
    public void runOpMode() {
        waitForStart();

        DcMotor lift = hardwareMap.get(DcMotor.class, "liftMotor");
        lift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        int pos = 0;

        while (!isStopRequested()) {
            if (gamepad1.dpad_up) {
                pos += 10;
            } else if (gamepad1.dpad_down) {
                pos -= 10;
            }
            telemetry.addData("Target position", pos);
            telemetry.addData("Lift value", lift.getCurrentPosition());
            telemetry.update();

            lift.setTargetPosition(pos);
            lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            lift.setPower(1);
        }
    }
}
