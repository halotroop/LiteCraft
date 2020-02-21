package io.github.hydos.ginger.engine.renderEngine.shaders;

import java.util.List;

import io.github.hydos.ginger.engine.cameras.ThirdPersonCamera;
import io.github.hydos.ginger.engine.elements.objects.Light;
import io.github.hydos.ginger.engine.mathEngine.Maths;
import io.github.hydos.ginger.engine.mathEngine.matrixes.Matrix4f;
import io.github.hydos.ginger.engine.mathEngine.vectors.Vector3f;

public class TerrainShader extends ShaderProgram
{
	private static final int MAX_LIGHTS = 5;
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_lightColour[];
	private int location_lightPosition[];
	private int location_attenuation[];
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_skyColour;
	private int location_backgroundTexture;
	private int location_rTexture;
	private int location_gTexture;
	private int location_bTexture;
	private int location_blendMap;
	private int location_toShadowMapSpace;
	private int location_shadowMap;

	public TerrainShader()
	{ super("terrainVertexShader.glsl", "terrainFragmentShader.glsl"); }

	@Override
	protected void getAllUniformLocations()
	{
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_lightPosition = new int[MAX_LIGHTS];
		location_lightColour = new int[MAX_LIGHTS];
		location_attenuation = new int[MAX_LIGHTS];
		for (int i = 0; i < MAX_LIGHTS; i++)
		{
			location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
			location_lightColour[i] = super.getUniformLocation("lightColour[" + i + "]");
			location_attenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
		}
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_skyColour = super.getUniformLocation("skyColour");
		location_backgroundTexture = super.getUniformLocation("backgroundTexture");
		location_rTexture = super.getUniformLocation("rTexture");
		location_gTexture = super.getUniformLocation("gTexture");
		location_bTexture = super.getUniformLocation("bTexture");
		location_blendMap = super.getUniformLocation("blendMap");
		location_toShadowMapSpace = super.getUniformLocation("toShadowMapSpace");
		location_shadowMap = super.getUniformLocation("shadowMap");
	}

	@Override
	protected void bindAttributes()
	{
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
	}

	public void loadTransformationMatrix(Matrix4f matrix)
	{ super.loadMatrix(location_transformationMatrix, matrix); }

	public void loadProjectionMatrix(Matrix4f matrix)
	{ super.loadMatrix(location_projectionMatrix, matrix); }

	public void loadViewMatrix(ThirdPersonCamera camera)
	{
		Matrix4f matrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, matrix);
	}

	public void loadLights(List<Light> lights)
	{
		for (int i = 0; i < MAX_LIGHTS; i++)
		{
			if (i < lights.size())
			{
				super.loadVector(location_lightPosition[i], lights.get(i).getPosition());
				super.loadVector(location_lightColour[i], lights.get(i).getColour());
				super.loadVector(location_attenuation[i], lights.get(i).getAttenuation());
			}
			else
			{
				super.loadVector(location_lightPosition[i], new Vector3f(0, 0, 0));
				super.loadVector(location_lightColour[i], new Vector3f(0, 0, 0));
				super.loadVector(location_attenuation[i], new Vector3f(1, 0, 0));
			}
		}
	}

	public void loadShine(float damper, float reflectivity)
	{
		super.loadFloat(location_shineDamper, damper);
		super.loadFloat(location_reflectivity, reflectivity);
	}

	public void loadSkyColour(Vector3f colour)
	{ super.loadVector(location_skyColour, colour); }

	public void loadToShadowMapSpace(Matrix4f matrix)
	{ super.loadMatrix(location_toShadowMapSpace, matrix); }

	public void connectTextureUnits()
	{
		super.loadInt(location_backgroundTexture, 0);
		super.loadInt(location_rTexture, 1);
		super.loadInt(location_gTexture, 2);
		super.loadInt(location_bTexture, 3);
		super.loadInt(location_blendMap, 4);
		super.loadInt(location_shadowMap, 5);
	}
}
