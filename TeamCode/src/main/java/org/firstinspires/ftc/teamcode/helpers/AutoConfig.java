package org.firstinspires.ftc.teamcode.helpers;

import com.acmerobotics.roadrunner.Pose2d;

import java.util.Dictionary;
import java.util.Hashtable;

public class AutoConfig {
    public double ROBOT_LENGTH = 15.9;
    public double ROBOT_WIDTH = 16.9;


    public Dictionary<Integer, Pose2d> startPositions = new Hashtable<>();
    //  BACKBOARD
    // 0          1
    // 2          3
    //  AUDIENCE

    public AutoConfig() {
        startPositions.put(0, new Pose2d(12, 72 - ROBOT_LENGTH / 2.0, Math.toRadians(-90)));
        startPositions.put(2, new Pose2d(-36, 72 - ROBOT_LENGTH / 2.0, Math.toRadians(-90)));

        startPositions.put(3, new Pose2d(-36, -72 + ROBOT_LENGTH / 2.0, Math.toRadians(90)));
        startPositions.put(1, new Pose2d(12, -72 + ROBOT_LENGTH / 2.0, Math.toRadians(90)));
    }

    public Pose2d getStartPos(boolean isRed, boolean isNearBackboard) {
        if (!isRed) {
            if (isNearBackboard) {
                return startPositions.get(0);
            } else {
                return startPositions.get(2);
            }
        } else {
            if (isNearBackboard) {
                return startPositions.get(1);
            } else {
                return startPositions.get(3);
            }
        }
    }
}
