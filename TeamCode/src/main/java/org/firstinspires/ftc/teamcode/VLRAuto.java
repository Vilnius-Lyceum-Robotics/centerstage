package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.outoftheboxrobotics.photoncore.Photon;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.drivetrain.MecanumDrive;
import org.firstinspires.ftc.teamcode.hardware.Claw;
import org.firstinspires.ftc.teamcode.hardware.DistanceSensors;
import org.firstinspires.ftc.teamcode.hardware.FrontCamera;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.helpers.AutoConfig;
import org.firstinspires.ftc.teamcode.helpers.PreGameConfigurator;

@Photon
@Autonomous
public class VLRAuto extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        AutoConfig cfg = new AutoConfig();
        PreGameConfigurator configurator = new PreGameConfigurator(telemetry, new GamepadEx(gamepad1));
        boolean isRed = configurator.upDownSelect("Red", "Blue");
        boolean isNearBackboard = configurator.leftRightSelect("Backboard", "Audience");

        boolean shouldWait = configurator.upDownSelect("Wait 5s", "Do not wait");
        boolean shouldMoveLeft = configurator.leftRightSelect("Park to the side", "Do not move");

        FrontCamera cam = new FrontCamera(hardwareMap, isRed);

        Claw claw = new Claw(hardwareMap);
        DistanceSensors distanceSensors = new DistanceSensors(hardwareMap);
        Lift lift = new Lift(hardwareMap, claw, distanceSensors);

        Pose2d startPose = cfg.getStartPos(isRed, isNearBackboard);

        telemetry.addData("MAIN", "Ready to start");
        telemetry.update();

        ///////////////////////////////////////////////
        waitForStart();
        ///////////////////////////////////////////////
        cam.processUntilDetection();
