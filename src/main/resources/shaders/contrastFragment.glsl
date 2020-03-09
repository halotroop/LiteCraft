#version 400

in vec2 textureCoords;

out vec4 out_Colour;

uniform sampler2D colourTexture;

const float brightness = 1.2;
const float contrast = 0.1;
const float saturation = 1.65;

void main(void){

 out_Colour = texture(colourTexture, textureCoords);

 // calculate saturation
 vec3 luminanceWeights = vec3(0.299, 0.587, 0.114);
 float luminance = dot(out_Colour.rgb, luminanceWeights);
 out_Colour = mix(vec4(luminance), out_Colour, saturation);

 // calculate contrast
 out_Colour.rgb = (out_Colour.rgb - 0.5) * (1.0 + contrast) + 0.5;

 // calculate brightness
 out_Colour.rgb *= brightness;


}
