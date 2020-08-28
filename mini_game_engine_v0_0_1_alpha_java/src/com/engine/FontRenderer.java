package com.engine;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.lwjgl.opengl.GL33C.*;

public class FontRenderer {
    private final String vertexShaderSource =
            "#version 330 core\n" +
            "\n" +
            "layout (location = 0) in vec3 position;\n" +
            "layout (location = 1) in vec4 color;\n" +
            "layout (location = 2) in vec2 coords;\n" +
            "\n" +
            "uniform mat4 matrix;\n" +
            "\n" +
            "out vec4 col;\n" +
            "out vec2 texCoords;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    col = color;\n" +
            "    texCoords = coords;\n" +
            "    gl_Position = matrix * vec4(position, 1.0f);\n" +
            "}\n";
    private final String fragmentShaderSource =
            "#version 330 core\n" +
            "\n" +
            "in vec4 col;\n" +
            "in vec2 texCoords;\n" +
            "\n" +
            "uniform sampler2D texSampler;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    gl_FragColor = texture(texSampler, texCoords) * col;\n" +
            "}\n";

    private final int textureId;
    private final int imageWidth;
    private final int imageHeight;
    private float fontSize = 16.0f;
    private final float symbolWidthCoef = 1.3f;

    private final int vao;
    private final int vbo;
    private final ShaderProgram shaderProgram;

    public FontRenderer(String fileName) {
        BufferedImage img;
        try {
            if (fileName.startsWith("/")) {
                img = ImageIO.read(TextureLoader.class.getResource(fileName));
            } else {
                img = ImageIO.read(new File(fileName));
            }

            textureId = TextureLoader.loadTexture(img);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error loading image: " + fileName);
        }

        imageWidth = img.getWidth();
        imageHeight = img.getHeight();

        shaderProgram = new ShaderProgram(vertexShaderSource, fragmentShaderSource);
        vao = glGenVertexArrays();
        vbo = glGenBuffers();
    }

    public void drawString(String textString, float xPos, float yPos, int color) {
        float r = (color >> 16 & 0xFF) / 255.0f;
        float g = (color >> 8  & 0xFF) / 255.0f;
        float b = (color       & 0xFF) / 255.0f;
        byte[] chars = textString.getBytes(Charset.forName("cp1251"));
        int glyphSize = imageWidth / 16;
        float xOffset = 0.0f;
        float[] buffer = new float[chars.length * 36];
        for (int i = 0; i < chars.length; i++) {
            char charId = (char)chars[i];
            float texPosX = ((charId % 16) * glyphSize) / (float)imageWidth;
            float texPosY = charId / 16 / 16.0f;
            float u1 = texPosX + (glyphSize / (float)imageWidth);
            float v1 = texPosY + (glyphSize / (float)imageWidth);

            int arrayIndex = i * 36;
            buffer[arrayIndex] = xPos + xOffset;
            buffer[arrayIndex + 1] = yPos;
            buffer[arrayIndex + 2] = 0.0f;
            buffer[arrayIndex + 3] = r;
            buffer[arrayIndex + 4] = g;
            buffer[arrayIndex + 5] = b;
            buffer[arrayIndex + 6] = 1.0f;
            buffer[arrayIndex + 7] = texPosX;
            buffer[arrayIndex + 8] = texPosY;

            buffer[arrayIndex + 9] = xPos + xOffset + fontSize;
            buffer[arrayIndex + 10] = yPos;
            buffer[arrayIndex + 11] = 0.0f;
            buffer[arrayIndex + 12] = r;
            buffer[arrayIndex + 13] = g;
            buffer[arrayIndex + 14] = b;
            buffer[arrayIndex + 15] = 1.0f;
            buffer[arrayIndex + 16] = u1;
            buffer[arrayIndex + 17] = texPosY;

            buffer[arrayIndex + 18] = xPos + xOffset + fontSize;
            buffer[arrayIndex + 19] = yPos + fontSize;
            buffer[arrayIndex + 20] = 0.0f;
            buffer[arrayIndex + 21] = r;
            buffer[arrayIndex + 22] = g;
            buffer[arrayIndex + 23] = b;
            buffer[arrayIndex + 24] = 1.0f;
            buffer[arrayIndex + 25] = u1;
            buffer[arrayIndex + 26] = v1;

            buffer[arrayIndex + 27] = xPos + xOffset;
            buffer[arrayIndex + 28] = yPos + fontSize;
            buffer[arrayIndex + 29] = 0.0f;
            buffer[arrayIndex + 30] = r;
            buffer[arrayIndex + 31] = g;
            buffer[arrayIndex + 32] = b;
            buffer[arrayIndex + 33] = 1.0f;
            buffer[arrayIndex + 34] = texPosX;
            buffer[arrayIndex + 35] = v1;

            xOffset += fontSize / symbolWidthCoef;
        }

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STREAM_DRAW);
        int strideSize = 9 * 4;
        glVertexAttribPointer(0, 3, GL_FLOAT, false, strideSize, 0);
        glVertexAttribPointer(1, 4, GL_FLOAT, false, strideSize, 3 * 4);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, strideSize, 7 * 4);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        glDrawArrays(GL_QUADS, 0, buffer.length / 9);

        glBindVertexArray(0);
    }

    public float getStringWidth(String t) {
        return t.length() * fontSize / symbolWidthCoef;
    }

    public int getTextureId() {
        return textureId;
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    public void destroy() {
        shaderProgram.destroy();
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
    }
}
