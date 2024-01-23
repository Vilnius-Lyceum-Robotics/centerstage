package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
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

        double xDelta, yDelta;

        int allianceCoef = isRed ? 1 : -1; // 1 for red, -1 for blue

        if (propPosition == Camera.PropPos.CENTER) {
            yDelta = -24 - cfg.ROBOT_LENGTH / 2.0 - 6;
            navBuilder = navBuilder.lineToY(allianceCoef * yDelta);
        } else {
            // Left or right
            boolean isLeft = propPosition == Camera.PropPos.LEFT;

            yDelta = (24 * 2 - cfg.ROBOT_LENGTH / 2) - 8; // Position of prop relative to start point
            xDelta = (-12 + cfg.ROBOT_WIDTH / 2) + 4;
            xDelta = isLeft ? xDelta : -xDelta;
            telemetry.addData("MAIN", "xDelta: " + xDelta);
            telemetry.update();

            double angle = 0; // Angle the robot should be facing to hit the prop
            if (isLeft && isRed) angle = Math.toRadians(180);
            if (!isLeft && !isRed) angle = Math.toRadians(180);

            // Determine prop position on field plane
            Pose2d placePos = new Pose2d(startPose.position.x + xDelta,
                    startPose.position.y + allianceCoef * yDelta, angle);

            // Move up to not hit the pillar on the left / right while turning
            navBuilder = navBuilder.lineToY(startPose.position.y + allianceCoef * (yDelta / 1.5))
                    // and go to the prop
                    .splineToLinearHeading(placePos, angle);
        }

        navBuilder = navBuilder.waitSeconds(0.1).afterTime(0.2, claw::ToggleClawLeft)
                .afterTime(0.4, () -> claw.clawRotator.setPosition(0.2))
                .waitSeconds(0.9);

        //////////////////////////////////////////////////////////
        Actions.runBlocking(navBuilder.build());
        //////////////////////////////////////////////////////////
    }
}
