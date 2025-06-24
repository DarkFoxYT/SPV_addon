
#version 150

uniform sampler2D DiffuseSampler;
uniform float Time;
in vec2 texCoord;
out vec4 fragColor;

void main() {
    float offset = 0.005 * sin(Time * 5.0);
    float r = texture(DiffuseSampler, texCoord + vec2(-offset, 0.0)).r;
    float g = texture(DiffuseSampler, texCoord).g;
    float b = texture(DiffuseSampler, texCoord + vec2(offset, 0.0)).b;
    float edge = abs(r - b) > 0.1 ? 1.0 : 0.0;
    fragColor = vec4(r + edge, g, b + edge, 1.0);
}