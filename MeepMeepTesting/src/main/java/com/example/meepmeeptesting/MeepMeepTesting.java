package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTesting {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(768);

        double backboardX = 72 - 24 - 0.0; // todo adjust live
        double backboardY = 1 * (-24 * 1.5);
        double ROBOT_LENGTH = 15.9;
        double ROBOT_WIDTH = 16.9;

        boolean isLeft = false;

        double xDelta = (-12 + ROBOT_WIDTH / 2) + 4;

        double yDelta = (24 * 2 - ROBOT_LENGTH / 2) - 8; // Position of prop relative to start point

        int allianceCoef = 1;


        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(50, 50, Math.toRadians(180), Math.toRadians(180), 13.77)
                .setDimensions(16.92, 15.74)
                .followTrajectorySequence(drive ->
                        drive.trajectorySequenceBuilder(new Pose2d(12, -72 + ROBOT_LENGTH / 2.0, Math.toRadians(90)))
                                .splineToLinearHeading(new Pose2d(-xDelta + 24,
                                        -72 + ROBOT_LENGTH / 2.0 + allianceCoef * yDelta,
                                        Math.toRadians(180)), Math.toRadians(180))
                                .build()
                );


        meepMeep.setBackground(MeepMeep.Background.FIELD_CENTERSTAGE_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}