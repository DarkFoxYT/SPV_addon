#version 150

// Uniforms
uniform sampler2D DiffuseSampler;   // Couleur finale
uniform sampler2D DepthSampler;     // Buffer de profondeur
uniform float FocusDistance;        // Distance de mise au point (à ajuster)
uniform float FocusRange;           // “Range” de la netteté (plus petit = +net autour du point)
uniform float BlurStrength;         // Force max du blur hors focus

in vec2 texCoord;
out vec4 fragColor;

// Fonction basique de flou gaussien 5x5 (à améliorer pour la prod)
vec3 blur(sampler2D img, vec2 uv, float radius) {
    vec3 col = vec3(0.0);
    float tot = 0.0;
    for (int x = -2; x <= 2; x++) {
        for (int y = -2; y <= 2; y++) {
            float w = exp(-0.4*(x*x + y*y) / (radius+0.001));
            col += texture(img, uv + vec2(x, y) * 0.002 * radius).rgb * w;
            tot += w;
        }
    }
    return col / tot;
}

void main() {
    float depth = texture(DepthSampler, texCoord).r; // Plus c'est loin, plus c'est bas
    float cameraToPixel = depth * 100.0; // Multiplie par la distance max de vue du jeu
    float diff = abs(cameraToPixel - FocusDistance);

    // Entre 0 (net) et 1 (blur max)
    float focus = clamp(diff / FocusRange, 0.0, 1.0);

    // Ajuste force du flou (0 = net, 1 = max blur)
    float blurAmount = BlurStrength * focus;

    vec3 color = blur(DiffuseSampler, texCoord, blurAmount);
    fragColor = vec4(color, 1.0);
}
