package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.Pose2d;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.outoftheboxrobotics.photoncore.Photon;

import org.firstinspires.ftc.teamcode.hardware.Chassis;
import org.firstinspires.ftc.teamcode.hardware.Claw;
import org.firstinspires.ftc.teamcode.hardware.Claw;
import org.firstinspires.ftc.teamcode.hardware.PullUp;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Lift;

@Photon
@TeleOp(name = "VLRTeleOp")
public class VLRTeleOp extends LinearOpMode {
    private Chassis chassis;
    private GamepadEx gamepadEx;
    private PullUp pullup;
    private Lift lift;
    private Claw claw;
    private Lift lift;
    private Claw claw;

    @Override
    public void runOpMode() {
        chassis = new Chassis(hardwareMap);

        gamepadEx = new GamepadEx(gamepad1);
        pullup = new PullUp(hardwareMap);
        claw = new Claw(hardwareMap);
        lift = new Lift(hardwareMap, claw);
        claw = new Claw(hardwareMap);
        lift = new Lift(hardwareMap, claw);
        waitForStart();

        while (opModeIsActive()) {
            gamepadEx.readButtons();
            chassis.setPower(1 - gamepadEx.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER) * 0.75);

            chassis.drive(new Pose2d(gamepadEx.getLeftX(), gamepadEx.getLeftY(), gamepadEx.getRightX()));
            lift.process();


            if (gamepadEx.wasJustPressed(GamepadKeys.Button.DPAD_UP)) {
                lift.extend();
            } else if (gamepadEx.wasJustPressed(GamepadKeys.Button.DPAD_DOWN)) {
                lift.retract();
            }

            if (gamepadEx.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER)) {
                claw.toggleLeft();
            }
            if (gamepadEx.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER)) {
                claw.toggleRight();
            }
            if (gamepadEx.wasJustPressed(GamepadKeys.Button.DPAD_UP)) {
                lift.extend();
            } else if (gamepadEx.wasJustPressed(GamepadKeys.Button.DPAD_DOWN)) {
                lift.retract();
            }

            if (gamepadEx.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER)) {
                claw.toggleLeft();
            }
            if (gamepadEx.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER)) {
                claw.toggleRight();
            }
        }
    }
}
