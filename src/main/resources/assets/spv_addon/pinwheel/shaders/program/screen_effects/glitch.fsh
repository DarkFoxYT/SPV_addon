#version 150

uniform sampler2D DiffuseSampler0;
in vec2 texCoord;
out vec4 fragColor;

float hash(vec2 p) {
    p = fract(p * vec2(127.1, 311.7));
    p += dot(p, p + 17.17);
    return fract(p.x * p.y);
}

void main() {
    float row = floor(texCoord.y * 144.0);
    float tear = (hash(vec2(row, 3.0)) - 0.5) * 0.006;
    vec2 uv = texCoord + vec2(tear, 0.0);
    vec3 color;
    color.r = texture(DiffuseSampler0, uv + vec2(0.002, 0.0)).r;
    color.g = texture(DiffuseSampler0, uv).g;
    color.b = texture(DiffuseSampler0, uv - vec2(0.002, 0.0)).b;
    fragColor = vec4(color, 1.0);
}
