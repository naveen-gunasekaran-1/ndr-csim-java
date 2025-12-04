package com.naveen.ndr.model;

import jdk.jfr.EventType;

public class DisasterEvent {
    //defining all the data need to identify a disaster
    private EventType eventType;
    private String eventId;
    private String originState;
    private int serverityLevel;
    private long timeStamp;
    private int populationAffected;
    private int infraDamageLevel;
    private int accessibilityLevel;
    private double spreadRate;
    private int cascadingRiskLevel;
    private String priorityLevel;
    private int severityScore;
    //construtor
    public DisasterEvent(String eventId, String originState, int serverityLevel, long timeStamp, int populationAffected,
                         int infraDamageLevel,int cascadingRiskLevel, int accessibilityLevel, double spreadRate, String priorityLevel, int severityScore, EventType eventType)
        {
        this.eventId = eventId;
        this.originState = originState;
        this.serverityLevel = serverityLevel;
        this.timeStamp = timeStamp;
        this.populationAffected = populationAffected;
        this.infraDamageLevel = infraDamageLevel;
        this.accessibilityLevel = accessibilityLevel;
        this.spreadRate = spreadRate;
        this.priorityLevel = priorityLevel;
        this.severityScore = severityScore;
        this.eventType = eventType;
        this.cascadingRiskLevel = cascadingRiskLevel;
        this.priorityLevel = priorityLevel;
        this.severityScore = severityScore;
        this.eventType = eventType;
        }
}
//getters and setters
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