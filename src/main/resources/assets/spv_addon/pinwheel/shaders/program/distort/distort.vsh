#version 120

attribute vec2 inPosition;
attribute vec2 inTexCoord0;

varying vec2 passTexCoord;
varying float glitchOffset;

uniform float time;

float rand(vec2 co){
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

void main() {
    gl_Position = vec4(inPosition, 0.0, 1.0);
    passTexCoord = inTexCoord0;
    glitchOffset = rand(inTexCoord0 + time);
}