package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.util.CSP_Controller;
import static org.firstinspires.ftc.teamcode.util.CSP_Controller.Direction;

@TeleOp(name = "CSP - Servo Control Demo", group = "CSP")
public class CSP_User_OpMode extends LinearOpMode {

    //==================== SERVO HARDWARE CONFIGURATION ====================
    private static final String[] SERVO_NAMES = {
            "gimbal_pan", "gimbal_tilt", "gripper"
    };

    private static final Direction[] DIRECTIONS = {
            Direction.FORWARD, Direction.FORWARD, Direction.REVERSE
    };
    
    private static final double[] MIN_POSITIONS = {
            0.07, 0.20, 0.30
    };

    private static final double[] MAX_POSITIONS = {
            0.93, 0.85, 0.80
    };

    private static final double[] REQUIRED_TRAVELS = {
            180.0, 90.0, 90.0
    };
    //====================================================================

    private CSP_Controller cspController;

    @Override
    public void runOpMode() {

        try {
            cspController = new CSP_Controller(
                    hardwareMap,
                    SERVO_NAMES,
                    DIRECTIONS,
                    MIN_POSITIONS,
                    MAX_POSITIONS,
                    REQUIRED_TRAVELS
            );
        } catch (Exception e) {
            telemetry.addData("ERROR", "CSP Initialization Failed!");
            telemetry.addLine(e.getMessage());
            telemetry.update();
            while (!isStopRequested()) { sleep(100); }
            return;
        }
        
        telemetry.addData("Status", "CSP Initialized. Press START to run.");
        telemetry.update();
        
        waitForStart();

        while (opModeIsActive()) {
            double panStick = gamepad1.left_stick_x;
            double panAngle = 90.0 + (panStick * 90.0);
            cspController.setAngle("gimbal_pan", panAngle);

            if (gamepad1.a) {
                cspController.setAngle("gripper", 0);
            } else if (gamepad1.b) {
                cspController.setAngle("gripper", 90);
            }
            
            telemetry.addData("Gimbal Pan Target Angle", "%.2f", panAngle);
            telemetry.addData("Gimbal Pan Calculated Position", "%.3f", cspController.calculatePosition("gimbal_pan", panAngle));
            telemetry.addLine("--------------------");
            telemetry.addData("Controls", "Press A to open gripper, B to close.");
            telemetry.update();
        }
    }
}
