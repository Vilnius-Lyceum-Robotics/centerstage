package org.firstinspires.ftc.teamcode;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Arclength;
import com.acmerobotics.roadrunner.InstantFunction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Pose2dDual;
import com.acmerobotics.roadrunner.PosePath;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.TrajectoryBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.VelConstraint;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.drivetrain.MecanumDrive;
import org.firstinspires.ftc.teamcode.hardware.Camera;
import org.firstinspires.ftc.teamcode.hardware.ClawNArm;
import org.firstinspires.ftc.teamcode.hardware.Controls;
import org.firstinspires.ftc.teamcode.helpers.AutoConfig;
import org.firstinspires.ftc.teamcode.helpers.PreGameConfigurator;

import java.util.concurrent.TimeUnit;

@Autonomous
public class VLRAuto extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        AutoConfig cfg = new AutoConfig();
        PreGameConfigurator configurator = new PreGameConfigurator(telemetry, new Controls(gamepad1, hardwareMap.get(IMU.class, "imu")));

        boolean isRed = configurator.upDownSelect("Red", "Blue");
        boolean isNearBackboard = configurator.leftRightSelect("Backboard", "Audience");

        boolean shouldWait = configurator.upDownSelect("Wait 7s", "Do not wait");
        boolean shouldMoveLeft = configurator.leftRightSelect("Park to the side", "Do not move");

        Camera cam = new Camera(hardwareMap, isRed);
        ClawNArm claw = new ClawNArm(hardwareMap);

        Pose2d startPose = cfg.getStartPos(isRed, isNearBackboard);

        telemetry.addData("MAIN", "Ready to start");
        telemetry.update();

        cam.process(5);
        ///////////////////////////////////////////////
        waitForStart();
        ///////////////////////////////////////////////
        claw.ArmCarryPos();
        sleep(2000);
        cam.process(5);

        Camera.PropPos propPosition = cam.teamPropPos;
        telemetry.addData("MAIN", "Prop position: " + propPosition);
        telemetry.update();

        MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);
        TrajectoryActionBuilder navBuilder = drive.actionBuilder(startPose);

        // Left or right
        boolean isLeft = propPosition == Camera.PropPos.LEFT;

        double xDelta = (-12 + cfg.ROBOT_WIDTH / 2) + 5.5;
        xDelta = isLeft ? xDelta : -xDelta;

        double yDelta = (24 * 2 - cfg.ROBOT_LENGTH / 2) - 8; // Position of prop relative to start point

        int allianceCoef = isRed ? 1 : -1; // 1 for red, -1 for blue

        boolean isSidecase = (isRed && isNearBackboard && propPosition == Camera.PropPos.RIGHT) ||
                (!isRed && isNearBackboard && propPosition == Camera.PropPos.LEFT);

        if (isSidecase) {
            navBuilder = navBuilder.splineToLinearHeading(new Pose2d(xDelta + 24 + 14.0, // todo adjust live
                    startPose.position.y + allianceCoef * yDelta,
                    Math.toRadians(180)), Math.toRadians(180));
        } else {
            if (propPosition == Camera.PropPos.CENTER) {
                yDelta = -24 - cfg.ROBOT_LENGTH / 2.0 - 4.2;
                navBuilder = navBuilder.lineToY(allianceCoef * yDelta);
            } else {
                telemetry.addData("MAIN", "xDelta: " + xDelta);
                telemetry.update();

                double angle = 0; // Angle the robot should be facing to hit the prop
                if (isLeft && isRed) angle = Math.PI;
                if (!isLeft && !isRed) angle = Math.PI;

                // Determine prop position on field plane
                Pose2d placePos = new Pose2d(startPose.position.x + xDelta,
                        startPose.position.y + allianceCoef * yDelta, angle);

                // Move up to not hit the pillar on the left / right while turning
                navBuilder = navBuilder.lineToY(startPose.position.y + allianceCoef * (yDelta / 1.5))
                        // and go to the prop
                        .splineToLinearHeading(placePos, angle);
            }
        }

        navBuilder = navBuilder.stopAndAdd(telemetryPacket -> false).waitSeconds(0.1).afterTime(0.2, claw::ToggleClawLeft)
                .afterTime(0.4, () -> claw.clawRotator.setPosition(0.2)).waitSeconds(0.3);

        if (propPosition == Camera.PropPos.CENTER)
            navBuilder = navBuilder.lineToY((yDelta - 3) * allianceCoef);

        // Move to backboard
        double backboardX = 72 - 24 - .5; // todo adjust live
        double backboardY = allianceCoef * (-24 * 1.5);

        double apriltagDeltaY = 0;
        if (propPosition == Camera.PropPos.LEFT) apriltagDeltaY = -5;
        if (propPosition == Camera.PropPos.RIGHT) apriltagDeltaY = 5;


        if (!isNearBackboard) {
            if (shouldWait) {
                navBuilder.waitSeconds(7);
            }
            if (propPosition != Camera.PropPos.CENTER) {
                // Advance forward
                navBuilder = navBuilder//.lineToY() // todo adjust live
                        .lineToX(-72 + 24 * 1.5 - allianceCoef * (propPosition == Camera.PropPos.LEFT ? -2 : 2))
                        .turnTo(Math.toRadians(95 * allianceCoef))
                        .lineToY(-12 * allianceCoef)
                        // .splineToLinearHeading(new Pose2d(-40, allianceCoef * (-24 + 0.0),  0), 0)
                        .splineToLinearHeading(new Pose2d(-72 + 24 * 1.5, -12 * allianceCoef, Math.toRadians(0)), Math.toRadians(0));
            } else {
                // Move to the left / right
                navBuilder = navBuilder//.lineToX(-72 + 24 / 2 + 6.0) // todo adjust live

                        .splineToLinearHeading(new Pose2d(-72 + 24 / 2 + 6.0, (-24 - 12) * allianceCoef, Math.toRadians(allianceCoef * 90)), Math.toRadians(0), (pose2dDual, posePath, v) -> 10)
                        .splineToLinearHeading(new Pose2d(-72 + 24 / 2 + 12.0, -12 * allianceCoef, Math.toRadians(180)), Math.toRadians(0), (pose2dDual, posePath, v) -> 15);
            }
            navBuilder = navBuilder.lineToX(0).lineToX(backboardX).turn(Math.toRadians(90)).lineToY(backboardY - apriltagDeltaY).turnTo(Math.toRadians(180));
            // .splineToLinearHeading(new Pose2d(backboardX, backboardY, Math.toRadians(180)), Math.toRadians(0), (pose2dDual, posePath, v) -> 5);
        } else {
            // can just proceed to backboard
            if (propPosition == Camera.PropPos.CENTER) {
                navBuilder = navBuilder.lineToY(-24 * 1.75 * allianceCoef)
                        .splineToLinearHeading(new Pose2d(backboardX, backboardY, Math.toRadians(180)), Math.toRadians(0));
            } else {
                navBuilder = navBuilder
                        //.splineToLinearHeading(new Pose2d(backboardX, backboardY, Math.toRadians(180)), isRed ? 0 : Math.toRadians(180));
                        .lineToX(backboardX, (pose2dDual, posePath, v) -> 15);
            }
        }

        //navBuilder.lineToY(backboardY + apriltagDeltaY);
        // todo arm extension bullshit

        //////////////////////////////////////////////////////////
        Actions.runBlocking(navBuilder.build());
        //////////////////////////////////////////////////////////

        claw.ArmBackAuto();
        ElapsedTime timeout = new ElapsedTime();
        timeout.reset();
        while (!claw.rotatorController.atSetPoint() && timeout.time(TimeUnit.SECONDS) < 5) {
            claw.ArmBackAuto();
            sleep(2);
        }
        claw.rotator.stopMotor();

        sleep(500);
        claw.ToggleClawRight();
        sleep(1000);
        claw.ToggleClawRight();
        claw.ToggleClawLeft();


        claw.ArmCarryPos();

        TrajectoryActionBuilder parkingTraj = drive.actionBuilder(new Pose2d(backboardX, backboardY, Math.PI));
        parkingTraj = parkingTraj.afterTime(0.2, () -> {
                    timeout.reset();
                    while (!claw.rotatorController.atSetPoint() && opModeIsActive() && timeout.seconds() < 2) {
                        claw.ArmCarryPos();
                        sleep(2);
                    }

                })
                .turnTo(Math.PI/2)
                .lineToY(-(72 - 12) * allianceCoef);

        if (shouldMoveLeft) {
            Actions.runBlocking(parkingTraj.build());
        }
        // todo parking
    }
}
