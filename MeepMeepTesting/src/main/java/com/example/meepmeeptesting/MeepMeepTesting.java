package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;


public class MeepMeepTesting {

    public static void main(String[] args) {

        //  (12, 37.95)     (12, -37.95)
        //  (-36, 37.95)    (-36, -37.95)

        MeepMeep meepMeep = new MeepMeep(768);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(50, 50, Math.toRadians(180), Math.toRadians(180), 13.77)
                .setDimensions(16.92, 15.74)

                .followTrajectorySequence(drive ->
                                drive.trajectorySequenceBuilder(new Pose2d(-36, 32, Math.toRadians(-90)))
                                        .splineToLinearHeading(
                                new Pose2d(-36 + 12,  32 - 24 , Math.toRadians(0)), Math.toRadians(0))
                                        .splineToLinearHeading(
                        new Pose2d(12 + 36, 64.05 - 15.9 - 12, Math.toRadians(180)), Math.toRadians(90))
                        .build());



        meepMeep.setBackground(MeepMeep.Background.FIELD_CENTERSTAGE_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}