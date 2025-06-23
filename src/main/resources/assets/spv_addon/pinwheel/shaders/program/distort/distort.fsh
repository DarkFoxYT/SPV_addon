#version 120

uniform sampler2D texture;
uniform float time;

varying vec2 passTexCoord;
varying float glitchOffset;

float rand(vec2 co){
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

void main() {
    float strength = 0.03 + rand(passTexCoord + time) * 0.02;
    float speed = 8.0;
    float glitch = step(0.95, fract(sin(time * 2.0 + passTexCoord.y * 10.0) * 43758.5453));
    float yJitter = glitch * (rand(vec2(time, passTexCoord.y)) - 0.5) * 0.08;
    float xJitter = glitch * (rand(vec2(passTexCoord.x, time)) - 0.5) * 0.08;

    // RGB split
    vec2 uvR = passTexCoord + vec2(strength, yJitter);
    vec2 uvG = passTexCoord + vec2(-strength, -yJitter);
    vec2 uvB = passTexCoord + vec2(xJitter, strength);

    vec4 colR = texture2D(texture, uvR);
    vec4 colG = texture2D(texture, uvG);
    vec4 colB = texture2D(texture, uvB);

    // Ajoute du bruit
    float noise = rand(passTexCoord * time) * 0.08;

    gl_FragColor = vec4(
        colR.r + noise,
        colG.g + noise,
        colB.b + noise,
        1.0
    );
}