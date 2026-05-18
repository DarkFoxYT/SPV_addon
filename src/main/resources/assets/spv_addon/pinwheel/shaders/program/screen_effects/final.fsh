#version 150

uniform sampler2D DiffuseSampler0;
in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 base = texture(DiffuseSampler0, texCoord);
    fragColor = vec4(base.rgb, base.a);
}
