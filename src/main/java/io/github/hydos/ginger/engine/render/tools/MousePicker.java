package io.github.hydos.ginger.engine.render.tools;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.github.halotroop.litecraft.LiteCraftMain;

import io.github.hydos.ginger.engine.cameras.ThirdPersonCamera;
import io.github.hydos.ginger.engine.io.Window;
import io.github.hydos.ginger.engine.math.Maths;
import io.github.hydos.ginger.engine.terrain.Terrain;

public class MousePicker
{
	private static final int RECURSION_COUNT = 200;
	private static final float RAY_RANGE = 600;
	private Vector3f currentRay = new Vector3f();
	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix;
	private ThirdPersonCamera camera;
	private Terrain terrain;
	private Vector3f currentTerrainPoint;

	public MousePicker(ThirdPersonCamera cam, Matrix4f projection, Terrain terrain)
	{
		camera = cam;
		projectionMatrix = projection;
		viewMatrix = Maths.createViewMatrix(camera);
		this.terrain = terrain;
	}

	public Vector3f getCurrentTerrainPoint()
	{ return currentTerrainPoint; }

	public Vector3f getCurrentRay()
	{ return currentRay; }

	public void update()
	{
		viewMatrix = Maths.createViewMatrix(camera);
		currentRay = calculateMouseRay();
		if (intersectionInRange(0, RAY_RANGE, currentRay))
		{
			currentTerrainPoint = binarySearch(0, 0, RAY_RANGE, currentRay);
		}
		else
		{
			currentTerrainPoint = null;
		}
	}

	private Vector3f calculateMouseRay()
	{
		float mouseX = (float) Window.getMouseX();
		float mouseY = (float) (LiteCraftMain.height - Window.getMouseY());
		Vector2f normalizedCoords = getNormalisedDeviceCoordinates(mouseX, mouseY);
		Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1.0f, 1.0f);
		Vector4f eyeCoords = toEyeCoords(clipCoords);
		Vector3f worldRay = toWorldCoords(eyeCoords);
		return worldRay;
	}

	private Vector3f toWorldCoords(Vector4f eyeCoords)
	{
		Matrix4f invertedView = viewMatrix.invert(viewMatrix);
		Vector4f rayWorld = invertedView.transform(eyeCoords);
		Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
		mouseRay.normalize();
		return mouseRay;
	}

	private Vector4f toEyeCoords(Vector4f clipCoords)
	{
		Matrix4f invertedProjection = projectionMatrix.invert(projectionMatrix);
		Vector4f eyeCoords = invertedProjection.transform(clipCoords);
		return new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 0f);
	}

	private Vector2f getNormalisedDeviceCoordinates(float mouseX, float mouseY)
	{
		float x = (2.0f * mouseX) / LiteCraftMain.width - 1f;
		float y = (2.0f * mouseY) / LiteCraftMain.height - 1f;
		return new Vector2f(x, y);
	}
	//**********************************************************

	private Vector3f getPointOnRay(Vector3f ray, float distance)
	{
		Vector3f output = new Vector3f();
		Vector3f camPos = camera.getPosition();
		Vector3f start = new Vector3f(camPos.x, camPos.y, camPos.z);
		Vector3f scaledRay = new Vector3f(ray.x * distance, ray.y * distance, ray.z * distance);
		return output.add(start, scaledRay);
	}

	private Vector3f binarySearch(int count, float start, float finish, Vector3f ray)
	{
		float half = start + ((finish - start) / 2f);
		if (count >= RECURSION_COUNT)
		{
			Vector3f endPoint = getPointOnRay(ray, half);
			Terrain terrain = getTerrain(endPoint.x, endPoint.z);
			if (terrain != null)
			{
				return endPoint;
			}
			else
			{
				return null;
			}
		}
		if (intersectionInRange(start, half, ray))
		{
			return binarySearch(count + 1, start, half, ray);
		}
		else
		{
			return binarySearch(count + 1, half, finish, ray);
		}
	}

	private boolean intersectionInRange(float start, float finish, Vector3f ray)
	{
		Vector3f startPoint = getPointOnRay(ray, start);
		Vector3f endPoint = getPointOnRay(ray, finish);
		if (!isUnderGround(startPoint) && isUnderGround(endPoint))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	private boolean isUnderGround(Vector3f testPoint)
	{
		Terrain terrain = getTerrain(testPoint.x(), testPoint.z());
		float height = 0;
		if (terrain != null)
		{ height = terrain.getHeightOfTerrain(testPoint.x(), testPoint.z()); }
		if (testPoint.y < height)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	private Terrain getTerrain(float worldX, float worldZ)
	{ return terrain; }
}