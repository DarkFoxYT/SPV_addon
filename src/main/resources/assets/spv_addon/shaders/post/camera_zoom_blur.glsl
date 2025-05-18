#version 150

uniform sampler2D DiffuseSampler;
uniform float Zoom;    // 1.0 (pas zoom) à 5.0 (zoom max)
uniform float Focus;   // 1.0 = net, 0.0 = flou

in vec2 texCoord;
out vec4 fragColor;

void main() {
    // Calcul du centre, et zoom (scale du rendu)
    vec2 center = vec2(0.5, 0.5);
    vec2 offset = texCoord - center;
    vec2 scaledCoord = center + offset / Zoom;

    // Crop (si on zoom trop, bordures noires)
    if (scaledCoord.x < 0.0 || scaledCoord.x > 1.0 || scaledCoord.y < 0.0 || scaledCoord.y > 1.0) {
        fragColor = vec4(0.0, 0.0, 0.0, 1.0); return;
    }

    // Flou de profondeur de champ simple
    float dof = 0.002 + (1.0 - Focus) * 0.010 * Zoom; // Plus on zoom, plus c'est dur de garder le focus
    vec4 sum = vec4(0.0);
    int samples = 8;
    for (int i = 0; i < samples; ++i) {
        float angle = 6.2831 * i / samples;
        vec2 d = dof * vec2(cos(angle), sin(angle));
        sum += texture(DiffuseSampler, scaledCoord + d);
    }
    sum += texture(DiffuseSampler, scaledCoord);
    fragColor = sum / float(samples + 1);

    // Vignette (optionnel)
    float vignette = smoothstep(0.85, 0.45, length(offset) * Zoom);
    fragColor.rgb *= mix(1.0, vignette, 0.35);

    // Aberration chromatique optionnel : rajoute un effet "cheap" caméra VHS
    // float chroma = 0.002 * (Zoom - 1.0);
    // fragColor.r = texture(DiffuseSampler, scaledCoord + vec2(chroma, 0)).r;
    // fragColor.b = texture(DiffuseSampler, scaledCoord - vec2(chroma, 0)).b;
}
