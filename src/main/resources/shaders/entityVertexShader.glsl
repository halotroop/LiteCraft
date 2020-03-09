#version 150


in vec3 position;
in vec2 textureCoords;
in vec3 normal;

out vec2 pass_textureCoords;
out vec3 surfaceNormal;
out vec3 toLightVector[5];
out vec3 toCameraVector;
out float visibility;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition[5];

const vec4 plane = vec4(0, -1, 0, 15);

uniform float useFakeLighting;

const float density = 0.0025;
const float gradient = 10.0;

void main(void){

	vec4 worldPosition = transformationMatrix * vec4(position.xyz,1.0);

	gl_ClipDistance[0] = dot(worldPosition, plane);

	vec4 positionRelativeToCam = viewMatrix * worldPosition;

	gl_Position = projectionMatrix * positionRelativeToCam;
	pass_textureCoords = textureCoords;

	vec3 actualNormal = normal;
	actualNormal = vec3(0.0, 1.0, 0.0);

	surfaceNormal = (transformationMatrix * vec4(actualNormal, 0.0)).xyz;
	for(int i=0;i<5;i++){
		toLightVector[i] = lightPosition[i] - worldPosition.xyz;
	}
	toCameraVector = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;

	float distance = length(positionRelativeToCam.xyz);
	visibility = exp(-pow((distance*density), gradient));
	visibility = clamp(visibility, 0.0, 1.0);


}
