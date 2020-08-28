package com.engine;

import org.lwjgl.*;
import javax.imageio.*;
import java.io.*;
import java.awt.image.*;
import java.nio.*;

import static org.lwjgl.opengl.GL42C.*;

public class TextureLoader {

    public static int loadTexture(String fileName){
        try {
            BufferedImage img;
            if (fileName.startsWith("/")) {
                img = ImageIO.read(TextureLoader.class.getResource(fileName));
            } else {
                img = ImageIO.read(new File(fileName));
            }

            return loadTexture(img);

        } catch (IOException e) {
            System.out.println(fileName);
            e.printStackTrace();
            throw new RuntimeException("Error loading image: " + fileName);
        }
    }

    public static int loadTexture(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();
        ByteBuffer pixels = BufferUtils.createByteBuffer(w * h * 4);
        int[] rawPixels = new int[w * h];
        image.getRGB(0,0, w, h, rawPixels, 0, w);
        pixels.asIntBuffer().put(rawPixels);
        int id = glGenTextures();
        if (id > 0) {
            glBindTexture(GL_TEXTURE_2D, id);
            glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA8, w, h);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, w, h, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
            glBindTexture(GL_TEXTURE_2D, 0);
        }
        return id;
    }
}
