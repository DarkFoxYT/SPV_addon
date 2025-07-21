#include veil:deferred_utils
#include veil:camera

// Sanity Effects Configuration
#define NOISE_OCTAVES 3
#define DISTORTION_FREQUENCY 2.0
#define CHROMATIC_SAMPLES 5

/**
 * Generate noise for visual effects
 */
float noise(vec2 coord) {
    return fract(sin(dot(coord, vec2(12.9898, 78.233))) * 43758.5453);
}

/**
 * Generate fractal noise
 */
float fractalNoise(vec2 coord, float time) {
    float value = 0.0;
    float amplitude = 1.0;
    float frequency = 1.0;
    
    for (int i = 0; i < NOISE_OCTAVES; i++) {
        value += amplitude * noise(coord * frequency + time);
        amplitude *= 0.5;
        frequency *= 2.0;
    }
    
    return value;
}

/**
 * Apply screen distortion effect
 */
vec2 applyDistortion(vec2 uv, float intensity, float time) {
    if (intensity <= 0.0) return uv;
    
    // Create wave-like distortion
    float distortX = sin(uv.y * DISTORTION_FREQUENCY * 3.14159 + time * 2.0) * intensity * 0.02;
    float distortY = cos(uv.x * DISTORTION_FREQUENCY * 3.14159 + time * 1.5) * intensity * 0.015;
    
    // Add noise-based distortion
    vec2 noiseCoord = uv * 10.0 + time * 0.5;
    float noiseX = (fractalNoise(noiseCoord, time) - 0.5) * intensity * 0.01;
    float noiseY = (fractalNoise(noiseCoord + vec2(100.0), time) - 0.5) * intensity * 0.01;
    
    return uv + vec2(distortX + noiseX, distortY + noiseY);
}

/**
 * Apply chromatic aberration effect
 */
vec3 applyChromaticAberration(sampler2D colorTexture, vec2 uv, float intensity) {
    if (intensity <= 0.0) {
        return texture(colorTexture, uv).rgb;
    }
    
    vec2 offset = vec2(intensity, 0.0);
    
    float r = texture(colorTexture, uv - offset).r;
    float g = texture(colorTexture, uv).g;
    float b = texture(colorTexture, uv + offset).b;
    
    return vec3(r, g, b);
}

/**
 * Apply visual noise effect
 */
vec3 applyVisualNoise(vec3 color, vec2 uv, float intensity, float time) {
    if (intensity <= 0.0) return color;
    
    // Generate noise
    float noiseValue = fractalNoise(uv * 100.0, time * 10.0);
    
    // Create flickering noise
    float flicker = sin(time * 20.0) * 0.5 + 0.5;
    noiseValue *= flicker;
    
    // Apply noise to color
    vec3 noiseColor = vec3(noiseValue);
    return mix(color, noiseColor, intensity * 0.3);
}

/**
 * Apply color shift effect
 */
vec3 applyColorShift(vec3 color, float intensity, float time) {
    if (intensity <= 0.0) return color;
    
    // Create color shifting matrix
    float shift = sin(time) * intensity;
    
    mat3 colorMatrix = mat3(
        1.0 + shift * 0.3, shift * 0.1, -shift * 0.1,
        -shift * 0.1, 1.0 - shift * 0.2, shift * 0.2,
        shift * 0.2, -shift * 0.1, 1.0 - shift * 0.1
    );
    
    return colorMatrix * color;
}

/**
 * Apply vignette effect
 */
vec3 applyVignette(vec3 color, vec2 uv, float intensity) {
    if (intensity <= 0.0) return color;
    
    // Calculate distance from center
    vec2 center = vec2(0.5, 0.5);
    float distance = length(uv - center);
    
    // Create vignette
    float vignette = 1.0 - smoothstep(0.3, 0.8, distance * (1.0 + intensity));
    
    return color * vignette;
}

/**
 * Apply desaturation effect
 */
vec3 applyDesaturation(vec3 color, float intensity) {
    if (intensity <= 0.0) return color;
    
    // Calculate luminance
    float luminance = dot(color, vec3(0.299, 0.587, 0.114));
    
    // Mix between original color and grayscale
    return mix(color, vec3(luminance), intensity);
}

/**
 * Apply pulse effect
 */
vec3 applyPulse(vec3 color, float intensity, float time, float speed) {
    if (intensity <= 0.0) return color;
    
    float pulse = sin(time * speed) * 0.5 + 0.5;
    float pulseEffect = pulse * intensity * 0.2;
    
    return color * (1.0 + pulseEffect);
}

/**
 * Apply flicker effect
 */
vec3 applyFlicker(vec3 color, float intensity, float time, float speed) {
    if (intensity <= 0.0) return color;
    
    float flicker = step(0.7, fractalNoise(vec2(time * speed), time));
    float flickerEffect = flicker * intensity * 0.5;
    
    return color * (1.0 - flickerEffect);
}

/**
 * Main sanity effects function
 */
vec4 applySanityEffects(sampler2D colorTexture, vec2 texCoord, 
                       float distortion, float noise, float colorShift, 
                       float vignette, float chromaticAberration, float desaturation,
                       float time, float pulseSpeed, float flickerSpeed) {
    
    // Apply screen distortion to UV coordinates
    vec2 distortedUV = applyDistortion(texCoord, distortion, time);
    
    // Clamp UV coordinates to prevent sampling outside texture
    distortedUV = clamp(distortedUV, 0.0, 1.0);
    
    // Sample color with chromatic aberration
    vec3 color = applyChromaticAberration(colorTexture, distortedUV, chromaticAberration);
    
    // Apply visual noise
    color = applyVisualNoise(color, texCoord, noise, time);
    
    // Apply color shift
    color = applyColorShift(color, colorShift, time);
    
    // Apply vignette
    color = applyVignette(color, texCoord, vignette);
    
    // Apply desaturation
    color = applyDesaturation(color, desaturation);
    
    // Apply pulse effect
    color = applyPulse(color, distortion, time, pulseSpeed);
    
    // Apply flicker effect
    color = applyFlicker(color, noise, time, flickerSpeed);
    
    // Ensure color values are in valid range
    color = clamp(color, 0.0, 1.0);
    
    return vec4(color, 1.0);
}
