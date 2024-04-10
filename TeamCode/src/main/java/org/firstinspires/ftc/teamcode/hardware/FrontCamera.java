package org.firstinspires.ftc.teamcode.hardware;


import static android.os.SystemClock.sleep;

import android.util.Size;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagLibrary;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.tfod.TfodProcessor;

import java.util.ArrayList;
import java.util.List;

@Config
public class FrontCamera {
    AprilTagProcessor aprilTagProcessor;
    TfodProcessor tfodProcessor;
    VisionPortal visionPortal;

    public List<AprilTagDetection> aprilTagDetections = new ArrayList<>();

    public static PropPos defaultPosition = PropPos.NONE;

    public enum PropPos {
        LEFT,
        CENTER,
        RIGHT,
        NONE
    }

    public boolean hasTeamProp = false;
    public PropPos teamPropPos = defaultPosition;

    public double propAng = 0;

    public FrontCamera(HardwareMap hardwareMap, boolean alliance) {
        WebcamName webcamName = hardwareMap.get(WebcamName.class, "WebcamFront");
        AprilTagLibrary tagLibrary = new AprilTagLibrary.Builder()
                .addTag(1, "Blue Alliance Left", 5.08, DistanceUnit.CM)
                .addTag(2, "Blue Alliance Center", 5.08, DistanceUnit.CM)
                .addTag(3, "Blue Alliance Right", 5.08, DistanceUnit.CM)
                .addTag(4, "Red Alliance Left", 5.08, DistanceUnit.CM)
                .addTag(5, "Red Alliance Center", 5.08, DistanceUnit.CM)
                .addTag(6, "Red Alliance Right", 5.08, DistanceUnit.CM)
                .build();

        aprilTagProcessor = new AprilTagProcessor.Builder()
                .setTagLibrary(tagLibrary)
                .setDrawAxes(true)
                .setDrawCubeProjection(true)
                .setLensIntrinsics(1493.422812, 1493.422812, 983.366398242, 553.266679092)
                .build();
        if (!alliance) {
            final String[] LABELS = {
                    "blueprop",
            };
            tfodProcessor = new TfodProcessor.Builder()
                    .setMaxNumRecognitions(1)
                    .setUseObjectTracker(true)
                    .setTrackerMaxOverlap((float) 0.2)
                    .setTrackerMinSize(16)
                    .setModelAssetName("blue.tflite")
                    .setModelLabels(LABELS)
                    .setTrackerMinCorrelation(0.5f)
                    .setTrackerMarginalCorrelation(0.6f)
                    .build();
        } else {
            final String[] LABELS = {
                    "redprop",
            };
            tfodProcessor = new TfodProcessor.Builder()
                    .setMaxNumRecognitions(1)
                    .setUseObjectTracker(true)
                    .setTrackerMaxOverlap((float) 0.2)
                    .setTrackerMinSize(16)
                    .setModelAssetName("red.tflite")
                    .setModelLabels(LABELS)
                    .build();
        }


        visionPortal = new VisionPortal.Builder()
                .setCamera(webcamName)
                .addProcessor(aprilTagProcessor)
                .addProcessor(tfodProcessor)
                .setCameraResolution(new Size(1920, 1080))
                .setStreamFormat(VisionPortal.StreamFormat.MJPEG)
                .enableLiveView(true)
                .setAutoStopLiveView(true)
                .build();

        visionPortal.resumeStreaming();
    }

    public void process() {
//        Y axis points straight outward from the camera lens center
//
//        X axis points to the right (looking outward), perpendicular to the Y axis
//
//        Z axis points upward, perpendicular to Y and X

        aprilTagDetections = aprilTagProcessor.getDetections();

        for (AprilTagDetection detection : aprilTagDetections) {
            if (detection.metadata != null) {
                System.out.println("Tag ID: " + detection.id);
                System.out.println("Tag Name: " + detection.metadata.name);

                System.out.println("Tag Range (cm): " + detection.ftcPose.range);
                System.out.println("Tag Bearing (deg): " + detection.ftcPose.bearing);
            }
        }
        List<Recognition> recognitions = tfodProcessor.getRecognitions();
        for (Recognition recognition : recognitions) {
            if (recognition.getLabel().equals("blueprop") || recognition.getLabel().equals("redprop")) {
                hasTeamProp = true;

                //System.out.println("DETECTED");
                double f = recognition.getLeft();
                //System.out.println(f);
                // left iki 450
                // center nuo 450 iki 1250
                // right po 1250
                if (f < 450) teamPropPos = PropPos.LEFT;
                else if (f < 1250) teamPropPos = PropPos.CENTER;
                else teamPropPos = PropPos.RIGHT;
                propAng = f;
                //System.out.println(f);
                //System.out.println(teamPropPos);
            }
        }
        // https://ftc-docs.firstinspires.org/en/latest/ftc_ml/index.html
        // https://ftc-docs.firstinspires.org/en/latest/ftc_ml/managing_tool/create_videos/create-videos.html
    }

    public void process(int times) {
        for (int i = 0; i < times; i++) {
            process();
        }
    }

    public void process(int times, int sleepMs) {
        for (int i = 0; i < times; i++) {
            process();
            sleep(sleepMs);
        }
    }

    public void processUntilDetection() {
        while (teamPropPos != PropPos.NONE) {
            process();
        }
    }

    public void close() {
        visionPortal.close();
    }
}
