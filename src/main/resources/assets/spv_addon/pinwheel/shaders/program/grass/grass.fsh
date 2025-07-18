#include veil:camera
#include veil:deferred_buffers
#include veil:deferred_utils
#include veil:material

uniform sampler2D WindNoise;
uniform float GameTime;
uniform float grassHeight;

in vec3 localPos;
in vec3 normal;

float getGrassHeightGradient(float height){
    return clamp(height / grassHeight, 0.0, 1.0);
}

void main() {
    vec3 worldPos = localPos + VeilCamera.CameraPosition;
    float grassGradient = getGrassHeightGradient(worldPos.y - 21.0);

    // Couleur de base : plus de nuances, variation selon la hauteur et l’orientation
    vec3 baseA = vec3(0.17, 0.26, 0.13);
    vec3 baseB = vec3(0.38, 0.56, 0.18);
    float facing = clamp(dot(normalize(normal), normalize(vec3(0.0, 1.0, 0.0))), 0.0, 1.0);
    vec3 grassColor = mix(baseA, baseB, pow(grassGradient, 1.7));
    grassColor = mix(grassColor, vec3(0.45, 0.65, 0.22), 0.15 * facing);

    // Variation subtile par bruit
    float colorNoise = texture(WindNoise, worldPos.xz * 0.13 + GameTime * 0.01).r;
    grassColor += 0.04 * (colorNoise - 0.5);

    // Occlusion ambiante : plus sombre à la base et entre les brins
    float aoNoise = texture(WindNoise, worldPos.xz * 0.09 + GameTime * 0.025).r;
    float baseOcclusion = mix(0.45, 1.0, pow(grassGradient, 1.5));
    float occlusionFactor = clamp(baseOcclusion * (0.7 + 0.3 * aoNoise), 0.32, 1.0);

    // Effet de lumière pulsante très léger
    float pulse = 0.93 + 0.07 * sin(GameTime * 1.2 + worldPos.x * 0.13 + worldPos.z * 0.13);
    grassColor *= pulse;

    // Éclairage directionnel réaliste
    vec3 sunDir = normalize(vec3(0.2, 1.0, 0.3));
    float diffuse = clamp(dot(normalize(normal), sunDir), 0.0, 1.0);
    grassColor *= 0.7 + 0.3 * diffuse;

    // Translucidité (backlight) : effet de lumière traversant les brins
    float backlight = pow(clamp(dot(normalize(normal), -sunDir), 0.0, 1.0), 2.5) * 0.25;
    grassColor += backlight * vec3(0.5, 0.7, 0.3);

    // Brillance rosée du matin
    vec3 viewDir = normalize(-localPos);
    vec3 halfDir = normalize(viewDir + sunDir);
    float spec = pow(max(dot(normalize(normal), halfDir), 0.0), 48.0) * 0.13;
    grassColor += spec;

    fragAlbedo = vec4(grassColor * occlusionFactor, 1.0);
    fragNormal = vec4(worldToViewSpaceDirection(normal), 1.0);
    fragMaterial = ivec4(15, 0, 0, 1);
    fragLightMap = vec4(1);
}