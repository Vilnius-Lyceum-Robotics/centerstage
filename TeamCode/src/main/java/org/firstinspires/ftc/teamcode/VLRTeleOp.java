package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.Pose2d;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.outoftheboxrobotics.photoncore.Photon;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.hardware.Chassis;
import org.firstinspires.ftc.teamcode.hardware.Claw;
import org.firstinspires.ftc.teamcode.hardware.ClawSensors;
import org.firstinspires.ftc.teamcode.hardware.DistanceSensors;
import org.firstinspires.ftc.teamcode.hardware.PullUp;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.helpers.ModeManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Photon
@TeleOp(name = "VLRTeleOp")
public class VLRTeleOp extends LinearOpMode {
    private Chassis chassis;
    private GamepadEx gamepadEx;
    private PullUp pullup;
    private Lift lift;
    private Claw claw;
    private ClawSensors clawSensors;

    @Override
    public void runOpMode() {
        ExecutorService es = Executors.newCachedThreadPool();
        DistanceSensors distanceSensors = new DistanceSensors(es, hardwareMap);
        ClawSensors clawSensors = new ClawSensors(es, hardwareMap);
        Chassis chassis = new Chassis(hardwareMap, distanceSensors);
        GamepadEx gamepadEx = new GamepadEx(gamepad1);
        PullUp pullup = new PullUp(hardwareMap);
        Claw claw = new Claw(hardwareMap);
        Lift lift = new Lift(hardwareMap, claw, distanceSensors);

        ElapsedTime looptime = new ElapsedTime();
        int lastLooptime = 0;

        waitForStart();

        while (opModeIsActive()) {
            looptime.reset();
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

            if (gamepadEx.wasJustPressed(GamepadKeys.Button.A)) {
                pullup.toggle();
            }

            if (gamepadEx.wasJustPressed(GamepadKeys.Button.B)) {
                ModeManager.toggleMode();
            }

            distanceSensors.process();
            clawSensors.process();


            telemetry.addData("Claw sensors", "%.2f %.2f", clawSensors.getDistanceLeft(), clawSensors.getDistanceRight());
            telemetry.addData("Distance between sensors",  "%.2f %.2f", distanceSensors.getLeftDistance(), distanceSensors.getRightDistance());
            telemetry.addData("Angle between sensors", "%.2f", distanceSensors.getAngle());
            telemetry.addData("Mode", ModeManager.getMode());
            telemetry.addData("---------", "---------");
            telemetry.addData("Last loop ms", lastLooptime);
            telemetry.update();
            lastLooptime = (int) looptime.milliseconds();
        }
    }
}
