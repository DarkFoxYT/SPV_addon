uniform sampler2D Diffuse;    // image du rendu
uniform sampler2D BokehMap;   // bokeh ou noise ou une de tes textures (ex: clouds1.png)
uniform sampler2D Noise;      // autre noise si besoin

uniform float FocusDistance; // ce que tu envoies côté code (target)
uniform float FocusRange;    // largeur nette
uniform float BlurStrength;  // intensité max du flou

in vec2 texCoord;
out vec4 fragColor;

// Simple blur kernel (pour l’exemple, tu peux améliorer)
vec4 blur(sampler2D img, vec2 uv, float strength) {
float total = 0.0;
vec4 color = vec4(0.0);
int samples = 8;
for (int i = 0; i < samples; i++) {
float angle = 6.2831853 * float(i) / float(samples);
vec2 offset = vec2(cos(angle), sin(angle)) * 0.002 * strength;
color += texture(img, uv + offset);
total += 1.0;
}
return color / total;
}

void main() {
// Simule la distance focale en fonction du centre
vec2 center = vec2(0.5, 0.5);
float dist = length(texCoord - center);

// Utilise une noise map pour moduler le bokeh
float bokeh = texture(BokehMap, texCoord * 2.0).r; // remplit selon ta texture

// Décide la netteté selon la distance au centre
float focus = smoothstep(FocusDistance - FocusRange, FocusDistance + FocusRange, dist);

float blurAmt = mix(0.0, BlurStrength, focus);
blurAmt *= mix(0.9, 1.15, bokeh); // bokeh/noise influence le blur

vec4 baseColor = texture(Diffuse, texCoord);
vec4 blurred = blur(Diffuse, texCoord, blurAmt);

fragColor = mix(baseColor, blurred, blurAmt);
}
