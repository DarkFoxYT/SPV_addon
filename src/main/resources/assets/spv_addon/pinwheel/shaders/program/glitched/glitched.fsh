#version 150
uniform float uTime;
uniform float uChaos;
uniform float uDistortionStrength;
uniform float uChromaStrength;
uniform float uFlashStrength;
uniform float uNoiseScale;
uniform vec2 uScreenSize;
in vec2 vUV;
out vec4 fragColor;

float hash(vec2 p) {
    p = fract(p * vec2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    float a = hash(i);
    float b = hash(i + vec2(1.0, 0.0));
    float c = hash(i + vec2(0.0, 1.0));
    float d = hash(i + vec2(1.0, 1.0));
    vec2 u = f * f * (3.0 - 2.0 * f);
    return mix(a, b, u.x) + (c - a) * u.y * (1.0 - u.x) + (d - b) * u.x * u.y;
}

void main() {
    vec2 uv = vUV;
    float line = sin((uv.y * uScreenSize.y * 0.05) + uTime * 11.0);
    float offset = line * uDistortionStrength;
    uv.x += offset;
    uv.y += (noise(uv * 28.0 + uTime) - 0.5) * uDistortionStrength * 0.75;

    float chroma = (noise(uv * 64.0 + uTime * 0.9) - 0.5) * uChromaStrength;
    vec3 glitched = vec3(
        0.45 + 0.55 * noise((uv + vec2(chroma, 0.0)) * uNoiseScale + uTime * 9.0),
        0.35 + 0.45 * noise(uv * (uNoiseScale * 0.75) - uTime * 6.0),
        0.55 + 0.40 * noise((uv - vec2(chroma, 0.0)) * (uNoiseScale * 1.15) + uTime * 3.0)
    );

    float scan = 1.0 - uFlashStrength + uFlashStrength * sin((uv.y + uTime * 0.2) * uScreenSize.y * 0.6);
    float speckle = (noise(uv * uNoiseScale + uTime * 7.0) - 0.5) * (0.08 + uChaos * 0.22);
    vec3 colorWarp = vec3(
        0.85 + 0.25 * sin(uTime * 1.7 + uv.x * 20.0),
        0.80 + 0.30 * cos(uTime * 1.3 + uv.y * 22.0),
        0.90 + 0.20 * sin(uTime * 2.0 + (uv.x + uv.y) * 15.0)
    );

    float band = smoothstep(0.62, 0.96, noise(vec2(floor(uv.y * 90.0), floor(uTime * 24.0))));
    float alpha = clamp(0.035 + uChaos * 0.18 + band * uChaos * 0.10, 0.0, 0.32);
    vec3 finalColor = glitched * colorWarp * scan + speckle;
    fragColor = vec4(finalColor, alpha);
}