//        telemetry.addData("ANGLE", "%.3f", (float) cam.propAng);
//        telemetry.update();
//        sleep(3000);
        lift.setExtension(1);
        lift.process();

        cam.process(5);
        cam.close();

        FrontCamera.PropPos propPosition = cam.teamPropPos;
        telemetry.addData("MAIN", "Prop position: " + propPosition);
        telemetry.addData("ANGLE", "%.3f", (float) cam.propAng);
        telemetry.update();

        MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);
        TrajectoryActionBuilder navBuilder = drive.actionBuilder(startPose);

        // Left or right
        boolean isLeft = propPosition == FrontCamera.PropPos.LEFT;

        double xDelta = (-12 + cfg.ROBOT_WIDTH / 2) + 2.5;
        xDelta = isLeft ? xDelta : -xDelta;

        double yDelta = (24 * 2 - cfg.ROBOT_LENGTH / 2) - 8; // Position of prop relative to start point

        int allianceCoef = isRed ? 1 : -1; // 1 for red, -1 for blue

        boolean isSidecase = (isRed && isNearBackboard && propPosition == FrontCamera.PropPos.RIGHT) ||
                (!isRed && isNearBackboard && propPosition == FrontCamera.PropPos.LEFT);

        // Failsafe


        if (isSidecase) {
            navBuilder = navBuilder.splineToLinearHeading(new Pose2d(xDelta + 24 + 14.0, // todo adjust live
                    startPose.position.y + allianceCoef * yDelta,
                    Math.toRadians(180)), Math.toRadians(180));
        } else {
            if (propPosition == FrontCamera.PropPos.CENTER) {
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

        navBuilder = navBuilder.stopAndAdd(telemetryPacket -> false).waitSeconds(0.1).afterTime(0, () -> claw.setLeftPos(Claw.ClawState.OPEN)).waitSeconds(0.3);

        if (propPosition == FrontCamera.PropPos.CENTER)
            navBuilder = navBuilder.lineToY((yDelta - 3.5) * allianceCoef);

        // Move to backboard
        double backboardX = 72 - 24 - 6.5;
        double positionOffset = propPosition == FrontCamera.PropPos.LEFT ? 6 : propPosition == FrontCamera.PropPos.CENTER ? 0 : -6;
        double backboardY = (allianceCoef * (-24 * 1.5)) + positionOffset; // offset for claw

        if (shouldWait) {
            navBuilder = navBuilder.waitSeconds(5);
        }

        if (!isNearBackboard) {
            // Is in audience side
            if (propPosition != FrontCamera.PropPos.CENTER) {
                // Advance forward, no need to dodge
                navBuilder = navBuilder
                        .lineToX(-72 + 24 * 1.5 - allianceCoef * (propPosition == FrontCamera.PropPos.LEFT ? -2 : 2))
                        .turnTo(Math.toRadians(95 * allianceCoef))
                        .lineToY(-12 * allianceCoef)
                        // .splineToLinearHeading(new Pose2d(-40, allianceCoef * (-24 + 0.0),  0), 0)
                        .splineToLinearHeading(new Pose2d(-72 + 24 * 1.5, -12 * allianceCoef, Math.toRadians(0)), Math.toRadians(0));
            } else {
                // Move to the left / right
                // TODO maybe instead of going around just go through the middle part?
                navBuilder = navBuilder
                        .splineToLinearHeading(new Pose2d(-72 + 24 / 2 + 10.0, (-24 - 12) * allianceCoef, Math.toRadians(allianceCoef * 90)), Math.toRadians(0), (pose2dDual, posePath, v) -> 10)
                        .splineToLinearHeading(new Pose2d(-72 + 24 / 2 + 10.0, -12 * allianceCoef, Math.toRadians(180)), Math.toRadians(0), (pose2dDual, posePath, v) -> 15);
            }
            navBuilder = navBuilder.lineToX(0).lineToX(backboardX)
                    .afterTime(0, () -> lift.setExtension(3))
                    .setTangent(Math.PI / 2).lineToY(backboardY);
        } else {
            // can just proceed to backboard
            if (propPosition == FrontCamera.PropPos.CENTER) {
                navBuilder = navBuilder.lineToY(-24 * 1.5 * allianceCoef)
                        .afterTime(0, () -> lift.setExtension(3))
                        .turnTo(0)
                        .splineToLinearHeading(new Pose2d(backboardX, backboardY, Math.toRadians(0)), Math.toRadians(0));
            } else {
                navBuilder = navBuilder
                        .lineToX(backboardX)
                        .afterTime(0, () -> lift.setExtension(3))
                        .turnTo(0)
                        .setTangent(Math.PI / 2)
                        .lineToY(backboardY, (pose2dDual, posePath, v) -> 15);
            }
        }
        navBuilder = navBuilder.afterTime(0, () -> lift.shouldContinueAutonomousLoop.set(false)); // Stop lift loop to not hang when running (switch trajectory)


        //////////////////////////////////////////////////////////
        Actions.runBlocking(new ParallelAction(navBuilder.build(),
                lift.autonomous(distanceSensors::process)));
        //////////////////////////////////////////////////////////
        sleep(150);

        // Align X with backboard using distance sensors
        double dist = distanceSensors.getMinDistance() - 2.9; // todo tune
        Actions.runBlocking(drive.actionBuilder(new Pose2d(backboardX, backboardY, 0))
                .lineToX(backboardX + dist)
                .build());

        sleep(500);
        claw.setRightPos(Claw.ClawState.OPEN);
        sleep(350);
        Actions.runBlocking(drive.actionBuilder(new Pose2d(backboardX + dist, backboardY, 0))
                .lineToX(backboardX - 1.5)
                .build());
        lift.setExtension(1);

        TrajectoryActionBuilder parkingTraj = drive.actionBuilder(new Pose2d(backboardX - 1.5, backboardY, 0));
        parkingTraj = parkingTraj
                .setTangent(Math.PI / 2)
                .lineToY(-(72 - 10) * allianceCoef)
                .setTangent(Math.PI)
                .lineToX(backboardX + 14)
                .waitSeconds(5).afterTime(0.2, () -> lift.setExtension(0));

        if (shouldMoveLeft) {
            Actions.runBlocking(new ParallelAction(parkingTraj.build(),
                    lift.autonomous(distanceSensors::process)));
        } else {
            lift.setExtension(0);
            while (!isStopRequested()) {
                lift.process();
                sleep(5);
            }
        }
    }
}
