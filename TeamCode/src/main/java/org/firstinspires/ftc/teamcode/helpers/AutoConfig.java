package org.firstinspires.ftc.teamcode.helpers;

import com.acmerobotics.roadrunner.Pose2d;

public class AutoConfig {
    private final Pose2d RED_NEAR_BACKBOARD;
    private final Pose2d RED_NEAR_AUDIENCE;
    private final Pose2d BLUE_NEAR_BACKBOARD;
    private final Pose2d BLUE_NEAR_AUDIENCE;

    public AutoConfig() {
        RED_NEAR_BACKBOARD = new Pose2d(Constants.PLATE_DIMENSION / 2.0, ( -Constants.FIELD_DIMENSION + Constants.ROBOT_LENGTH ) / 2.0, Math.toRadians(90));
        RED_NEAR_AUDIENCE = new Pose2d(-(Constants.PLATE_DIMENSION / 2.0 * 3.0), ( -Constants.FIELD_DIMENSION + Constants.ROBOT_LENGTH ) / 2.0, Math.toRadians(90));
        BLUE_NEAR_BACKBOARD = new Pose2d(Constants.PLATE_DIMENSION / 2.0,  ( Constants.FIELD_DIMENSION - Constants.ROBOT_LENGTH ) / 2.0, Math.toRadians(-90));
        BLUE_NEAR_AUDIENCE = new Pose2d(-(Constants.PLATE_DIMENSION / 2.0 * 3.0),  ( Constants.FIELD_DIMENSION - Constants.ROBOT_LENGTH ) / 2.0, Math.toRadians(-90));
    }

    public Pose2d getStartPos(boolean isRed, boolean isNearBackboard) {
        if (isRed) {
            return isNearBackboard ? RED_NEAR_BACKBOARD : RED_NEAR_AUDIENCE;
        } else {
            return isNearBackboard ? BLUE_NEAR_BACKBOARD : BLUE_NEAR_AUDIENCE;
        }
    }
}
