package org.firstinspires.ftc.teamcode;

import android.text.method.Touch;

import com.acmerobotics.roadrunner.Pose2d;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.arcrobotics.ftclib.gamepad.ToggleButtonReader;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.DigitalIoDeviceConfigurationType;

import org.firstinspires.ftc.teamcode.hardware.Chassis;
import org.firstinspires.ftc.teamcode.hardware.ClawNArm;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Plane;

@TeleOp(name = "VLRTeleOp")
public class VLRTeleOp extends LinearOpMode {
    private Chassis chassis;
    private ClawNArm clawNArm;
    private Lift lift;
    private Plane plane;
    private GamepadEx gamepadEx;

    private TouchSensor limit;
    private boolean limitState = false;
    private int armPosition = 0; // 0 - front, 1 - carry, 2 - back

    @Override
    public void runOpMode() {
        chassis = new Chassis(hardwareMap);
        clawNArm = new ClawNArm(hardwareMap);
        lift = new Lift(hardwareMap);
        plane = new Plane(hardwareMap);
        gamepadEx = new GamepadEx(gamepad1);

        limit = hardwareMap.get(TouchSensor.class, "ClawSwitch");

        waitForStart();

        while (opModeIsActive()) {
            gamepadEx.readButtons();
            chassis.setPower(1 - gamepadEx.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER) * 0.75);

            chassis.drive(new Pose2d(gamepadEx.getLeftX(), gamepadEx.getLeftY(), gamepadEx.getRightX()));

            if (gamepadEx.wasJustPressed(GamepadKeys.Button.DPAD_LEFT)) {
                armPosition = Math.max(0, armPosition - 1);
            } else if (gamepadEx.wasJustPressed(GamepadKeys.Button.DPAD_RIGHT)) {
                armPosition = Math.min(2, armPosition + 1);
            }

            switch (armPosition) {
                case 0:
                    clawNArm.ArmFront();
                    break;
                case 1:
                    clawNArm.ArmCarryPos();
                    break;
                case 2:
                    clawNArm.ArmBack();
                    break;
            }

            if (gamepadEx.getButton(GamepadKeys.Button.DPAD_UP)) lift.up();
            else if (gamepadEx.getButton(GamepadKeys.Button.DPAD_DOWN)) lift.down();
            else lift.stop();

            if (gamepadEx.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER)) clawNArm.ToggleClawRight();
            if (gamepadEx.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER)) clawNArm.ToggleClawLeft();

            if (gamepadEx.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER) > 0.8) plane.launch();

            if (gamepadEx.getButton(GamepadKeys.Button.START)) clawNArm.encoder.reset();

            if (limit.isPressed() != limitState) {
                limitState = limit.isPressed();
                if (limitState) gamepadEx.gamepad.rumble(250);
            }

            telemetry.addData("Lift position", "%.1f | %s", (float) clawNArm.encoder.getPosition(), armPosition == 0 ? "down" : armPosition == 1 ? "carry" : "up");
            telemetry.addData("Claw limit", "%s", limitState ? "ACTIVE" : "inactive");
            telemetry.addData("Claw", "%s | %s", !clawNArm.clawLeft ? "closed" : " open ", !clawNArm.clawRight ? "closed" : " open ");
            telemetry.update();
        }
    }
}
