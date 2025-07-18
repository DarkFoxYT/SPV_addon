#include veil:material
#include veil:deferred_buffers
#include veil:camera

uniform sampler2D u_Scene;  // Scene color buffer
uniform vec2 ScreenSize;
uniform vec2 LightPos;      // Normalized [0,1]
uniform float Exposure;
uniform float Decay;
uniform float Density;
uniform float Weight;

void main() {
    vec2 uv = gl_FragCoord.xy / ScreenSize;
    vec2 deltaTexCoord = (LightPos - uv) * Density;
    vec2 coord = uv;
    float illuminationDecay = 1.0;
    vec4 color = vec4(0.0);

    for (int i = 0; i < 100; i++) {
        coord += deltaTexCoord;
        vec4 sample = texture(u_Scene, coord);
    sample *= illuminationDecay * Weight;
color += sample;
illuminationDecay *= Decay;
}

fragAlbedo = vec4(color.rgb * Exposure, 1.0);
fragNormal = vec4(0.0, 0.0, 1.0, 1.0);
fragMaterial = ivec4(15, 0, 0, 1);
fragLightSampler = vec4(1.0);
fragLightMap = vec4(10.0);
}
