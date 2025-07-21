#include veil:material
#include veil:deferred_utils
#include spv_addon:sanity_effects

// Input textures
uniform sampler2D DiffuseSampler0;
uniform sampler2D DepthSampler;

// Sanity effect uniforms
uniform float SanityDistortion;
uniform float SanityNoise;
uniform float SanityColorShift;
uniform float SanityVignette;
uniform float SanityChromaticAberration;
uniform float SanityDesaturation;
uniform float SanityTime;
uniform vec2 SanityDistortionCenter;
uniform float SanityPulseSpeed;
uniform float SanityFlickerSpeed;

// Screen uniforms
uniform vec2 resolution;
uniform float GameTime;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    // Sample original color
    vec4 originalColor = texture(DiffuseSampler0, texCoord);
    
    // Sample depth to avoid affecting sky
    float depth = texture(DepthSampler, texCoord).r;
    
    // Apply sanity effects only to rendered geometry (not sky)
    if (depth < 1.0) {
        // Apply all sanity effects
        vec4 effectColor = applySanityEffects(
            DiffuseSampler0,
            texCoord,
            SanityDistortion,
            SanityNoise,
            SanityColorShift,
            SanityVignette,
            SanityChromaticAberration,
            SanityDesaturation,
            SanityTime,
            SanityPulseSpeed,
            SanityFlickerSpeed
        );
        
        fragColor = effectColor;
    } else {
        // Keep sky unchanged
        fragColor = originalColor;
    }
}
