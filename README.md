# CSP - Calculate Servo Position for FTC

Tired of endlessly guessing servo position values like `0.42` or `0.78`? **CSP** is a simple, plug-and-play utility for FTC Java programming that lets you control your servos using intuitive angles (like 90 degrees or 180 degrees) instead of arbitrary decimal values.

This system handles all the complex calculations, including mapping, scaling, and even reversing servo motion, so you can focus on building great robot logic.

## Why Use CSP?

*   **Intuitive Control:** Command your servos with real-world angles (`setAngle(90)`), not abstract positions (`setPosition(0.5)`).
*   **Rapid Prototyping:** Stop the trial-and-error. Define your servo's mechanical limits once, and the system handles the rest.
*   **Clean Configuration:** All your servo settings are in one clean, easy-to-read block at the top of your OpMode. No more "magic numbers" scattered in your code.
*   **Effortless Reversing:** Is your servo mounted backward? Just set its direction to `REVERSE`. No need to invert your logic or swap min/max values.
*   **Robust and Safe:** The system includes comprehensive error checking to catch configuration mistakes during initialization, saving you debugging time on the field.

## Installation

1.  **Download the API file:** Get the `CSP_Controller.java` file.
2.  **Place it in your project:** Create a `util` package inside your `TeamCode` module and place the file there. The final path should be:
    `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/util/CSP_Controller.java`
3.  **Use the Example OpMode:** Copy the `CSP_User_OpMode.java` file into your main `teamcode` directory to see a working example and use it as a template for your own OpModes.

## How to Use

Using CSP is a simple two-step process: **Configure** and **Control**.

### Step 1: Configure Your Servos

In your OpMode file (like `CSP_User_OpMode.java`), you'll find a configuration block at the top. This is the only place you need to define your servo settings.

Let's configure a robot with a **pan gimbal** and a **reversed gripper**.

```java
//==================== SERVO HARDWARE CONFIGURATION ====================
// 1. Servo Names (from your robot's config file)
private static final String[] SERVO_NAMES = {
        "gimbal_pan", "gripper"
};

// 2. Motion Direction (FORWARD or REVERSE)
private static final Direction[] DIRECTIONS = {
        Direction.FORWARD, Direction.REVERSE // Our gripper is mounted backward
};

// 3. Minimum Position (0-1), must be SMALLER than max
private static final double[] MIN_POSITIONS = {
        0.07, // gimbal_pan's leftmost position
        0.30  // gripper's most closed position
};

// 4. Maximum Position (0-1)
private static final double[] MAX_POSITIONS = {
        0.93, // gimbal_pan's rightmost position
        0.80  // gripper's most open position
};

// 5. The actual range of motion you want to use (in degrees)
private static final double[] REQUIRED_TRAVELS = {
        180.0, // We want to control the pan over a full 180 degrees
        90.0   // The gripper only needs 90 degrees to go from closed to open
};
//====================================================================
```

**Key Rules:**
*   All five arrays must have the **same number of elements**.
*   The order matters! The first element in each array corresponds to the first servo, the second to the second, and so on.
*   `MIN_POSITIONS` must always be numerically smaller than `MAX_POSITIONS`. The `REVERSE` flag handles the inversion for you.

### Step 2: Control Your Servos in the OpMode

1.  **Initialize the Controller:** In your `runOpMode()`, create an instance of the controller. It will automatically read your configuration.

    ```java
    private CSP_Controller cspController;

    @Override
    public void runOpMode() {
        // ...
        cspController = new CSP_Controller(
                hardwareMap,
                SERVO_NAMES,
                DIRECTIONS,
                MIN_POSITIONS,
                MAX_POSITIONS,
                REQUIRED_TRAVELS
        );
        // ...
        waitForStart();
        // ...
    }
    ```

2.  **Set Angles:** In your main loop, use the `setAngle()` command. It's that simple!

    ```java
    // ... in while(opModeIsActive()) ...

    // Control the pan servo with a gamepad stick
    double panStick = gamepad1.left_stick_x; // Value from -1.0 to 1.0
    double panAngle = 90.0 + (panStick * 90.0); // Map stick to 0-180 degrees
    cspController.setAngle("gimbal_pan", panAngle);

    // Control the gripper with buttons
    if (gamepad1.a) {
        // Set gripper to 90 degrees. Because it's REVERSE,
        // this will move it to its MIN_POSITION (0.30 - closed).
        cspController.setAngle("gripper", 90);
    } else if (gamepad1.b) {
        // Set gripper to 0 degrees. Because it's REVERSE,
        // this will move it to its MAX_POSITION (0.80 - open).
        cspController.setAngle("gripper", 0);
    }
    ```

## API Overview

The `CSP_Controller` class provides two main methods for control:

*   `void setAngle(String servoName, double angle)`
    Calculates the correct position and immediately commands the physical servo to move.

*   `double calculatePosition(String servoName, double angle)`
    Calculates and returns the position value (0.0-1.0) without moving the servo. This is useful for telemetry and debugging.

## License

This project is licensed under the MIT License. Feel free to use, modify, and distribute it in your own FTC projects. Good luck this season!
