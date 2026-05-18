#version 150

uniform sampler2D DiffuseSampler0;
uniform float SanityDistortion;
uniform float SanityNoise;
uniform float SanityColorShift;
uniform float SanityVignette;
uniform float SanityChromaticAberration;
uniform float SanityDesaturation;
uniform float SanityTime;
uniform vec2 SanityDistortionCenter;
uniform float SanityPulseSpeed;
uniform float SanityFlickerSpeed;

in vec2 texCoord;
out vec4 fragColor;

float hash(vec2 p) {
    p = fract(p * vec2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

void main() {
    vec2 centered = texCoord - SanityDistortionCenter;
    float radius = length(centered);
    float pulse = sin(SanityTime * SanityPulseSpeed + radius * 18.0) * 0.5 + 0.5;
    float warp = SanityDistortion * pulse * smoothstep(0.05, 0.85, radius);
    vec2 uv = texCoord + normalize(centered + vec2(0.0001)) * warp;

    float chroma = SanityChromaticAberration * (0.5 + pulse);
    vec3 color;
    color.r = texture(DiffuseSampler0, uv + vec2(chroma, 0.0)).r;
    color.g = texture(DiffuseSampler0, uv).g;
    color.b = texture(DiffuseSampler0, uv - vec2(chroma, 0.0)).b;

    float luminance = dot(color, vec3(0.2126, 0.7152, 0.0722));
    color = mix(color, vec3(luminance), SanityDesaturation);

    float n = hash(texCoord * 1024.0 + floor(SanityTime * SanityFlickerSpeed));
    color += (n - 0.5) * SanityNoise * 0.22;
    color.r += SanityColorShift * 0.08;
    color.gb -= SanityColorShift * 0.035;

    float vignette = smoothstep(0.82, 0.16, radius);
    color *= mix(1.0 - SanityVignette * 0.72, 1.0, vignette);

    fragColor = vec4(color, 1.0);
}
