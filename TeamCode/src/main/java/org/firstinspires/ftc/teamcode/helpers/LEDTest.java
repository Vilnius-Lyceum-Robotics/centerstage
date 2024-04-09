package org.firstinspires.ftc.teamcode.helpers;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DigitalChannel;

@TeleOp
public class LEDTest extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        DigitalChannel redLED = hardwareMap.get(DigitalChannel.class, "redLED");
        DigitalChannel greenLED = hardwareMap.get(DigitalChannel.class, "greenLED");

        redLED.setMode(DigitalChannel.Mode.OUTPUT);
        greenLED.setMode(DigitalChannel.Mode.OUTPUT);

        waitForStart();

        while (opModeIsActive()) {
            redLED.setState(true);
            greenLED.setState(false);
            sleep(1000);
            redLED.setState(false);
            greenLED.setState(true);
            sleep(1000);
            redLED.setState(true);
            greenLED.setState(true);
            sleep(1000);
            redLED.setState(false);
            greenLED.setState(false);
            sleep(1000);
        }
    }
}
