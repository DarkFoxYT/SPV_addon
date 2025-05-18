#version 150

uniform sampler2D DiffuseSampler;
uniform float BlurStrength; // From your mod code

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec2 texel = 1.0 / textureSize(DiffuseSampler, 0);
    vec3 result = texture(DiffuseSampler, texCoord).rgb;
    float blur = BlurStrength * 0.003;
    result += texture(DiffuseSampler, texCoord + vec2(blur, 0.0)).rgb;
    result += texture(DiffuseSampler, texCoord - vec2(blur, 0.0)).rgb;
    result += texture(DiffuseSampler, texCoord + vec2(0.0, blur)).rgb;
    result += texture(DiffuseSampler, texCoord - vec2(0.0, blur)).rgb;
    result /= 5.0;
    fragColor = vec4(result, 1.0);
}
