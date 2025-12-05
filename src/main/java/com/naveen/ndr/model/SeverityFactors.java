package com.naveen.ndr.model;

/**
 * SeverityFactor
 *
 * A simple immutable data holder representing the five core
 * disaster-severity inputs (P, I, A, T, C). This class does NOT compute
 * severity — it only standardizes how the inputs are represented.
 * Rules (FloodSeverityRule, WildfireSeverityRule, etc.) may use or
 * convert these values.
 *
 * Fields:
 *   P = population affected (0–∞)
 *   I = infrastructure damage % (0–100)
 *   A = accessibility difficulty % (0–100)
 *   T = spread rate (type-dependent units)
 *   C = cascading risk % (0–100)
 */
public final class SeverityFactors {

    private final int populationAffected;   // P
    private final int infraDamageLevel;     // I
    private final int accessibilityLevel;   // A
    private final double spreadRate;        // T
    private final int cascadingRiskLevel;   // C

    public SeverityFactors(int populationAffected,
                          int infraDamageLevel,
                          int accessibilityLevel,
                          double spreadRate,
                          int cascadingRiskLevel) {
        this.populationAffected = Math.max(0, populationAffected);
        this.infraDamageLevel = clampPercent(infraDamageLevel);
        this.accessibilityLevel = clampPercent(accessibilityLevel);
        this.spreadRate = Math.max(0.0, spreadRate);
        this.cascadingRiskLevel = clampPercent(cascadingRiskLevel);
    }

    private int clampPercent(int v) {
        if (v < 0) return 0;
        if (v > 100) return 100;
        return v;
    }

    public int getPopulationAffected() {
        return populationAffected;
    }

    public int getInfraDamageLevel() {
        return infraDamageLevel;
    }

    public int getAccessibilityLevel() {
        return accessibilityLevel;
    }

    public double getSpreadRate() {
        return spreadRate;
    }

    public int getCascadingRiskLevel() {
        return cascadingRiskLevel;
    }

    @Override
    public String toString() {
        return "SeverityFactor{" +
                "P=" + populationAffected +
                ", I=" + infraDamageLevel +
                ", A=" + accessibilityLevel +
                ", T=" + spreadRate +
                ", C=" + cascadingRiskLevel +
                '}';
    }
}