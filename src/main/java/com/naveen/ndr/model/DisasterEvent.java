package com.naveen.ndr.model;

public class DisasterEvent {
    // Unique identifier for the event
    private String eventId;

    // Type of disaster (Flood, Cyclone, Wildfire, etc.)
    private EventType eventType;

    // Which state generated this event
    private String originState;

    // Core severity factor inputs
    private int populationAffected;
    private int infraDamageLevel;
    private int accessibilityLevel;
    private double spreadRate;
    private int cascadingRiskLevel;

    // Timestamp
    private long timestamp;

    // Final computed severity score (0–100)
    private int severityScore;

    // Priority label (Critical, High, Medium, Low) — optional
    private String priorityLevel;

    // Constructor (no logic)
    public DisasterEvent(String eventId, EventType eventType, String originState,
                         int populationAffected, int infraDamageLevel, int accessibilityLevel,
                         double spreadRate, int cascadingRiskLevel, long timestamp) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.originState = originState;
        this.populationAffected = populationAffected;
        this.infraDamageLevel = infraDamageLevel;
        this.accessibilityLevel = accessibilityLevel;
        this.spreadRate = spreadRate;
        this.cascadingRiskLevel = cascadingRiskLevel;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getEventId() { return eventId; }
    public EventType getEventType() { return eventType; }
    public String getOriginState() { return originState; }
    public int getPopulationAffected() { return populationAffected; }
    public int getInfraDamageLevel() { return infraDamageLevel; }
    public int getAccessibilityLevel() { return accessibilityLevel; }
    public double getSpreadRate() { return spreadRate; }
    public int getCascadingRiskLevel() { return cascadingRiskLevel; }
    public long getTimestamp() { return timestamp; }
    public int getSeverityScore() { return severityScore; }
    public String getPriorityLevel() { return priorityLevel; }

    public void setSeverityScore(int severityScore) { this.severityScore = severityScore; }
    public void setPriorityLevel(String priorityLevel) { this.priorityLevel = priorityLevel; }

    // Useful summary for reporting
    @Override
    public String toString() {
        return "DisasterEvent{" +
                "eventId='" + eventId + '\'' +
                ", eventType=" + eventType +
                ", originState='" + originState + '\'' +
                ", populationAffected=" + populationAffected +
                ", infraDamageLevel=" + infraDamageLevel +
                ", accessibilityLevel=" + accessibilityLevel +
                ", spreadRate=" + spreadRate +
                ", cascadingRiskLevel=" + cascadingRiskLevel +
                ", timestamp=" + timestamp +
                ", severityScore=" + severityScore +
                ", priorityLevel='" + priorityLevel + '\'' +
                '}';
    }
}

