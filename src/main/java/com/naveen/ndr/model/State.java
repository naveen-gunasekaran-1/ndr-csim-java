package com.naveen.ndr.model;
import java.util.Collections;
import java.util.List;
import java.util.Set;
public class State {
    private final String stateId;
    private final String stateName;
    private final long totalPopulation;
    private final double areaSqKm;
    private final TerrainType terrainType;
    private int numHospitals;
    private int numMajorRoads;
    private int numRailLinks;
    private boolean hasMajorDam;
    private volatile RiskProfile baselineRiskProfile;
    private final double eventGenerationWeight;
    private final List<String> scenarioTemplates;
    private String stateAuthorityContact;

    //constructor
    public State(String stateId,
                 String stateName,
                 long totalPopulation,
                 double areaSqKm,
                 TerrainType terrainType,
                 int numHospitals,
                 int numMajorRoads,
                 int numRailLinks,
                 boolean hasMajorDam,
                 RiskProfile baselineRiskProfile,
                 double eventGenerationWeight,
                 List<String> scenarioTemplates,
                 String stateAuthorityContact) {
        // Basic validation
        if (stateId == null || stateId.isEmpty()) throw new IllegalArgumentException("stateId required");
        if (stateName == null || stateName.isEmpty()) throw new IllegalArgumentException("stateName required");
        if (totalPopulation < 0) throw new IllegalArgumentException("totalPopulation must be >= 0");
        if (areaSqKm <= 0) throw new IllegalArgumentException("areaSqKm must be > 0");
        if (eventGenerationWeight < 0) throw new IllegalArgumentException("eventGenerationWeight must be >= 0");

        this.stateId = stateId;
        this.stateName = stateName;
        this.totalPopulation = totalPopulation;
        this.areaSqKm = areaSqKm;
        this.terrainType = terrainType == null ? TerrainType.PLAIN : terrainType;
        this.numHospitals = Math.max(0, numHospitals);
        this.numMajorRoads = Math.max(0, numMajorRoads);
        this.numRailLinks = Math.max(0, numRailLinks);
        this.hasMajorDam = hasMajorDam;
        this.baselineRiskProfile = baselineRiskProfile == null ? RiskProfile.MEDIUM : baselineRiskProfile;
        this.eventGenerationWeight = eventGenerationWeight;
        this.scenarioTemplates = scenarioTemplates == null ? Collections.emptyList() : Collections.unmodifiableList(scenarioTemplates);
        this.stateAuthorityContact = stateAuthorityContact;
    }
        //Getters
        public String getStateId() { return stateId; }
        public String getStateName() { return stateName; }
        public long getTotalPopulation() { return totalPopulation; }
        public double getAreaSqKm() { return areaSqKm; }
        public TerrainType getTerrainType() { return terrainType; }
        public synchronized int getNumHospitals() { return numHospitals; }
        public synchronized int getNumMajorRoads() { return numMajorRoads; }
        public synchronized int getNumRailLinks() { return numRailLinks; }
        public synchronized boolean hasMajorDam() { return hasMajorDam; }
        public RiskProfile getBaselineRiskProfile() { return baselineRiskProfile; }
        public double getEventGenerationWeight() { return eventGenerationWeight; }
        public List<String> getScenarioTemplates() { return scenarioTemplates; }
        public synchronized String getStateAuthorityContact() { return stateAuthorityContact; }

        public double getPopulationDensity() {
            return totalPopulation / areaSqKm;
        }


        public boolean isCoastal() {
            return terrainType == TerrainType.COASTAL;
        }


        public long getEstimatedVulnerablePopulation(double percentage) {
            double pct = Math.max(0.0, Math.min(100.0, percentage));
            return (long) ((pct / 100.0) * totalPopulation);
        }

        // --------------------- Controlled mutators --------------------------
/**
 * Update infrastructure counts in a synchronized manner. Use only when necessary.
 */
        public synchronized void updateInfrastructure(int hospitals, int majorRoads, int railLinks, boolean majorDam) {
            if (hospitals >= 0) this.numHospitals = hospitals;
            if (majorRoads >= 0) this.numMajorRoads = majorRoads;
            if (railLinks >= 0) this.numRailLinks = railLinks;
            this.hasMajorDam = majorDam;
        }


/**
 * Update contact information in a thread-safe way.
 */
        public synchronized void updateContactInfo(String contact) {
            this.stateAuthorityContact = contact;
        }


/**
 * Set baseline risk profile (volatile field) — intended to be safe for infrequent updates.
 */
        public void setBaselineRiskProfile(RiskProfile profile) {
            if (profile != null) this.baselineRiskProfile = profile;
        }


// ----------------------- Utility / debug ----------------------------
        public String getSummary() {
            return String.format("%s (%s) pop=%,d area=%.1fkm2 terrain=%s hospitals=%d", stateName, stateId, totalPopulation, areaSqKm, terrainType, numHospitals);
        }


        @Override
        public String toString() {
            return "State{" +
                    "stateId='" + stateId + '\'' +
                    ", stateName='" + stateName + '\'' +
                    ", totalPopulation=" + totalPopulation +
                    ", areaSqKm=" + areaSqKm +
                    ", terrainType=" + terrainType +
                    ", numHospitals=" + numHospitals +
                    ", numMajorRoads=" + numMajorRoads +
                    ", numRailLinks=" + numRailLinks +
                    ", hasMajorDam=" + hasMajorDam +
                    ", baselineRiskProfile=" + baselineRiskProfile +
                    ", eventGenerationWeight=" + eventGenerationWeight +
                    '}';
        }


// ----------------------- Supporting enums --------------------------
        public enum TerrainType { PLAIN, HILLY, COASTAL, FOREST, MOUNTAIN }
        public enum RiskProfile { LOW, MEDIUM, HIGH }
    }