package net.dark.spv_addon.entities.ai.procedural;

public record PredatorBrainConfig(
        double senseRange,
        float minimumHearingActivity,
        int repathIntervalTicks,
        int searchDurationTicks,
        double patrolSpeed,
        double investigateSpeed,
        double chaseSpeed,
        int patrolRadius
) {
    public static PredatorBrainConfig defaultPredator() {
        return new PredatorBrainConfig(
                28.0,
                0.10f,
                8,
                80,
                0.45,
                0.65,
                0.85,
                10
        );
    }
}

