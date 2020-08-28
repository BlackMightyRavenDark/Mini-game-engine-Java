package com;

import com.engine.input.*;
import com.engine.Camera;

public class CameraController {
    public Camera camera;
    public float flyingSpeed = 10.0f;
    private final float mouseSensitivity = 0.1f;

    public CameraController(Camera camera) {
        this.camera = camera;
    }

    private void handleMouseMovement() {
        camera.rotationYaw   += Mouse.getDeltaX() * mouseSensitivity;
        camera.rotationPitch += Mouse.getDeltaY() * mouseSensitivity;
        while (camera.rotationYaw >= 360.0f) {
            camera.rotationYaw -= 360.0f;
        }
        while (camera.rotationYaw < 0.0f) {
            camera.rotationYaw += 360.0f;
        }
        if (camera.rotationPitch > camera.maxPitch) {
            camera.rotationPitch = camera.maxPitch;
        } else if (camera.rotationPitch < -camera.maxPitch) {
            camera.rotationPitch = -camera.maxPitch;
        }
        camera.calculateFront();
    }

    private void handleMovement(float speed) {
        speed *= (float)Main.getMainTimer().getDeltaTime();

        if (Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_UP) || Keyboard.isKeyDown(Keyboard.KEY_KP_8)) {
            camera.moveRelative(camera.getFront().mul(speed));
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_DOWN) || Keyboard.isKeyDown(Keyboard.KEY_KP_2)) {
            camera.moveRelative(camera.getFront().negate().mul(speed));
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_LEFT) || Keyboard.isKeyDown(Keyboard.KEY_KP_4)) {
            camera.moveRelative(camera.getFront().cross(Camera.upVector).normalize().negate().mul(speed));
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_RIGHT) || Keyboard.isKeyDown(Keyboard.KEY_KP_6)) {
            camera.moveRelative(camera.getFront().cross(Camera.upVector).normalize().mul(speed));
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
            camera.moveRelative(0.0f, -speed, 0.0f);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            camera.moveRelative(0.0f, speed, 0.0f);
        }
    }

    public void tick() {
        if (Mouse.isGrabbed()) {
            handleMouseMovement();
        }
        handleMovement(flyingSpeed);
    }

}
