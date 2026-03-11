#version 150
uniform sampler2D uColor;
uniform sampler2D uDepth;
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
    float depth = texture(uDepth, vUV).r;
    float geoMask = step(0.999, 1.0 - depth);

    vec2 uv = vUV;
    float line = sin((uv.y * uScreenSize.y * 0.05) + uTime * 11.0);
    float offset = line * uDistortionStrength;
    uv.x += offset;
    uv.y += (noise(uv * 28.0 + uTime) - 0.5) * uDistortionStrength * 0.75;

    vec3 base = texture(uColor, uv).rgb;

    float chroma = (noise(uv * 64.0 + uTime * 0.9) - 0.5) * uChromaStrength;
    float r = texture(uColor, uv + vec2(chroma, 0.0)).r;
    float g = texture(uColor, uv).g;
    float b = texture(uColor, uv - vec2(chroma, 0.0)).b;

    vec3 glitched = vec3(r, g, b);
    float scan = 1.0 - uFlashStrength + uFlashStrength * sin((uv.y + uTime * 0.2) * uScreenSize.y * 0.6);
    float speckle = (noise(uv * uNoiseScale + uTime * 7.0) - 0.5) * (0.08 + uChaos * 0.22);
    vec3 colorWarp = vec3(
        0.85 + 0.25 * sin(uTime * 1.7 + uv.x * 20.0),
        0.80 + 0.30 * cos(uTime * 1.3 + uv.y * 22.0),
        0.90 + 0.20 * sin(uTime * 2.0 + (uv.x + uv.y) * 15.0)
    );

    float blend = geoMask * clamp(uChaos * 1.2, 0.0, 1.0);
    vec3 finalColor = mix(base, glitched * colorWarp * scan + speckle, blend);
    fragColor = vec4(finalColor, 1.0);
}
