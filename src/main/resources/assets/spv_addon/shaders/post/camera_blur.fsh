#version 150

uniform sampler2D DiffuseSampler;
uniform float BlurStrength; // 0.0 = net, 1.0+ = flou
uniform float Zoom;         // 1.0 = normal, 3.0 = max zoom

in vec2 texCoord;
out vec4 fragColor;

void main() {
    float strength = BlurStrength * 0.035; // Ajuste le facteur au besoin

    vec4 sum = vec4(0.0);
    sum += texture(DiffuseSampler, texCoord + vec2(-strength, 0.0)) * 0.20;
    sum += texture(DiffuseSampler, texCoord + vec2( strength, 0.0)) * 0.20;
    sum += texture(DiffuseSampler, texCoord + vec2(0.0, -strength)) * 0.20;
    sum += texture(DiffuseSampler, texCoord + vec2(0.0,  strength)) * 0.20;
    sum += texture(DiffuseSampler, texCoord) * 0.20;

    fragColor = sum;
}
