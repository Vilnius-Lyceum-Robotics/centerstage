//package org.firstinspires.ftc.teamcode;
//
//import com.acmerobotics.roadrunner.Pose2d;
//import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
//import com.acmerobotics.roadrunner.ftc.Actions;
//import com.outoftheboxrobotics.photoncore.Photon;
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.hardware.IMU;
//import com.qualcomm.robotcore.util.ElapsedTime;
//
//import org.firstinspires.ftc.teamcode.drivetrain.MecanumDrive;
//import org.firstinspires.ftc.teamcode.hardware.FrontCamera;
//import org.firstinspires.ftc.teamcode.hardware.Controls;
//import org.firstinspires.ftc.teamcode.helpers.AutoConfig;
//import org.firstinspires.ftc.teamcode.helpers.PreGameConfigurator;
//
//import java.util.concurrent.TimeUnit;
//
//@Photon
//@Autonomous
//public class VLRAuto extends LinearOpMode {
//    @Override
//    public void runOpMode() throws InterruptedException {
//        AutoConfig cfg = new AutoConfig();
//        PreGameConfigurator configurator = new PreGameConfigurator(telemetry, new Controls(gamepad1, hardwareMap.get(IMU.class, "imu")));
//
//        boolean isRed = configurator.upDownSelect("Red", "Blue");
//        boolean isNearBackboard = configurator.leftRightSelect("Backboard", "Audience");
//
//        boolean shouldWait = configurator.upDownSelect("Wait 7s", "Do not wait");
//        boolean shouldMoveLeft = configurator.leftRightSelect("Park to the side", "Do not move");
//
//        FrontCamera cam = new FrontCamera(hardwareMap, isRed);
//        BackCamera backCam = new BackCamera(hardwareMap);
//        ClawNArm claw = new ClawNArm(hardwareMap);
//
//        Pose2d startPose = cfg.getStartPos(isRed, isNearBackboard);
//
//        telemetry.addData("MAIN", "Ready to start");
//        telemetry.update();
//
//        cam.process(5);
//        telemetry.addData("ANGLE", "%.3f", (float) cam.propAng);
//        ///////////////////////////////////////////////
//        waitForStart();
//        ///////////////////////////////////////////////
//        claw.ArmCarryPos();
//        sleep(2000);
//        cam.process(5);
//        cam.close();
//
//        FrontCamera.PropPos propPosition = cam.teamPropPos;
//        telemetry.addData("MAIN", "Prop position: " + propPosition);
//        telemetry.addData("ANGLE", "%.3f", (float) cam.propAng);
//        telemetry.update();
//
//        MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);
//        TrajectoryActionBuilder navBuilder = drive.actionBuilder(startPose);
//
//        // Left or right
//        boolean isLeft = propPosition == FrontCamera.PropPos.LEFT;
//
//        double xDelta = (-12 + cfg.ROBOT_WIDTH / 2) + 2.5;
//        xDelta = isLeft ? xDelta : -xDelta;
//
//        double yDelta = (24 * 2 - cfg.ROBOT_LENGTH / 2) - 8; // Position of prop relative to start point
//
//        int allianceCoef = isRed ? 1 : -1; // 1 for red, -1 for blue
//
//        boolean isSidecase = (isRed && isNearBackboard && propPosition == FrontCamera.PropPos.RIGHT) ||
//                (!isRed && isNearBackboard && propPosition == FrontCamera.PropPos.LEFT);
//
//        // Failsafe
//
//
//        if (isSidecase) {
//            navBuilder = navBuilder.splineToLinearHeading(new Pose2d(xDelta + 24 + 14.0, // todo adjust live
//                    startPose.position.y + allianceCoef * yDelta,
//                    Math.toRadians(180)), Math.toRadians(180));
//        } else {
//            if (propPosition == FrontCamera.PropPos.CENTER) {
//                yDelta = -24 - cfg.ROBOT_LENGTH / 2.0 - 4.2;
//                navBuilder = navBuilder.lineToY(allianceCoef * yDelta);
//            } else {
//                telemetry.addData("MAIN", "xDelta: " + xDelta);
//                telemetry.update();
//
//                double angle = 0; // Angle the robot should be facing to hit the prop
//                if (isLeft && isRed) angle = Math.PI;
//                if (!isLeft && !isRed) angle = Math.PI;
//
//                // Determine prop position on field plane
//                Pose2d placePos = new Pose2d(startPose.position.x + xDelta,
//                        startPose.position.y + allianceCoef * yDelta, angle);
//
//                // Move up to not hit the pillar on the left / right while turning
//                navBuilder = navBuilder.lineToY(startPose.position.y + allianceCoef * (yDelta / 1.5))
//                        // and go to the prop
//                        .splineToLinearHeading(placePos, angle);
//            }
//        }
//
//        navBuilder = navBuilder.stopAndAdd(telemetryPacket -> false).waitSeconds(0.1).afterTime(0.2, claw::ToggleClawLeft)
//                .afterTime(0.4, () -> claw.clawRotator.setPosition(0.8)).waitSeconds(0.3)
//                .afterTime(0, backCam::build); // Open apriltag camera after placing
//
//        if (propPosition == FrontCamera.PropPos.CENTER)
//            navBuilder = navBuilder.lineToY((yDelta - 3.5) * allianceCoef);
//
//        // Move to backboard
//        double backboardX = 72 - 24 - 6.5 + (!isNearBackboard ? -4 : 0); // todo adjust live
//        double backboardY = allianceCoef * (-24 * 1.5);
//
//
//        if (!isNearBackboard) {
//            if (shouldWait) {
//                navBuilder = navBuilder.waitSeconds(7);
//            }
//            if (propPosition != FrontCamera.PropPos.CENTER) {
//                // Advance forward
//                navBuilder = navBuilder//.lineToY() // todo adjust live
//                        .lineToX(-72 + 24 * 1.5 - allianceCoef * (propPosition == FrontCamera.PropPos.LEFT ? -2 : 2))
//                        .turnTo(Math.toRadians(95 * allianceCoef))
//                        .lineToY(-12 * allianceCoef)
//                        // .splineToLinearHeading(new Pose2d(-40, allianceCoef * (-24 + 0.0),  0), 0)
//                        .splineToLinearHeading(new Pose2d(-72 + 24 * 1.5, -12 * allianceCoef, Math.toRadians(0)), Math.toRadians(0));
//            } else {
//                // Move to the left / right
//                navBuilder = navBuilder//.lineToX(-72 + 24 / 2 + 6.0) // todo adjust live
//
//                        .splineToLinearHeading(new Pose2d(-72 + 24 / 2 + 10.0, (-24 - 12) * allianceCoef, Math.toRadians(allianceCoef * 90)), Math.toRadians(0), (pose2dDual, posePath, v) -> 10)
//                        .splineToLinearHeading(new Pose2d(-72 + 24 / 2 + 10.0, -12 * allianceCoef, Math.toRadians(180)), Math.toRadians(0), (pose2dDual, posePath, v) -> 15);
//            }
//            navBuilder = navBuilder.lineToX(0).lineToX(backboardX).turnTo(Math.toRadians(180)).setTangent(Math.PI / 2).lineToY(backboardY);
//            // .splineToLinearHeading(new Pose2d(backboardX, backboardY, Math.toRadians(180)), Math.toRadians(0), (pose2dDual, posePath, v) -> 5);
//        } else {
//            // can just proceed to backboard
//            if (propPosition == FrontCamera.PropPos.CENTER) {
//                navBuilder = navBuilder.lineToY(-24 * 1.75 * allianceCoef)
//                        .splineToLinearHeading(new Pose2d(backboardX, backboardY, Math.toRadians(180)), Math.toRadians(0));
//            } else {
//                navBuilder = navBuilder
//                        //.splineToLinearHeading(new Pose2d(backboardX, backboardY, Math.toRadians(180)), isRed ? 0 : Math.toRadians(180));
//                        .lineToX(backboardX, (pose2dDual, posePath, v) -> 15);
//            }
//        }
//
//        //////////////////////////////////////////////////////////
//        Actions.runBlocking(navBuilder.build());
//        //////////////////////////////////////////////////////////
//        sleep(150);
//
//        claw.ArmBackAuto();
//        ElapsedTime timeout = new ElapsedTime();
//        timeout.reset();
//        claw.clawR.setPosition(0.65);
//        while (!claw.rotatorController.atSetPoint() && timeout.time(TimeUnit.SECONDS) < 5) {
//            claw.ArmBackAuto();
//            sleep(2);
//        }
//        claw.clawR.setPosition(0.7);
//
//        // Apriltag navigation X
//        int correct = 0;
//        timeout.reset();
//        while (!isStopRequested() || timeout.seconds() > 4) {
//            double offset = backCam.process(propPosition);
//
//            telemetry.addData("Distance", "%.6f", offset);
//            telemetry.update();
//
//            if (Math.abs(offset) < 1) correct++;
//            else {
//                correct = 0;
//
//                TrajectoryActionBuilder builder = drive.actionBuilder(new Pose2d(backboardX, backboardY, Math.PI))
//                        .setTangent(Math.PI / 2)
//                        .lineToY(backboardY + offset, (pose2dDual, posePath, v) -> 8);
//
//                Actions.runBlocking(builder.build());
//                backboardY += offset;
//            }
//
//            if (correct >= 2) // Require at least two to make sure position is correct
//                break;
//        }
//        claw.clawR.setPosition(0.75);
//        // X
//        correct = 0;
//        timeout.reset();
//        while (!isStopRequested() || timeout.seconds() > 4) {
//            double xOffset = backCam.distToBoard() - 8.5;
//
//            telemetry.addData("Distance to board position", "%.6f", backCam.distToBoard());
//            telemetry.update();
//
//            if (Math.abs(xOffset) < 1) correct++;
//            else {
//                correct = 0;
//
//                TrajectoryActionBuilder builder = drive.actionBuilder(new Pose2d(backboardX, backboardY, Math.PI))
//                        .lineToX(backboardX + xOffset, (pose2dDual, posePath, v) -> 5);
//
//                Actions.runBlocking(builder.build());
//                backboardX += xOffset;
//            }
//
//            if (correct >= 2) break;
//        }
//        claw.clawR.setPosition(0.8);
//
//        Actions.runBlocking(drive.actionBuilder(new Pose2d(backboardX, backboardY, Math.PI))
//                .setTangent(Math.PI / 2)
//                .lineToY(backboardY - 1.5)
//                .build());
//
//        backboardY -= 1.5;
//
//        claw.rotator.stopMotor();
//
//        sleep(500);
//        claw.ToggleClawRight();
//        sleep(1000);
//        claw.ToggleClawRight();
//        claw.ToggleClawLeft();
//
//        claw.ArmCarryPos();
//        timeout.reset();
//        while (!claw.rotatorController.atSetPoint() && opModeIsActive() && timeout.seconds() < 2) {
//            claw.ArmCarryPos();
//            sleep(2);
//        }
//
//        TrajectoryActionBuilder parkingTraj = drive.actionBuilder(new Pose2d(backboardX, backboardY, Math.PI));
//        parkingTraj = parkingTraj
//                .setTangent(Math.PI / 2)
//                .lineToY(-(72 - 8) * allianceCoef)
//                .setTangent(Math.PI)
//                .lineToX(backboardX + 14);
//
//        if (shouldMoveLeft) {
//            Actions.runBlocking(parkingTraj.build());
//        }
//        // todo parking
//    }
//}
