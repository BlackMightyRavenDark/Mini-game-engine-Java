package com.engine.input;

import org.lwjgl.BufferUtils;
import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;

public class Mouse {
    public static final int MOUSE_BUTTON_LEFT = GLFW_MOUSE_BUTTON_LEFT;
    public static final int MOUSE_BUTTON_RIGHT = GLFW_MOUSE_BUTTON_RIGHT;
    public static final int MOUSE_BUTTON_MIDDLE = GLFW_MOUSE_BUTTON_MIDDLE;
    public static final int MOUSE_EVENT_PRESS = GLFW_PRESS;
    public static final int MOUSE_EVENT_RELEASE = GLFW_RELEASE;
    public static final int MOUSE_EVENT_MOVE = 1000;

    public static boolean hasButtonEvent = false;
    public static boolean hasMovingEvent = false;
    public static long windowId = 0;
    public static int eventAction;
    public static int eventButton;
    public static int eventMods;

    public static int deltaX;
    public static int deltaY;

    private static final DoubleBuffer positionX;
    private static final DoubleBuffer positionY;

    static {
        positionX = BufferUtils.createDoubleBuffer(1);
        positionY = BufferUtils.createDoubleBuffer(1);
    }

    public static boolean nextEvent() {
        if (Mouse.windowId == 0) {
            Mouse.hasButtonEvent = false;
            Mouse.hasMovingEvent = false;
            return false;
        }
        if (Mouse.hasButtonEvent) {
            Mouse.hasButtonEvent = false;
            return true;
        }
        if (Mouse.hasMovingEvent) {
            Mouse.hasMovingEvent = false;
            Mouse.eventAction = Mouse.MOUSE_EVENT_MOVE;
            return true;
        }
        return false;
    }

    public static int getDeltaX() {
        if (Mouse.windowId == 0) {
            return 0;
        }
        int res = Mouse.deltaX;
        Mouse.deltaX = 0;
        return res;
    }

    public static int getDeltaY() {
        if (Mouse.windowId == 0) {
            return 0;
        }
        int res = Mouse.deltaY;
        Mouse.deltaY = 0;
        return res;
    }

    public static void setPosition(double x, double y) {
        if (Mouse.windowId != 0) {
            glfwSetCursorPos(Mouse.windowId, x, y);
            Mouse.deltaX = (int)(Mouse.getPositionX() - x);
            Mouse.deltaY = (int)(Mouse.getPositionY() - y);
        }
    }

    public static boolean setGrabbed(boolean grabbed) {
        if (Mouse.windowId == 0) {
            return false;
        }
        glfwSetInputMode(Mouse.windowId, GLFW_CURSOR, grabbed ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL);
        Mouse.deltaX = 0;
        Mouse.deltaY = 0;
        return true;
    }

    public static boolean isGrabbed() {
        return Mouse.windowId != 0 && glfwGetInputMode(Mouse.windowId, GLFW_CURSOR) == GLFW_CURSOR_DISABLED;
    }

    public static boolean isButtonDown(int button) {
        return Mouse.windowId != 0 && glfwGetMouseButton(Mouse.windowId, button) == Mouse.MOUSE_EVENT_PRESS;
    }

    public static void updatePosition() {
        if (Mouse.windowId != 0) {
            glfwGetCursorPos(Mouse.windowId, Mouse.positionX, Mouse.positionY);
        }
    }

    public static double getPositionX() {
        return Mouse.positionX.get(0);
    }

    public static double getPositionY() {
        return Mouse.positionY.get(0);
    }
}
