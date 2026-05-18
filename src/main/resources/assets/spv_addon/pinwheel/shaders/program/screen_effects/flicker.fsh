#version 150

uniform sampler2D DiffuseSampler0;
in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 base = texture(DiffuseSampler0, texCoord);
    float edge = smoothstep(0.78, 0.18, length(texCoord - vec2(0.5)));
    fragColor = vec4(base.rgb * mix(0.92, 1.04, edge), base.a);
}
