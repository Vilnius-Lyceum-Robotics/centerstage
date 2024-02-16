package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.teamcode.drivetrain.MecanumDrive;
import org.firstinspires.ftc.teamcode.hardware.Camera;
import org.firstinspires.ftc.teamcode.hardware.ClawNArm;
import org.firstinspires.ftc.teamcode.hardware.Controls;
import org.firstinspires.ftc.teamcode.helpers.AutoConfig;
import org.firstinspires.ftc.teamcode.helpers.PreGameConfigurator;

@Autonomous
public class VLRAuto extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        AutoConfig cfg = new AutoConfig();
        PreGameConfigurator configurator = new PreGameConfigurator(telemetry, new Controls(gamepad1, hardwareMap.get(IMU.class, "imu")));

        boolean isRed = configurator.upDownSelect("Red", "Blue");
        boolean isNearBackboard = configurator.leftRightSelect("Backboard", "Audience");

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

        double xDelta = (-12 + cfg.ROBOT_WIDTH / 2) + 4;
        xDelta = isLeft ? xDelta : -xDelta;

        double yDelta = (24 * 2 - cfg.ROBOT_LENGTH / 2) - 8; // Position of prop relative to start point

        int allianceCoef = isRed ? 1 : -1; // 1 for red, -1 for blue

        if ((isRed && isNearBackboard && propPosition == Camera.PropPos.RIGHT) ||
                (!isRed && !isNearBackboard && propPosition == Camera.PropPos.LEFT)) {
            navBuilder = navBuilder.splineToLinearHeading(new Pose2d(xDelta + 24 + 0.0, // todo adjust live
                    startPose.position.y + allianceCoef * yDelta,
                    Math.toRadians(180)), Math.toRadians(180));
        } else {
            if (propPosition == Camera.PropPos.CENTER) {
                yDelta = -24 - cfg.ROBOT_LENGTH / 2.0 - 6;
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

        navBuilder = navBuilder.waitSeconds(0.1).afterTime(0.2, claw::ToggleClawLeft)
                .afterTime(0.4, () -> claw.clawRotator.setPosition(0.2)).waitSeconds(0.3);

        // Move to backboard
        double backboardX = 72 - 24 - 8.0; // todo adjust live
        double backboardY = allianceCoef * (-24 * 1.5);

        if (!isNearBackboard) {
            if (propPosition != Camera.PropPos.CENTER) {
                // Advance forward
                navBuilder = navBuilder.lineToY(allianceCoef * (-24 + 0.0)) // todo adjust live
                        .splineToLinearHeading(new Pose2d(-36, -12 * allianceCoef, Math.toRadians(0)), Math.toRadians(0));
            } else {
                // Move to the left / right
                navBuilder = navBuilder.lineToX(-72 + 24 / 2 + 6.0) // todo adjust live
                        .splineToLinearHeading(new Pose2d(-36, -12 * allianceCoef, Math.toRadians(0)), Math.toRadians(0));
            }
            navBuilder = navBuilder.lineToX(72 - 24 * 2)
                    .splineToLinearHeading(new Pose2d(backboardX, backboardY, Math.toRadians(180)), Math.toRadians(180));
        } else {
            // can just proceed to backboard
            navBuilder = navBuilder.lineToX(backboardX);
        }

        double apriltagDeltaY = 0;
        if (propPosition == Camera.PropPos.LEFT) apriltagDeltaY = -4.5;
        if (propPosition == Camera.PropPos.RIGHT) apriltagDeltaY = 4.5;

        navBuilder.lineToY(backboardY + apriltagDeltaY);
        // todo arm extension bullshit

        //////////////////////////////////////////////////////////
        Actions.runBlocking(navBuilder.build());
        //////////////////////////////////////////////////////////
    }
}
