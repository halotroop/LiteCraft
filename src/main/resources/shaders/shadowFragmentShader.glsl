#version 400

in vec2 textureCoords;

out vec4 out_Colour;

uniform sampler2D modelTexture;

void main(void){
    float alpha = texture(modelTexture, textureCoords).a;
    if(alpha < 0.4){
    	discard;
    }

    out_Colour = vec4(1.0, 1.0, 1.0, 0.1);
}
