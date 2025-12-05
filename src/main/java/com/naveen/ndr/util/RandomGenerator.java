package com.naveen.ndr.util;

import com.naveen.ndr.util.ConfigLoader;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * RandomGenerator (Advanced Version)
 * ---------------------------------
 * Centralized randomness engine for the entire NDR simulation.
 * Supports:
 *   • Seeded or non-seeded randomness (reproducible simulations)
 *   • Weighted random selection
 *   • Gaussian (normal distribution) randomness
 *   • Bias functions for more realistic disaster modeling
 *   • Jitter utilities (base ± variance)
 *   • Percent rolls, threshold decisions, and probability testing
 *
 * Why this class exists:
 *   - Removes scattered ThreadLocalRandom usage
 *   - Allows tuning randomness from config
 *   - Supports deterministic simulation when needed
 */
public final class RandomGenerator {

    private static final RandomGenerator INSTANCE = new RandomGenerator();

    private final Random seededRandom;       // used when deterministic mode enabled
    private final boolean deterministicMode;  // true if seed provided

    private RandomGenerator() {
        ConfigLoader config = new ConfigLoader();

        long seed = config.getLong("simulation.seed", 0L);
        this.deterministicMode = (seed != 0L);

        if (deterministicMode) {
            System.out.println("[RandomGenerator] Deterministic mode enabled, seed=" + seed);
            this.seededRandom = new Random(seed);
        } else {
            this.seededRandom = null;
        }
    }

    public static RandomGenerator get() {
        return INSTANCE;
    }

    private Random rng() {
        return deterministicMode ? seededRandom : ThreadLocalRandom.current();
    }

    // -------------------------------
    // Basic Random Utilities
    // -------------------------------

    public int intRange(int min, int max) {
        return rng().nextInt((max - min) + 1) + min;
    }

    public double doubleRange(double min, double max) {
        return min + (rng().nextDouble() * (max - min));
    }

    public int percent() {
        return intRange(0, 100);
    }

    public boolean chance(double probability) {
        return rng().nextDouble() < probability;
    }

    // -------------------------------
    // Jitter Utilities
    // -------------------------------

    public long jitter(long base, long variance) {
        long offset = intRange((int) -variance, (int) variance);
        long result = base + offset;
        return Math.max(1L, result);
    }

    public int jitterInt(int base, int variance) {
        return intRange(base - variance, base + variance);
    }

    // -------------------------------
    // Gaussian / Normal Random
    // -------------------------------

    /**
     * Returns a gaussian random number centered at 'mean' with the given standard deviation.
     * Useful for modeling natural disaster variations.
     */
    public double gaussian(double mean, double stdDev) {
        return mean + (rng().nextGaussian() * stdDev);
    }

    // -------------------------------
    // Weighted random selection
    // -------------------------------

    /**
     * Picks a key from a map where each entry has a weight (probability mass).
     * Example: {FLOOD=0.5, CYCLONE=0.3, WILDFIRE=0.2}
     */
    public <T> T weightedPick(Map<T, Double> weightedMap) {
        double total = weightedMap.values().stream().mapToDouble(d -> d).sum();
        double roll = rng().nextDouble() * total;

        double cumulative = 0.0;
        for (Map.Entry<T, Double> entry : weightedMap.entrySet()) {
            cumulative += entry.getValue();
            if (roll <= cumulative) {
                return entry.getKey();
            }
        }
        // fallback
        return weightedMap.keySet().iterator().next();
    }

    // -------------------------------
    // Bias functions for realism
    // -------------------------------

    /**
     * Applies an exponential bias toward higher values.
     * Useful for modeling rare but severe spikes.
     */
    public int biasHighInt(int min, int max, double biasFactor) {
        double r = Math.pow(rng().nextDouble(), biasFactor);
        return (int) (min + (r * (max - min)));
    }

    /**
     * Applies an exponential bias toward lower values.
     */
    public int biasLowInt(int min, int max, double biasFactor) {
        double r = 1 - Math.pow(rng().nextDouble(), biasFactor);
        return (int) (min + (r * (max - min)));
    }

    @Override
    public String toString() {
        return "RandomGenerator{" +
                "deterministicMode=" + deterministicMode +
                '}';
    }
}