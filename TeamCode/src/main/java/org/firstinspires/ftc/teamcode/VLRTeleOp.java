package org.firstinspires.ftc.teamcode;

import androidx.annotation.NonNull;

import com.acmerobotics.roadrunner.Pose2d;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.outoftheboxrobotics.photoncore.Photon;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.hardware.Chassis;
import org.firstinspires.ftc.teamcode.hardware.Claw;
import org.firstinspires.ftc.teamcode.hardware.ClawSensors;
import org.firstinspires.ftc.teamcode.hardware.DistanceSensors;
import org.firstinspires.ftc.teamcode.hardware.Led;
import org.firstinspires.ftc.teamcode.hardware.PullUp;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Plane;
import org.firstinspires.ftc.teamcode.helpers.BooleanState;
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

    public BooleanState rightAutoOpen;
    public BooleanState leftAutoOpen;
    public BooleanState automaticLift;

    @Override
    public void runOpMode() {
        rightAutoOpen = new BooleanState(true);
        leftAutoOpen = new BooleanState(true);
        automaticLift = new BooleanState(false);

        ExecutorService es = Executors.newCachedThreadPool();
        DistanceSensors distanceSensors = new DistanceSensors(es, hardwareMap);
        ClawSensors clawSensors = new ClawSensors(es, hardwareMap);
        Chassis chassis = new Chassis(hardwareMap, distanceSensors);
        GamepadEx gamepadEx = new GamepadEx(gamepad1);
        GamepadEx gp2 = new GamepadEx(gamepad2);
        PullUp pullup = new PullUp(hardwareMap);
        Claw claw = new Claw(hardwareMap);
        Lift lift = new Lift(hardwareMap, claw, distanceSensors);

        Plane plane = new Plane(hardwareMap);

        Led leftLed = new Led(hardwareMap, "Left", es);
        Led rightLed = new Led(hardwareMap, "Right", es);

        ElapsedTime looptime = new ElapsedTime();
        int lastLooptime = 0;

        waitForStart();

        while (opModeIsActive()) {
            looptime.reset();
            gamepadEx.readButtons();
            chassis.setPower(1 - gamepadEx.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER) * 0.75);

            chassis.drive(new Pose2d(gamepadEx.getLeftX(), gamepadEx.getLeftY(), gamepadEx.getRightX()));

            if (lift.encoderIsEnabled()) {
                lift.process(automaticLift);
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

            if (gamepadEx.wasJustPressed(GamepadKeys.Button.A)) {
                pullup.toggle();
            }

            if (gamepadEx.wasJustPressed(GamepadKeys.Button.B)) {
                ModeManager.toggleMode();
            }

            if (gamepadEx.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER) >= 1.0) plane.launch();

            distanceSensors.process();
            clawSensors.process();
            leftLed.process();
            rightLed.process();
            // carry - blink red -done
            // down || carry - claw closed = RED
            // down || carry - claw open = NONE
            // isClose() = GREEN
            // pixel and closed = AMBER


            if (lift.getPosition() == 1) {
                leftLed.setColor(Led.Color.RED);
                leftLed.blink(500);
                rightLed.setColor(Led.Color.RED);
                rightLed.blink(500);
                rightAutoOpen.set(true);
                leftAutoOpen.set(true);
            } else {
                // both claws closed - and both have pixel, lift go to position 1
                if (automaticLift.get() && claw.rightIsClosed() && claw.leftIsClosed() && clawSensors.isCloseLeft() && clawSensors.isCloseRight()) {
                    lift.setExtension(1);
                    automaticLift.set(false);
                }
//                leftLed.setColor(Led.Color.NONE);
//                rightLed.setColor(Led.Color.NONE);
                claw.manageClaw(ModeManager.getMode() == ModeManager.Mode.NORMAL ? leftLed : rightLed, Claw.Hand.LEFT, clawSensors, lift, leftAutoOpen);
                claw.manageClaw(ModeManager.getMode() == ModeManager.Mode.NORMAL ? rightLed : leftLed, Claw.Hand.RIGHT, clawSensors, lift, rightAutoOpen);
            }
            // Override control

            if (gp2.isDown(GamepadKeys.Button.LEFT_STICK_BUTTON)) {
                pullup.disableEncoders();
            } else {
                pullup.enableEncoders();
            }
            pullup.setManualPower(gp2.getLeftY());
            if (!pullup.encodersEnabled) {
                telemetry.addData("OVERRIDE", "PULLUP ENCODERS DISABLED");
            }

            if (gp2.isDown(GamepadKeys.Button.RIGHT_STICK_BUTTON)) {
                lift.disableEncoder();
            } else {
                lift.enableEncoder();
            }
            lift.setManualPower(gp2.getRightY());
            if (!lift.encoderIsEnabled()) {
                telemetry.addData("OVERRIDE", "LIFT ENCODERS DISABLED");

            }

            telemetry.addData("CLaw Closed", "Right Claw Closed %b - Left Claw Closed $b", claw.isClosed(Claw.Hand.RIGHT), claw.isClosed(Claw.Hand.LEFT));
            telemetry.addData("Claw sensors", "%.2f %.2f", clawSensors.getDistanceLeft(), clawSensors.getDistanceRight());
            telemetry.addData("Distance between sensors", "%.2f %.2f", distanceSensors.getLeftDistance(), distanceSensors.getRightDistance());
            telemetry.addData("Angle between sensors", "%.2f", distanceSensors.getAngle());
            telemetry.addData("Mode", ModeManager.getMode());
            telemetry.addData("---------", "---------");
            telemetry.addData("Last loop ms", lastLooptime);
            telemetry.update();
            lastLooptime = (int) looptime.milliseconds();
        }
    }
}
