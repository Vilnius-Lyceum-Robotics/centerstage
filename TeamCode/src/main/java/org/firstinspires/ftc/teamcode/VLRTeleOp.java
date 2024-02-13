package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.hardware.Chassis;
import org.firstinspires.ftc.teamcode.hardware.ClawNArm;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Plane;
import org.firstinspires.ftc.ftclib.gamepad.GamepadEx;
import org.firstinspires.ftc.ftclib.gamepad.KeyReader;
import org.firstinspires.ftc.ftclib.gamepad.ToggleButtonReader;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

@TeleOp(name = "VLRTeleOp")
public class VLRTeleOp extends LinearOpMode {
    private Chassis chassis;
    private ClawNArm clawNArm;
    private Lift lift;
    private Plane plane;
    private GamepadEx gamepadEx;
    private ToggleButtonReader leftClawToggle;
    private ToggleButtonReader rightClawToggle;
    private int armPosition = 0; // 0 - front, 1 - carry, 2 - back

    @Override
    public void runOpMode() {
        chassis = new Chassis(hardwareMap);
        clawNArm = new ClawNArm(hardwareMap);
        lift = new Lift(hardwareMap);
        plane = new Plane(hardwareMap);
        gamepadEx = new GamepadEx(gamepad1);
        leftClawToggle = new ToggleButtonReader(gamepadEx, GamepadEx.Button.LEFT_BUMPER);
        rightClawToggle = new ToggleButtonReader(gamepadEx, GamepadEx.Button.RIGHT_BUMPER);

        waitForStart();

        while (opModeIsActive()) {
            gamepadEx.readButtons();

            chassis.setPower(1 - gamepadEx.getTriggerValue(GamepadEx.Trigger.RIGHT_TRIGGER) * 0.5);
            // Assuming chassis.drive() can accept individual joystick values.
            chassis.drive(gamepadEx.getLeftStickX(), gamepadEx.getLeftStickY(), gamepadEx.getRightStickX());

            if (gamepadEx.wasJustPressed(GamepadEx.Button.DPAD_LEFT)) {
                armPosition = Math.max(0, armPosition - 1);
            } else if (gamepadEx.wasJustPressed(GamepadEx.Button.DPAD_RIGHT)) {
                armPosition = Math.min(2, armPosition + 1);
            }

            switch (armPosition) {
                case 0:
                    clawNArm.armFront();
                    break;
                case 1:
                    clawNArm.armCarryPosition();
                    break;
                case 2:
                    clawNArm.armBack();
                    break;
            }

            leftClawToggle.readValue();
            if (leftClawToggle.getState()) {
                clawNArm.toggleClawLeft();
            }

            rightClawToggle.readValue();
            if (rightClawToggle.getState()) {
                clawNArm.toggleClawRight();
            }

            if (gamepadEx.getTriggerValue(GamepadEx.Trigger.RIGHT_TRIGGER) > 0.8) {
                plane.launch();
            }

            telemetry.addData("Arm Position", armPosition);
            telemetry.update();

            sleep(20);
        }
    }
}
