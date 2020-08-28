package com.engine;

import com.engine.input.Keyboard;
import com.engine.input.Mouse;
import org.lwjgl.glfw.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class GameWindow {
    public static int WINDOW_STATE_MAXIMIZED = GLFW_MAXIMIZED;
    public static int WINDOW_STATE_MINIMIZED = GLFW_ICONIFIED;
    private boolean focusChanged = false;
    private long id;
    private int width;
    private int height;
    private final String title;

    private boolean wasResized = true;

    public GameWindow(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;
    }

    public long create() {
        if (!glfwInit()) {
            System.out.println("glfwInit() failed!");
            System.exit(1);
        }

        id = glfwCreateWindow(width, height, title, 0, 0);
        if (id == 0) {
            System.out.println("glfwCreateWindow() failed!");
            System.exit(1);
        }

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_FALSE);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        setCallbacks();

        return id;
    }

    public void destroy() {
        if (id != 0) {
            glfwDestroyWindow(id);
        }
    }

    public void makeCurrent() {
        glfwMakeContextCurrent(id);
    }

    public void close() {
        glfwSetWindowShouldClose(id, true);
    }

    public static void pollEvents() {
        glfwPollEvents();
    }

    public void swapBuffers() {
        glfwSwapBuffers(id);
    }

    public boolean isShouldClose() {
        return glfwWindowShouldClose(id);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getId() {
        return id;
    }

    public void setViewPort() {
        glViewport(0, 0, width, height);
    }

    public boolean isWasResized() {
        if (wasResized) {
            wasResized = false;
            return true;
        }
        return false;
    }

    public boolean isFocusChanged() {
        if (focusChanged) {
            focusChanged = false;
            return true;
        }
        return false;
    }

    public boolean isMinimized() {
        return id != 0 && glfwGetWindowAttrib(id, GameWindow.WINDOW_STATE_MINIMIZED) == GameWindow.WINDOW_STATE_MINIMIZED;
    }

    public boolean isMaximized() {
        return id != 0 && glfwGetWindowAttrib(id, GameWindow.WINDOW_STATE_MAXIMIZED) == GameWindow.WINDOW_STATE_MAXIMIZED;
    }

    public boolean isFocused() {
        return id != 0 && glfwGetWindowAttrib(id, GLFW_FOCUSED) != 0;
    }

    private void setCallbacks() {
        glfwSetWindowSizeCallback(id, new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long windowId, int newWidth, int newHeight) {
                width = newWidth;
                height = newHeight;
                wasResized = true;
            }
        });
        glfwSetWindowFocusCallback(id, new GLFWWindowFocusCallback() {
            @Override
            public void invoke(long windowId, boolean focused) {
                focusChanged = true;
                if (focused) {
                    Mouse.windowId = windowId;
                    Keyboard.windowId = windowId;
                } else {
                    Mouse.hasButtonEvent = false;
                    Mouse.hasMovingEvent = false;
                    Keyboard.hasEvent = false;
                    Keyboard.windowId = 0;
                }
            }
        });
        glfwSetKeyCallback(id, new GLFWKeyCallback() {
            @Override
            public void invoke(long windowId, int key, int scancode, int action, int mods) {
                Keyboard.windowId = windowId;
                Keyboard.eventKey = key;
                Keyboard.eventScancode = scancode;
                Keyboard.eventAction = action;
                Keyboard.eventMods = mods;
                Keyboard.hasEvent = true;
            }
        });
        glfwSetMouseButtonCallback(id, new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long windowId, int button, int action, int mods) {
                Mouse.windowId = windowId;
                Mouse.eventAction = action;
                Mouse.eventButton = button;
                Mouse.eventMods = mods;
                Mouse.hasButtonEvent = true;
            }
        });
        glfwSetCursorPosCallback(id, new GLFWCursorPosCallback() {
            @Override
            public void invoke(long windowId, double x, double y) {
                Mouse.windowId = windowId;
                Mouse.deltaX = (int)(x - Mouse.getPositionX());
                Mouse.deltaY = (int)(y - Mouse.getPositionY());
                Mouse.updatePosition();
                Mouse.hasMovingEvent = true;
            }
        });
    }

}
