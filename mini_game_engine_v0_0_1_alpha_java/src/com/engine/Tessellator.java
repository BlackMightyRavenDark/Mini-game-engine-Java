package com.engine;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL46C.*;

public class Tessellator {

    private ShaderProgram shaderProgram;

    private final ArrayList<Float> arrayVertexColorLines;
    private final ArrayList<Float> arrayVertexColorQuads;
    private final ArrayList<Float> arrayVertexColorTextureQuads;

    private final int vaoVertexColorLines;
    private final int vboVertexColorLines;

    private final int vaoVertexColorQuads;
    private final int vboVertexColorQuads;

    private final int vaoVertexColorTextureQuads;
    private final int vboVertexColorTextureQuads;

    private float currentColorRed;
    private float currentColorGreen;
    private float currentColorBlue;
    private float currentColorAlpha;

    public Tessellator() {
        arrayVertexColorLines = new ArrayList<>();
        arrayVertexColorQuads = new ArrayList<>();
        arrayVertexColorTextureQuads = new ArrayList<>();

        vaoVertexColorLines = glGenVertexArrays();
        vaoVertexColorQuads = glGenVertexArrays();
        vaoVertexColorTextureQuads = glGenVertexArrays();
        
        vboVertexColorLines = glGenBuffers();
        vboVertexColorQuads = glGenBuffers();
        vboVertexColorTextureQuads = glGenBuffers();

        setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void setShaderProgram(ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
    }

    public void setColor(float r, float g, float b, float a) {
        currentColorRed = r;
        currentColorGreen = g;
        currentColorBlue = b;
        currentColorAlpha = a;
    }

    public void setColor(float r, float g, float b) {
        setColor(r, g, b, 1.0f);
    }




    public void addVertexColorLines(float x, float y, float z) {
        arrayVertexColorLines.add(x);
        arrayVertexColorLines.add(y);
        arrayVertexColorLines.add(z);
        arrayVertexColorLines.add(1.0f);
        arrayVertexColorLines.add(currentColorRed);
        arrayVertexColorLines.add(currentColorGreen);
        arrayVertexColorLines.add(currentColorBlue);
        arrayVertexColorLines.add(currentColorAlpha);
    }

    public void prepareBufferColorLines() {
        if (arrayVertexColorLines.size() > 0) {
            glBindVertexArray(vaoVertexColorLines);
            glBindBuffer(GL_ARRAY_BUFFER, vboVertexColorLines);
            glBufferData(GL_ARRAY_BUFFER, Tessellator.toFloatBuffer(arrayVertexColorLines), GL_STATIC_DRAW);
            int strideSize = 8 * 4;
            glVertexAttribPointer(0, 4, GL_FLOAT, false, strideSize, 0);
            glVertexAttribPointer(1, 4, GL_FLOAT, false, strideSize, 4 * 4);
            glEnableVertexAttribArray(0);
            glEnableVertexAttribArray(1);
            glBindVertexArray(0);
        }
    }

    public void drawVertexColorLines(Matrix4f matrix) {
        if (arrayVertexColorLines.size() > 0) {
            shaderProgram.use();
            shaderProgram.setUniformMatrix4f("matrix", matrix);

            glBindVertexArray(vaoVertexColorLines);
            glDrawArrays(GL_LINES, 0, arrayVertexColorLines.size() / 2);
            glBindVertexArray(0);
        }
    }

    public void clearVertexColorLines() {
        arrayVertexColorLines.clear();
    }




    public void addVertexColorQuads(float x, float y, float z) {
        arrayVertexColorQuads.add(x);
        arrayVertexColorQuads.add(y);
        arrayVertexColorQuads.add(z);
        arrayVertexColorQuads.add(1.0f);
        arrayVertexColorQuads.add(currentColorRed);
        arrayVertexColorQuads.add(currentColorGreen);
        arrayVertexColorQuads.add(currentColorBlue);
        arrayVertexColorQuads.add(currentColorAlpha);
    }

    public void prepareBufferColorQuads() {
        if (arrayVertexColorQuads.size() > 0) {
            glBindVertexArray(vaoVertexColorQuads);
            glBindBuffer(GL_ARRAY_BUFFER, vboVertexColorQuads);
            int strideSize = 8 * 4;
            glBufferData(GL_ARRAY_BUFFER, Tessellator.toFloatBuffer(arrayVertexColorQuads), GL_STATIC_DRAW);
            glVertexAttribPointer(0, 4, GL_FLOAT, false, strideSize, 0);
            glVertexAttribPointer(1, 4, GL_FLOAT, false, strideSize, 4 * 4);
            glEnableVertexAttribArray(0);
            glEnableVertexAttribArray(1);
            glBindVertexArray(0);
        }
    }

    public void drawVertexColorQuads(Matrix4f matrix) {
        if (arrayVertexColorQuads.size() > 0) {
            shaderProgram.use();

            shaderProgram.setUniformMatrix4f("matrix", matrix);

            glBindVertexArray(vaoVertexColorQuads);
            glDrawArrays(GL_QUADS, 0, arrayVertexColorQuads.size() / 8);
            glBindVertexArray(0);
        }
    }

    public void clearVertexColorQuads() {
        arrayVertexColorQuads.clear();
    }



    public void addVertexColorTextureQuads(float x, float y, float z, float u, float v) {
        arrayVertexColorTextureQuads.add(x);
        arrayVertexColorTextureQuads.add(y);
        arrayVertexColorTextureQuads.add(z);
        arrayVertexColorTextureQuads.add(1.0f);
        arrayVertexColorTextureQuads.add(currentColorRed);
        arrayVertexColorTextureQuads.add(currentColorGreen);
        arrayVertexColorTextureQuads.add(currentColorBlue);
        arrayVertexColorTextureQuads.add(currentColorAlpha);
        arrayVertexColorTextureQuads.add(u);
        arrayVertexColorTextureQuads.add(v);
    }

    public void prepareBufferColorTextureQuads() {
        if (arrayVertexColorTextureQuads.size() > 0) {
            glBindVertexArray(vaoVertexColorTextureQuads);
            glBindBuffer(GL_ARRAY_BUFFER, vboVertexColorTextureQuads);
            int strideSize = 10 * 4;
            glBufferData(GL_ARRAY_BUFFER, Tessellator.toFloatBuffer(arrayVertexColorTextureQuads), GL_STATIC_DRAW);
            glVertexAttribPointer(0, 4, GL_FLOAT, false, strideSize, 0);
            glVertexAttribPointer(1, 4, GL_FLOAT, false, strideSize, 4 * 4);
            glVertexAttribPointer(2, 2, GL_FLOAT, false, strideSize, 8 * 4);
            glEnableVertexAttribArray(0);
            glEnableVertexAttribArray(1);
            glEnableVertexAttribArray(2);
            glBindVertexArray(0);
        }
    }

    public void drawVertexColorTextureQuads(Matrix4f matrix) {
        if (arrayVertexColorTextureQuads.size() > 0) {
            shaderProgram.use();
            shaderProgram.setUniformMatrix4f("matrix", matrix);

            glBindVertexArray(vaoVertexColorTextureQuads);
            glDrawArrays(GL_QUADS, 0, arrayVertexColorTextureQuads.size() / 10);
            glBindVertexArray(0);
        }
    }

    public void clearVertexColorTextureQuads() {
        arrayVertexColorTextureQuads.clear();
    }


    public void destroy() {
        glDeleteVertexArrays(vaoVertexColorLines);
        glDeleteBuffers(vboVertexColorLines);
        glDeleteVertexArrays(vaoVertexColorQuads);
        glDeleteBuffers(vboVertexColorQuads);
        glDeleteVertexArrays(vaoVertexColorTextureQuads);
        glDeleteBuffers(vboVertexColorTextureQuads);
    }

    public static FloatBuffer toFloatBuffer(ArrayList<Float> arrayList) {
        float[] floats = new float[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            floats[i] = arrayList.get(i);
        }
        FloatBuffer buffer = BufferUtils.createFloatBuffer(arrayList.size());
        buffer.put(floats);
        buffer.flip();
        return buffer;
    }

}
