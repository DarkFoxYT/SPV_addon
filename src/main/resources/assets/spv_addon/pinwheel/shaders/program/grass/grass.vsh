#version 460
#include veil:camera

layout(location = 0) in vec3 Position;
layout(location = 1) in vec3 Normal;

layout (std430, binding = 0) buffer MyBuffer {
    vec3 position[];
} myBuffer;

uniform float GameTime;
uniform sampler2D WindNoise;
uniform float density;
uniform float grassHeight;

float hash12(vec2 p) {
    vec3 p3 = fract(vec3(p.xyx) * 0.1031);
    p3 += dot(p3, p3.yzx + 33.33);
    return fract((p3.x + p3.y) * p3.z);
}

mat2 rot2D(float angle) {
    float rad = radians(angle);
    float s = sin(rad);
    float c = cos(rad);
    return mat2(c, -s, s, c);
}

float getGrassHeightGradient(float height) {
    return height / grassHeight;
}

out vec3 localPos;
out vec3 normal;

void main() {
    vec3 pos = Position;

    vec3 cameraPos = VeilCamera.CameraPosition;
    cameraPos.xz = mod(cameraPos.xz, 1.0);

    vec3 offset = myBuffer.position[gl_InstanceID];
    vec3 worldOffset = offset + floor(VeilCamera.CameraPosition);
    float rand = hash12(worldOffset.xz);

    vec3 tempNormal = Normal;
    mat2 randRot = rot2D(rand * 360.0);
    pos.xz *= randRot;
    tempNormal.xz *= randRot;

    localPos = (pos - cameraPos) + offset;

    float windStrength = 0.25;
    vec3 worldPos = localPos + VeilCamera.CameraPosition;
    float grassGradient = getGrassHeightGradient(pos.y);
    float windtexture = texture(WindNoise, worldPos.xz * 0.03 + vec2(GameTime * 100.0 + rand * 0.1)).r - 0.3;
    float heightTexture = texture(WindNoise, worldOffset.xz * 0.065 + rand * 0.07).r;
    heightTexture = mix(0.3, 1.1, heightTexture);

    localPos.y += heightTexture * grassGradient;
    localPos.xz -= 2.0 * (grassGradient * grassGradient) * (windtexture * windStrength) * (grassHeight + heightTexture);
    tempNormal.y = 2.0 * (grassGradient * grassGradient) * (windtexture * windStrength) * (grassHeight + heightTexture);

    normal = tempNormal;
    gl_Position = VeilCamera.ProjMat * VeilCamera.ViewMat * vec4(localPos, 1.0);
}
