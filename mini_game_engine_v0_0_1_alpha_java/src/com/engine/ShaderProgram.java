package com.engine;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL33C.*;

public class ShaderProgram {
    private int programId;

    public ShaderProgram(String vertexSource, String fragmentSource) {
        int vertexShader = compile(vertexSource.startsWith("#") ? vertexSource :
                ShaderProgram.readFile(vertexSource), GL_VERTEX_SHADER);
        if (vertexShader == 0) {
            throw new RuntimeException("Vertex shader error");
        }
        int fragmentShader = compile(fragmentSource.startsWith("#") ? fragmentSource :
                ShaderProgram.readFile(fragmentSource), GL_FRAGMENT_SHADER);
        if (fragmentShader == 0) {
            glDeleteShader(vertexShader);
            throw new RuntimeException("Fragment shader error");
        }
        if (!link(vertexShader, fragmentShader)) {
            throw new RuntimeException("Error linking shader program!");
        }

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    private int compile(String shaderSource, int shaderType) {
        int shaderId = glCreateShader(shaderType);
        glShaderSource(shaderId, shaderSource);
        glCompileShader(shaderId);
        int isCompiled = glGetShaderi(shaderId, GL_COMPILE_STATUS);
        if (isCompiled == GL_FALSE) {
            int logLength = glGetShaderi(shaderId, GL_INFO_LOG_LENGTH);
            if (logLength > 0) {
                String infoLog = glGetShaderInfoLog(shaderId, logLength);
                glDeleteShader(shaderId);
                System.out.println((shaderType == GL_VERTEX_SHADER ? "VERTEX " : "FRAGMENT ") +
                        "SHADER COMPILING ERROR:");
                System.out.println(infoLog);
                return 0;
            }
        }
        return shaderId;
    }

    private boolean link(int vertexShaderId, int fragmentShaderId) {
        programId = glCreateProgram();
        glAttachShader(programId, vertexShaderId);
        glAttachShader(programId, fragmentShaderId);
        glLinkProgram(programId);
        int isLinked = glGetProgrami(programId, GL_LINK_STATUS);
        if (isLinked == 0) {
            int logLength = glGetProgrami(programId, GL_INFO_LOG_LENGTH);
            if (logLength > 0) {
                String infoLog = glGetProgramInfoLog(programId, logLength);
                System.out.println("Linking shader program failed!");
                System.out.println(infoLog);
                glDeleteShader(vertexShaderId);
                glDeleteShader(fragmentShaderId);
                glDeleteProgram(programId);
                programId = 0;
                return false;
            }
        }
        return isLinked != 0;
    }

    public void use() {
        glUseProgram(programId);
    }

    public static void unBind() {
        glUseProgram(0);
    }

    public void setUniformMatrix4f(String name, Matrix4f matrix) {
        int loc = glGetUniformLocation(programId, name);
        FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
        matrix.get(matrixBuffer);
        glUniformMatrix4fv(loc,false, matrixBuffer);
    }

    public void destroy() {
       if (programId != 0) {
           glDeleteProgram(programId);
       }
    }

    public static String readFile(String fileName) {
        String t = null;
        try {
            if (fileName.startsWith("/")) {
                URL url = ShaderProgram.class.getResource(fileName);
                t = new String(Files.readAllBytes(Paths.get(url.toURI())));
            } else {
                t = new String(Files.readAllBytes(Paths.get(fileName)));
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return t;
    }
}
