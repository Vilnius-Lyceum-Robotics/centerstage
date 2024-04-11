package org.firstinspires.ftc.teamcode.helpers;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DigitalChannel;

@TeleOp
public class LEDTest extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        DigitalChannel leftRedLED = hardwareMap.get(DigitalChannel.class, "redLeft");
        DigitalChannel leftGreenLED = hardwareMap.get(DigitalChannel.class, "greenLeft");
        DigitalChannel rightGreenLED = hardwareMap.get(DigitalChannel.class, "greenRight");
        DigitalChannel rightRedLED = hardwareMap.get(DigitalChannel.class, "redRight");

        leftGreenLED.setMode(DigitalChannel.Mode.OUTPUT);
        leftRedLED.setMode(DigitalChannel.Mode.OUTPUT);

        rightRedLED.setMode(DigitalChannel.Mode.OUTPUT);
        rightGreenLED.setMode(DigitalChannel.Mode.OUTPUT);
        //prev
        // 0 - redLeft
        // 1 - greenLeft
        // 2 - redRight
        // 3 - greenRight

        // right and flipped
        leftGreenLED.setState(true);
        leftRedLED.setState(false);
        // left and flipped
        rightGreenLED.setState(true);
        rightRedLED.setState(false);

        waitForStart();

        while (opModeIsActive()) {
//            leftRedLED.setState(true);
//            rightGreenLED.setState(false);
//            sleep(1000);
//            leftRedLED.setState(false);
//            rightGreenLED.setState(true);
//            sleep(1000);
//            leftRedLED.setState(true);
//            rightGreenLED.setState(true);
//            sleep(1000);
//            leftRedLED.setState(false);
//            rightGreenLED.setState(false);
//            sleep(1000);
//            leftRedLED.setState(true);

        }
    }
}
