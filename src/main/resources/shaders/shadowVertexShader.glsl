#version 150

in vec3 in_position;
in vec2 in_textureCoords;

out vec2 textureCoords;

uniform mat4 mvpMatrix;

void main(void){

	textureCoords = in_textureCoords;

	gl_Position = mvpMatrix * vec4(in_position, 1.0);

}
