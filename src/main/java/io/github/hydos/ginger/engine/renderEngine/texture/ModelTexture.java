package io.github.hydos.ginger.engine.renderEngine.texture;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

public class ModelTexture {
    private int textureID = GL11.glGenTextures();
    
    private boolean transparency = false;
    private boolean useFakeLighting = false;
    
    public int numberOfRows = 1;
    
    private int normalMap;
    
    private float shineDamper = 1;
    private float reflectivity = 0;
    
    private Image texture;
    
    public ModelTexture(String file) {
        texture = Image.createImage("/textures/" + file);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textureID);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, 10241, 9729.0f);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, 10240, 9729.0f);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, texture.getWidth(), texture.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, texture.getImage());
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.4f);
    }

    public ModelTexture(Image texture) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textureID);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, 10241, 9729.0f);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, 10240, 9729.0f);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, 6408, texture.getWidth(), texture.getHeight(), 0, 6408, 5121, texture.getImage());
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

	public void remove() {
        GL11.glDeleteTextures(this.textureID);
    }

    public int getTextureID() {
        return this.textureID;
    }
    
    public Image getTexture() {
    	return texture;
    }

	public int getNormalMap() {
		return normalMap;
	}

	public void setNormalMap(int normalMap) {
		this.normalMap = normalMap;
	}

	public float getShineDamper() {
		return shineDamper;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}

	public boolean isTransparent() {
		return transparency;
	}

	public void setTransparency(boolean b) {
		this.transparency = b;
	}

	public boolean isUseFakeLighting() {
		return useFakeLighting;
	}

	public void useFakeLighting(boolean useFakeLighting) {
		this.useFakeLighting = useFakeLighting;
	}
    
    
}

