#version 150
uniform float uEnable;
uniform float uTime;
uniform float uStrength;
uniform vec2 uScreenSize;
in vec2 vUV;
out vec4 fragColor;

float hash(vec2 p) {
    p = fract(p * vec2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

float rand(vec2 p, float t) {
    return hash(p + vec2(t, t * 0.73));
}

void main() {
    float t = (uTime > 0.0) ? uTime : mod(gl_FragCoord.x + gl_FragCoord.y, 100.0) * 0.01;
    float n = rand(vUV * uScreenSize.xy, floor(t * 60.0));
    float scanline = 0.72 + 0.28 * sin(vUV.y * uScreenSize.y * 1.7 + t * 18.0);
    float tear = smoothstep(0.84, 0.98, rand(vec2(floor(vUV.y * 48.0), floor(t * 18.0)), t));
    vec3 noiseCol = mix(vec3(n), vec3(0.35, 0.85, 1.0) * n, tear * 0.35) * scanline;
    float alpha = clamp(uEnable * (uStrength + tear * 0.12), 0.0, 0.45);
    fragColor = vec4(noiseCol, alpha);
}
