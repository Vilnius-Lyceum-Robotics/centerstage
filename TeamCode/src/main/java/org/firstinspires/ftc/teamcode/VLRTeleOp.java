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
import org.firstinspires.ftc.teamcode.hardware.DistanceSensors;
import org.firstinspires.ftc.teamcode.hardware.PullUp;
import org.firstinspires.ftc.teamcode.hardware.Lift;

@Photon
@TeleOp(name = "VLRTeleOp")
public class VLRTeleOp extends LinearOpMode {

    @Override
    public void runOpMode() {
        DistanceSensors distanceSensors = new DistanceSensors(hardwareMap);
        Chassis chassis = new Chassis(hardwareMap, distanceSensors);
        GamepadEx gamepadEx = new GamepadEx(gamepad1);
        PullUp pullup = new PullUp(hardwareMap);
        Claw claw = new Claw(hardwareMap);
        Lift lift = new Lift(hardwareMap, claw, chassis, distanceSensors);

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
                chassis.toggleMode();
            }

            distanceSensors.process();

//            telemetry.addData("Distance between sensors",  "%.2f %.2f", distanceSensors.getMinDistance(), distanceSensors.getMaxDistance());
            telemetry.addData("Distance between sensors",  "%.2f %.2f", distanceSensors.leftDistance.get(), distanceSensors.rightDistance.get());
            telemetry.addData("Angle between sensors", "%.2f", distanceSensors.getAngle());
            telemetry.addData("Mode", chassis.currentMode);
            telemetry.addData("---------", "---------");
            telemetry.addData("Last loop ms", lastLooptime);
            telemetry.update();
//            System.out.println("Distance between sensors: " + distanceSensors.getRawLeft() + " " + distanceSensors.getRawRight());
            //System.out.println("Angle between sensors: " + distanceSensors.getAngle());
            lastLooptime = (int) looptime.milliseconds();
        }
    }
}
