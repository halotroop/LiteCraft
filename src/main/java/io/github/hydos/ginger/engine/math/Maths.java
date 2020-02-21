package io.github.hydos.ginger.engine.math;

import java.lang.Math;

import org.joml.*;

import io.github.hydos.ginger.engine.cameras.ThirdPersonCamera;

public class Maths
{
	public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, Vector3f scale)
	{
		Matrix4f matrix = new Matrix4f();
		matrix.identity();
		matrix.translate(translation, matrix);
		matrix.rotate((float) Math.toRadians(rx), new Vector3f(1, 0, 0), matrix);
		matrix.rotate((float) Math.toRadians(ry), new Vector3f(0, 1, 0), matrix);
		matrix.rotate((float) Math.toRadians(rz), new Vector3f(0, 0, 1), matrix);
		matrix.scale(scale, matrix);
		return matrix;
	}

	public static Matrix4f createViewMatrix(ThirdPersonCamera camera)
	{
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.identity();
		viewMatrix.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0), viewMatrix);
		viewMatrix.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0), viewMatrix);
		viewMatrix.rotate((float) Math.toRadians(camera.getRoll()), new Vector3f(0, 0, 1), viewMatrix);
		Vector3f cameraPos = camera.getPosition();
		Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
		viewMatrix.translate(negativeCameraPos, viewMatrix);
		return viewMatrix;
	}

	public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos)
	{
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}

	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale)
	{
		Matrix4f matrix = new Matrix4f();
		matrix.identity();
		matrix.translate(new Vector3f(translation.x, translation.y, 1), matrix);
		matrix.scale(new Vector3f(scale.x, scale.y, 1f), matrix);
		return matrix;
	}

	public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale)
	{
		Matrix4f matrix = new Matrix4f();
		matrix.identity();
		matrix.translate(translation, matrix);
		matrix.rotate((float) Math.toRadians(rx), new Vector3f(1, 0, 0), matrix);
		matrix.rotate((float) Math.toRadians(ry), new Vector3f(0, 1, 0), matrix);
		matrix.rotate((float) Math.toRadians(rz), new Vector3f(0, 0, 1), matrix);
		Vector3f newScale = new Vector3f(scale, scale, scale);
		matrix.scale(newScale, matrix);
		return matrix;
	}

	public static Vector3f Vec4ToVec3(Vector4f eyeSpacePos)
	{
		Vector3f vec3 = new Vector3f();
		vec3.x = eyeSpacePos.x;
		vec3.y = eyeSpacePos.y;
		vec3.z = eyeSpacePos.z;
		return vec3;
	}

	public static void scale(Vector3f change, float time)
	{
		change.x = change.x * time;
		change.y = change.y * time;
		change.x = change.x * time;
	}
}
