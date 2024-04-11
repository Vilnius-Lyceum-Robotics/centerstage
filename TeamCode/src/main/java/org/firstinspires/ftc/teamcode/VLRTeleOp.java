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
import org.firstinspires.ftc.teamcode.hardware.Led;
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

        Led leftLed = new Led(hardwareMap, "Left", es);
        Led rightLed = new Led(hardwareMap, "Right", es);

        ElapsedTime looptime = new ElapsedTime();
        int lastLooptime = 0;


        boolean rightAutoOpen = true;
        boolean leftAutoOpen = true;

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
//            leftLed.test();
//            rightLed.test();
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
                rightAutoOpen = true;
                leftAutoOpen = true;
            } else {

                if (claw.leftIsClosed() && clawSensors.isCloseLeft()) {
                    leftLed.setColor(Led.Color.NONE);
                } else if (!claw.leftIsClosed() && clawSensors.isCloseLeft()) {
                    if (claw.isAutomatic() && lift.getPosition() == 0) {
                        claw.setLeftPos(Claw.ClawState.CLOSED);
                    }
                    leftLed.setColor(Led.Color.GREEN);
                } else if (claw.leftIsClosed()) {

                    if(lift.getPosition() == 0 && leftAutoOpen){
                        claw.setLeftPos(Claw.ClawState.OPEN);
                        leftAutoOpen = false;
                    } else if(lift.getPosition() != 0) {
                        leftAutoOpen = true;
                    }

                    leftLed.setColor(Led.Color.RED);
                } else {
                    leftLed.setColor(Led.Color.AMBER);
                }

                if (claw.rightIsClosed() && clawSensors.isCloseRight()) {
                    rightLed.setColor(Led.Color.NONE);
                } else if (!claw.rightIsClosed() && clawSensors.isCloseRight()) {
                    if (claw.isAutomatic() && lift.getPosition() == 0) {
                        claw.setRightPos(Claw.ClawState.CLOSED);
                    }
                    rightLed.setColor(Led.Color.GREEN);
                } else if (claw.rightIsClosed()) {

                    if(lift.getPosition() == 0 && rightAutoOpen) {
                        claw.setRightPos(Claw.ClawState.OPEN);
                        rightAutoOpen = false;
                    } else if(lift.getPosition() != 0){
                        rightAutoOpen = true;
                    }

                    rightLed.setColor(Led.Color.RED);
                } else {
                    rightLed.setColor(Led.Color.AMBER);
                }
            }

            telemetry.addData("CLaw Closed", "Right Claw Closed %b - Left Claw Closed $b", claw.rightIsClosed(), claw.leftIsClosed());
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
