package com;

import com.engine.*;
import com.engine.input.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Main {

    private static Timer timerMain;

    private final ShaderProgram shaderProgramTexture;
    private final FontRenderer font;

    private final GameWindow window;
    private final CameraController controller;

    private int sceneFps = 0;

    public Main() {

        (window = new GameWindow(640, 480, "Mini game engine | Version 0.0.1-alpha | OpenGL 3.3")).create();
        window.makeCurrent();
        GL.createCapabilities();

        ShaderProgram shaderProgram = new ShaderProgram("/shaders/vertex.txt", "/shaders/fragment.txt");
        shaderProgramTexture = new ShaderProgram("/shaders/vertexTexture.txt", "/shaders/fragmentTexture.txt");

        Tessellator tessellatorCrosshair = new Tessellator();
        tessellatorCrosshair.setShaderProgram(shaderProgram);

        font = new FontRenderer("/textures/font.png");
        glBindTexture(GL_TEXTURE_2D, font.getTextureId());

        controller = new CameraController(new Camera(
                new Vector3f(0.0f, 0.0f, 4.0f), 0.0f, 0.0f, 0.0f));

        boolean canDragBox = false;
        float boxSize = 100.0f;
        float boxRotationAngle = 0.0f;
        float boxPositionX = 100.0f;
        float boxPositionY = 100.0f;
        float oldX = 0.0f;
        float oldY = 0.0f;

        timerMain = new Timer();

        int fps = 0;

        while (!window.isShouldClose()) {
            fps++;
            if (timerMain.getElapsedTime() >= 1.0) {
                sceneFps = fps;
                fps = 0;
                timerMain.reset();
            }

            GameWindow.pollEvents();

            if (!window.isMinimized()) {

                glClear(GL_COLOR_BUFFER_BIT);

                if (window.isFocusChanged()) {
                    if (!window.isFocused() && Mouse.isGrabbed()) {
                        canDragBox = false;
                        Mouse.setGrabbed(false);
                        Mouse.setPosition(window.getWidth() / 2.0, window.getHeight() / 2.0);
                        System.out.println("Window focus was lost! Mouse is released.");
                    }
                }

                if (window.isWasResized()) {
                    if (!window.isMinimized()) {
                        window.setViewPort();
                        if (boxPositionX > window.getWidth() - boxSize) {
                            boxPositionX = window.getWidth() - boxSize;
                        }
                        if (boxPositionY > window.getHeight() - boxSize) {
                            boxPositionY = window.getHeight() - boxSize;
                        }

                        float xCenter = window.getWidth() / 2.0f;
                        float yCenter = window.getHeight() / 2.0f;
                        tessellatorCrosshair.clearVertexColorLines();
                        tessellatorCrosshair.addVertexColorLines(xCenter - 10.0f, yCenter, 0.0f);
                        tessellatorCrosshair.addVertexColorLines(xCenter + 10.0f, yCenter, 0.0f);
                        tessellatorCrosshair.addVertexColorLines(xCenter, yCenter - 10.0f, 0.0f);
                        tessellatorCrosshair.addVertexColorLines(xCenter, yCenter + 10.0f, 0.0f);
                        tessellatorCrosshair.prepareBufferColorLines();
                    }
                    System.out.println("Window size: " + window.getWidth() + "x" + window.getHeight());
                }

                while (Keyboard.nextEvent()) {
                    switch (Keyboard.eventAction) {
                        case Keyboard.EVENT_PRESS:
                            System.out.println("Key " + Keyboard.eventKey + " is pressed");

                            switch (Keyboard.eventKey) {
                                case Keyboard.KEY_ESCAPE:
                                case Keyboard.KEY_ENTER:
                                case Keyboard.KEY_KP_ENTER:
                                    if (Mouse.isGrabbed()) {
                                        Mouse.setGrabbed(false);
                                        Mouse.setPosition(window.getWidth() / 2.0, window.getHeight() / 2.0);
                                        System.out.println("Mouse is released");
                                    } else {
                                        window.close();
                                    }
                                    break;

                                case Keyboard.KEY_R:
                                    controller.camera.reset();
                                    System.out.println("Reset camera.");
                                    break;
                            }
                            break;

                        case Keyboard.EVENT_RELEASE:
                            System.out.println("Key " + Keyboard.eventKey + " is released");
                            break;

                        case Keyboard.EVENT_REPEAT:
                            System.out.println("Key " + Keyboard.eventKey + " is repeated");
                            break;
                    }
                }

                if (window.isShouldClose()) {
                    break;
                }

                controller.tick();

                while (Mouse.nextEvent()) {
                    switch (Mouse.eventAction) {

                        case Mouse.MOUSE_EVENT_PRESS:
                            switch (Mouse.eventButton) {

                                case Mouse.MOUSE_BUTTON_LEFT:
                                    if (!Mouse.isGrabbed()) {
                                        if (Mouse.getPositionX() >= boxPositionX && Mouse.getPositionX() <= boxPositionX + boxSize &&
                                                Mouse.getPositionY() >= boxPositionY && Mouse.getPositionY() <= boxPositionY + boxSize) {
                                            oldX = (float)Mouse.getPositionX() - boxPositionX;
                                            oldY = (float)Mouse.getPositionY() - boxPositionY;
                                            canDragBox = true;
                                        } else {
                                            Mouse.setGrabbed(true);
                                            System.out.println("Mouse is grabbed");
                                        }
                                    }
                                    System.out.println("LEFT button is pressed");
                                    break;

                                case Mouse.MOUSE_BUTTON_RIGHT:
                                    System.out.println("RIGHT button is pressed");
                                    break;

                                case Mouse.MOUSE_BUTTON_MIDDLE:
                                    System.out.println("MIDDLE button is pressed");
                                    break;
                            }
                            break;

                        case Mouse.MOUSE_EVENT_RELEASE:

                            switch (Mouse.eventButton) {
                                case Mouse.MOUSE_BUTTON_LEFT:
                                    if (!Mouse.isGrabbed()) {
                                        canDragBox = false;
                                    }
                                    System.out.println("LEFT button is released");
                                    break;

                                case Mouse.MOUSE_BUTTON_RIGHT:
                                    System.out.println("RIGHT button is released");
                                    break;

                                case Mouse.MOUSE_BUTTON_MIDDLE:
                                    System.out.println("MIDDLE button is released");
                                    break;
                            }
                            break;

                        case Mouse.MOUSE_EVENT_MOVE:
                            if (canDragBox) {
                                boxPositionX += (float)Mouse.getPositionX() - oldX - boxPositionX;
                                boxPositionY += (float)Mouse.getPositionY() - oldY - boxPositionY;
                                if (boxPositionX < 0.0f) {
                                    boxPositionX = 0.0f;
                                } else if (boxPositionX > window.getWidth() - boxSize) {
                                    boxPositionX = window.getWidth() - boxSize;
                                }
                                if (boxPositionY < 0.0f) {
                                    boxPositionY = 0.0f;
                                } else if (boxPositionY > window.getHeight() - boxSize) {
                                    boxPositionY = window.getHeight() - boxSize;
                                }
                            }
                            break;
                    }
                }

                Matrix4f projectionMatrix = controller.camera.getProjectionMatrix(window.getWidth(), window.getHeight());
                Matrix4f viewMatrix = controller.camera.getViewMatrix();
                Matrix4f matrix = new Matrix4f(projectionMatrix).mul(viewMatrix);

                Tessellator t = new Tessellator();
                t.setShaderProgram(shaderProgram);
                renderCoordinatesNet(t, 10, -1.0f, 1.0f);
                t.prepareBufferColorLines();
                t.drawVertexColorLines(matrix);

                t.setColor(0.1f, 1.0f, 0.0f);
                t.addVertexColorQuads(0.0f, 0.0f, 0.0f);
                t.setColor(0.0f, 0.0f, 1.0f);
                t.addVertexColorQuads(1.0f, 0.0f, 0.0f);
                t.setColor(1.0f, 0.5f, 0.0f);
                t.addVertexColorQuads(1.0f, 1.0f, 0.0f);
                t.setColor(1.0f, 0.2f, 0.0f);
                t.addVertexColorQuads(0.0f, 1.0f, 0.0f);
                t.prepareBufferColorQuads();
                t.drawVertexColorQuads(matrix);

                boxRotationAngle += (float)timerMain.getDeltaTime() * 200.0f;
                while (boxRotationAngle >= 360.0f) {
                    boxRotationAngle -= 360.0f;
                }

                float x = boxSize / 2.0f;
                float y = boxSize / 2.0f;
                Matrix4f orthoMatrix = new Matrix4f().ortho(
                        0.0f, window.getWidth(), window.getHeight(), 0.0f, -1.0f, 1.0f);
                Matrix4f modelMatrix = new Matrix4f()
                        .translate(x, y, 0.0f)
                        .rotate((float) Math.toRadians(boxRotationAngle), 0.0f, 0.0f, 1.0f)
                        .translate(-x, -y, 0.0f);
                viewMatrix.identity().translate(boxPositionX, boxPositionY, 0.0f);

                glEnable(GL_BLEND);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                glEnable(GL_ALPHA_TEST);
                glAlphaFunc(GL_GREATER, 0.0f);

                t.setShaderProgram(shaderProgramTexture);
                t.clearVertexColorTextureQuads();
                t.setColor(0.0f, 1.0f, 0.0f);
                t.addVertexColorTextureQuads(0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
                t.setColor(1.0f, 1.0f, 0.0f);
                t.addVertexColorTextureQuads(boxSize, 0.0f, 0.0f, 1.0f, 0.0f);
                t.setColor(1.0f, 0.0f, 0.0f);
                t.addVertexColorTextureQuads(boxSize, boxSize, 0.0f, 1.0f, 1.0f);
                t.setColor(0.0f, 0.0f, 1.0f);
                t.addVertexColorTextureQuads(0.0f, boxSize, 0.0f, 0.0f, 1.0f);
                t.prepareBufferColorTextureQuads();
                t.drawVertexColorTextureQuads(orthoMatrix.mul(viewMatrix).mul(modelMatrix));

                t.destroy();

                glDisable(GL_ALPHA_TEST);
                glDisable(GL_BLEND);

                orthoMatrix.identity().ortho(
                        0.0f, window.getWidth(), window.getHeight(), 0.0f, -1.0f, 1.0f);
                tessellatorCrosshair.drawVertexColorLines(orthoMatrix);

                drawOnScreenText();

                ShaderProgram.unBind();

                window.swapBuffers();
            }
        }

        font.destroy();
        shaderProgram.destroy();
        shaderProgramTexture.destroy();
        tessellatorCrosshair.destroy();

        glfwMakeContextCurrent(0);

        window.destroy();

        glfwTerminate();
    }

    private void drawOnScreenText() {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_ALPHA_TEST);
        glAlphaFunc(GL_GREATER, 0.0f);

        Matrix4f orthoMatrix = new Matrix4f().ortho(
                0.0f, window.getWidth(), window.getHeight(), 0.0f, -1.0F, 1.0F);
        shaderProgramTexture.use();
        shaderProgramTexture.setUniformMatrix4f("matrix", orthoMatrix);

        Vector3f position = controller.camera.getPosition();
        String t = "Position: [" + String.format("%.2f %.2f %.2f", position.x, position.y, position.z) + "]";
        font.drawString(t, 0.0f, 0.0f, 0xFFFFFF);

        t = "Rotation: [" + String.format("%.2f %.2f", controller.camera.rotationYaw, controller.camera.rotationPitch) + "]";
        font.drawString(t, 0.0f, 18.0f, 0xFFFFFF);

        t = "FPS: " + sceneFps;
        font.drawString(t, window.getWidth() - font.getStringWidth(t), 0.0f, 0x00FF00);

        t = "Delta time: " + String.format("%.9f", timerMain.getDeltaTime());
        font.drawString(t, window.getWidth() - font.getStringWidth(t), 18.0f, 0xFFFFFFF);

        glDisable(GL_ALPHA_TEST);
        glDisable(GL_BLEND);
    }

    private void renderCoordinatesNet(Tessellator t, int netSize, float yPos, float cubeSize) {
        for (float x = -netSize; x <= netSize; x += cubeSize) {
            t.addVertexColorLines(x, yPos, (float)netSize);
            t.addVertexColorLines(x, yPos, (float)-netSize);
        }
        for (float x = -netSize; x <= netSize; x += cubeSize) {
            t.addVertexColorLines((float)netSize, yPos, x);
            t.addVertexColorLines((float)-netSize, yPos, x);
        }
    }

    public static Timer getMainTimer() {
        return timerMain;
    }

    public static void main(String[] args) {
        new Main();
    }
}
