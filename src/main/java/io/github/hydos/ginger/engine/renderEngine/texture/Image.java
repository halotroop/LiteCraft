package io.github.hydos.ginger.engine.renderEngine.texture;

import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_info_from_memory;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.system.MemoryStack;

import io.github.hydos.ginger.engine.renderEngine.tools.IOUtil;

public class Image {
	
	public Image(String imagePath) {
		ByteBuffer img;
        ByteBuffer imageBuffer;
        try {
            imageBuffer = IOUtil.ioResourceToByteBuffer(imagePath, 8 * 1024);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (MemoryStack stack = stackPush()) {
            IntBuffer w    = stack.mallocInt(1);
            IntBuffer h    = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            // Use info to read image metadata without decoding the entire image.
            // We don't need this for this demo, just testing the API.
            if (!stbi_info_from_memory(imageBuffer, w, h, comp)) {
                throw new RuntimeException("Failed to read image information: " + stbi_failure_reason());
            }

//            System.out.println("Image width: " + w.get(0));
//            System.out.println("Image height: " + h.get(0));
//            System.out.println("Image components: " + comp.get(0));
//            System.out.println("Image HDR: " + stbi_is_hdr_from_memory(imageBuffer));

            // Decode the image
            img = stbi_load_from_memory(imageBuffer, w, h, comp, 0);
            if (img == null) {
                throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
            }
            
            this.image = img;
            this.width = w.get(0);
            this.height = h.get(0);
        }
    }
	
	public static Image createImage(String imagePath) {
		ByteBuffer img;
        ByteBuffer imageBuffer;
        try {
            imageBuffer = IOUtil.ioResourceToByteBuffer(imagePath, 8 * 1024);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (MemoryStack stack = stackPush()) {
            IntBuffer w    = stack.mallocInt(1);
            IntBuffer h    = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            // Use info to read image metadata without decoding the entire image.
            // We don't need this for this demo, just testing the API.
            if (!stbi_info_from_memory(imageBuffer, w, h, comp)) {
                throw new RuntimeException("Failed to read image information: " + stbi_failure_reason());
            }

            img = stbi_load_from_memory(imageBuffer, w, h, comp, 0);
            if (img == null) {
                throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
            }
            
            return new Image(w.get(0), h.get(0), img);
        }
    }
	
    public ByteBuffer getImage() {
        return image;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private ByteBuffer image;
    private int width, height;

    Image(int width, int heigh, ByteBuffer image) {
        this.image = image;
        this.height = heigh;
        this.width = width;
    }
    

}