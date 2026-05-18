#version 150

uniform sampler2D DiffuseSampler0;
in vec2 texCoord;
out vec4 fragColor;

float hash(vec2 p) {
    p = fract(p * vec2(269.5, 183.3));
    p += dot(p, p + 41.13);
    return fract(p.x * p.y);
}

void main() {
    vec4 base = texture(DiffuseSampler0, texCoord);
    float n = hash(texCoord * 900.0);
    float scan = 0.96 + 0.04 * sin(texCoord.y * 900.0);
    fragColor = vec4(mix(base.rgb, vec3(n) * scan, 0.055), base.a);
}
