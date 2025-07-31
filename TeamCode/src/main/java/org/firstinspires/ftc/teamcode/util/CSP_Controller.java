package org.firstinspires.ftc.teamcode.util;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import java.util.HashMap;
import java.util.Map;

public class CSP_Controller {

    public enum Direction {
        FORWARD,
        REVERSE
    }

    private static class ServoData {
        final Servo servoHardware;
        final Direction direction;
        final double minPosition;
        final double maxPosition;
        final double requiredTravel;

        ServoData(Servo servoHardware, Direction direction, double minPosition, double maxPosition, double requiredTravel) {
            this.servoHardware = servoHardware;
            this.direction = direction;
            this.minPosition = minPosition;
            this.maxPosition = maxPosition;
            this.requiredTravel = requiredTravel;
        }
    }

    private final Map<String, ServoData> servoMap = new HashMap<>();

    public CSP_Controller(HardwareMap hardwareMap, String[] servoNames, Direction[] directions,
                          double[] minPositions, double[] maxPositions, double[] requiredTravels) {
        
        int configLength = servoNames.length;
        if (directions.length != configLength || minPositions.length != configLength ||
                maxPositions.length != configLength || requiredTravels.length != configLength) {
            throw new IllegalArgumentException("CSP Configuration Error: All configuration arrays must have the same length.");
        }

        for (int i = 0; i < configLength; i++) {
            String name = servoNames[i];

            if (minPositions[i] >= maxPositions[i]) {
                throw new IllegalArgumentException(String.format("CSP Servo '%s' Config Error: minPosition (%.2f) must be strictly less than maxPosition (%.2f). Use 'REVERSE' direction for inverse motion.",
                        name, minPositions[i], maxPositions[i]));
            }
            if (requiredTravels[i] <= 0) {
                 throw new IllegalArgumentException(String.format("CSP Servo '%s' Config Error: requiredTravel must be a positive number.", name));
            }
            if (servoMap.containsKey(name)) {
                throw new IllegalArgumentException(String.format("CSP Servo '%s' Config Error: Duplicate servo name found.", name));
            }

            Servo servo = hardwareMap.get(Servo.class, name);
            ServoData data = new ServoData(servo, directions[i], minPositions[i], maxPositions[i], requiredTravels[i]);
            servoMap.put(name, data);
        }
    }

    public void setAngle(String servoName, double angle) {
        double position = calculatePosition(servoName, angle);
        servoMap.get(servoName).servoHardware.setPosition(position);
    }

    public double calculatePosition(String servoName, double angle) {
        ServoData data = servoMap.get(servoName);
        if (data == null) {
            throw new IllegalArgumentException(String.format("CSP Error: No servo named '%s' was found.", servoName));
        }

        double normalizedAngle = angle % 360;
        if (normalizedAngle < 0) {
            normalizedAngle += 360;
        }

        if (normalizedAngle < 0 || normalizedAngle > data.requiredTravel) {
            throw new IllegalArgumentException(String.format("Angle %.1f (normalized to %.1f) is outside the configured travel range (0-%.1f degrees) for servo '%s'",
                    angle, normalizedAngle, data.requiredTravel, servoName));
        }

        double angleRatio = normalizedAngle / data.requiredTravel;

        if (data.direction == Direction.REVERSE) {
            angleRatio = 1.0 - angleRatio;
        }

        double effectiveRange = data.maxPosition - data.minPosition;
        double position = data.minPosition + (angleRatio * effectiveRange);

        return Math.max(0.0, Math.min(1.0, position));
    }
}
