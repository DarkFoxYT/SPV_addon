#version 150
uniform sampler2D uColor;     // frame color
uniform sampler2D uDepth;     // frame depth
uniform float uEnable;        // 0.0 or 1.0
uniform float uTime;          // optional (seconds)
in vec2 vUV;
out vec4 fragColor;

float hash(vec2 p){
    // cheap noise
    p = fract(p * vec2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

float rand(vec2 p, float t){
    return hash(p + vec2(t, t*0.73));
}

void main() {
    vec4 col   = texture(uColor, vUV);
    float depth = texture(uDepth, vUV).r;

    // Affecte uniquement la géométrie (pas le ciel) : dans MC post, ciel ~1.0
    float geom = step(0.999, 1.0 - depth); // 1 si géométrie

    // time fallback si non fourni
    float t = (uTime > 0.0) ? uTime : mod(gl_FragCoord.x + gl_FragCoord.y, 100.0) * 0.01;

    // bruit statique
    float n  = rand(vUV * 1024.0, floor(t * 60.0));

    // intensité et mix
    float strength = 0.25; // 0.0–1.0
    vec3 noiseCol = vec3(n);

    vec3 mixed = mix(col.rgb, mix(col.rgb, noiseCol, strength), uEnable * geom);
    fragColor = vec4(mixed, col.a);
}
