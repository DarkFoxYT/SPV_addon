#include veil:camera
#include veil:deferred_buffers
#include veil:deferred_utils
#include veil:material

uniform sampler2D WindNoise;
uniform float GameTime;
uniform float grassHeight;

in vec3 localPos;
in vec3 normal;

float getGrassHeightGradient(float height) {
    return clamp(height / grassHeight, 0.0, 1.0);
}

void main() {
    vec3 worldPos = localPos + VeilCamera.CameraPosition;
    float grassGradient = getGrassHeightGradient(worldPos.y - 21.0);
    vec3 n = normalize(normal);
    float facing = clamp(n.y, 0.0, 1.0);
    float noiseTex = texture(WindNoise, worldPos.xz * 0.12 + vec2(GameTime * 0.015, GameTime * 0.01)).r;

    vec3 baseA = vec3(0.17, 0.26, 0.13);
    vec3 baseB = vec3(0.38, 0.56, 0.18);
    vec3 grassColor = mix(baseA, baseB, pow(grassGradient, 1.7));
    grassColor = mix(grassColor, vec3(0.45, 0.65, 0.22), 0.15 * facing);
    grassColor += 0.035 * (noiseTex - 0.5);

    float baseOcclusion = mix(0.45, 1.0, pow(grassGradient, 1.35));
    float occlusionFactor = clamp(baseOcclusion * (0.8 + 0.2 * noiseTex), 0.35, 1.0);
    grassColor *= 0.96 + 0.04 * sin(GameTime * 1.1 + worldPos.x * 0.09 + worldPos.z * 0.09);

    vec3 sunDir = normalize(vec3(0.2, 1.0, 0.3));
    float diffuse = clamp(dot(n, sunDir), 0.0, 1.0);
    grassColor *= 0.7 + 0.3 * diffuse;

    float backlight = pow(clamp(dot(n, -sunDir), 0.0, 1.0), 2.5) * 0.25;
    grassColor += backlight * vec3(0.5, 0.7, 0.3);

    vec3 viewDir = normalize(-localPos);
    vec3 halfDir = normalize(viewDir + sunDir);
    float spec = pow(max(dot(n, halfDir), 0.0), 40.0) * 0.10;
    grassColor += spec;

    fragAlbedo = vec4(grassColor * occlusionFactor, 1.0);
    fragNormal = vec4(worldToViewSpaceDirection(n), 1.0);
    fragMaterial = ivec4(15, 0, 0, 1);
    fragLightMap = vec4(1);
}
