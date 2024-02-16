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

        double xDelta, yDelta;

        int allianceCoef = isRed ? 1 : -1; // 1 for red, -1 for blue

        boolean isLeft = propPosition == Camera.PropPos.LEFT;

        double angle = 0; // Angle the robot should be facing to hit the prop
        if (isLeft && isRed) angle = Math.toRadians(180);
        if (!isLeft && !isRed) angle = Math.toRadians(180);

        if (propPosition == Camera.PropPos.CENTER) {
            // robot length = 15.9
            yDelta = -24 - cfg.ROBOT_LENGTH / 2.0 - 6; // -37.95
            navBuilder = navBuilder.lineToY(allianceCoef * yDelta);
            //           BACKBOARD
            //  (12, 37.95)     (12, -37.95)
            //  (-36, 37.95)    (-36, -37.95)

        } else {
            // Left or right
            //boolean isLeft = propPosition == Camera.PropPos.LEFT;

            yDelta = (24 * 2 - cfg.ROBOT_LENGTH / 2) - 8; // Position of prop relative to start point (32.05)
            xDelta = (-12 + cfg.ROBOT_WIDTH / 2) + 4; // 0.45
            xDelta = isLeft ? xDelta : -xDelta;
            telemetry.addData("MAIN", "xDelta: " + xDelta);
            telemetry.update();

            //double angle = 0; // Angle the robot should be facing to hit the prop
            //if (isLeft && isRed) angle = Math.toRadians(180);
            //if (!isLeft && !isRed) angle = Math.toRadians(180);

            // Determine prop position on field plane
            Pose2d placePos = new Pose2d(startPose.position.x + xDelta,
                    startPose.position.y + allianceCoef * yDelta, angle);
            //          BACKBOARD
            // !isLeft              !isLeft
            // (11.55, 32, (180))   (11.55, -32, (0))
            // isLeft               isLeft
            // (12,45, 32, (0))     (12.45, -32, (180))
            //
            // !isLeft              !isLeft
            // (-36.45, 32, (180))  (-36.45, -32 (0))
            // isLeft               isLeft
            // (-35.55, 32, (0))    (-35.55, -32 (180))

            // Move up to not hit the pillar on the left / right while turning
            navBuilder = navBuilder.lineToY(startPose.position.y + allianceCoef * (yDelta / 1.5))
                    // and go to the prop
                    .splineToLinearHeading(placePos, angle);

        }

        navBuilder = navBuilder.waitSeconds(0.1).afterTime(0.2, claw::ToggleClawLeft)
                .afterTime(0.4, () -> claw.clawRotator.setPosition(0.2))
                .waitSeconds(0.9);

        yDelta = startPose.position.y/2;

        if (isNearBackboard){
            double backboard = (propPosition != Camera.PropPos.CENTER) ? 1: -1;
            //WORKS CORRECTLY
            if (propPosition != Camera.PropPos.CENTER) {
                navBuilder = navBuilder.lineToY(startPose.position.y - 12);
            } else {
                navBuilder = navBuilder.lineToY((yDelta + 12))
                        .lineToX(startPose.position.x + 12);
            }
            navBuilder = navBuilder.splineToLinearHeading(
                    new Pose2d(12 * 4, startPose.position.y + backboard * yDelta, Math.toRadians(180)), Math.toRadians(0));
        }
        else {
            if (propPosition != Camera.PropPos.CENTER) {
                navBuilder = navBuilder.lineToY(startPose.position.y +  yDelta)
                        .splineToLinearHeading(new Pose2d(-12,  (-12)*allianceCoef, Math.toRadians(angle)), Math.toRadians(0)); // Math.toRadians should be the same as the variable angle

            } else {
                navBuilder = navBuilder.lineToX(startPose.position.x - 12*allianceCoef)
                        .splineToLinearHeading(new Pose2d(-12 * 4, yDelta, Math.toRadians(180)), Math.toRadians(0))
                ;
            }
            navBuilder = navBuilder.lineToX(24)
                    .splineToLinearHeading(new Pose2d(startPose.position.x * (-1.3), startPose.position.y -  yDelta, Math.toRadians(180)), Math.toRadians(0));
        }
        //////////////////////////////////////////////////////////
        Actions.runBlocking(navBuilder.build());
        //////////////////////////////////////////////////////////
    }
}
