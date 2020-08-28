package com.engine;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.joml.Math.toRadians;

public class Camera {
    private final Vector3f position;
    public float rotationYaw;
    public float rotationPitch;
    public float rotationRoll;
    private final Vector3f front;
    private float zNear = 0.1f;
    private float zFar = 1000.0f;

    public static final Vector3f upVector = new Vector3f(0.0f, 1.0f, 0.0f);

    public final float maxPitch = 89.9f;

    public Camera(Vector3f position, float rotationYaw, float rotationPitch, float rotationRoll) {
        this.position = position;
        this.rotationYaw = rotationYaw;
        this.rotationPitch = rotationPitch;
        this.rotationRoll = rotationRoll;
        front = new Vector3f();
        calculateFront();
    }

    public void calculateFront() {
        front.x = (float)(Math.cos(toRadians(rotationYaw - 90.0f)) * Math.cos(toRadians(rotationPitch)));
        front.y = (float)-Math.sin(toRadians(rotationPitch));
        front.z = (float)(Math.sin(toRadians(rotationYaw - 90.0f)) * Math.cos(toRadians(rotationPitch)));
        front.normalize();
    }

    public Matrix4f getProjectionMatrix(float width, float height) {
        return new Matrix4f().perspective(45.0f, width / height, zNear, zFar);
    }

    public Matrix4f getViewMatrix() {
        return new Matrix4f().lookAt(position, new Vector3f(position).add(front), Camera.upVector);
    }

    public void moveRelative(float x, float y, float z) {
        position.add(x, y, z);
    }

    public void moveRelative(Vector3f vector) {
        position.add(vector);
    }

    public void reset() {
        position.x = 0.0f;
        position.y = 0.0f;
        position.z = 4.0f;
        rotationYaw = 0.0f;
        rotationPitch = 0.0f;
        rotationRoll = 0.0f;
        front.x = 0.0f;
        front.y = 0.0f;
        front.z = -1.0f;
    }

    public Vector3f getPosition() {
        return new Vector3f(position);
    }

    public Vector3f getFront() {
        return new Vector3f(front);
    }

    public float getZNear() {
        return zNear;
    }

    public void setZNear(float zNear) {
        this.zNear = zNear;
    }

    public float getZFar() {
        return zFar;
    }

    public void setZFar(float zFar) {
        this.zFar = zFar;
    }
}
