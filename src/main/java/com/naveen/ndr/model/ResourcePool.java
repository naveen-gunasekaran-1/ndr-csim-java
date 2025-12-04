package com.naveen.ndr.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ResourcePool {
    private int ndrfUnitsAvailable;
    private int trucksAvailable;
    private int boatsAvailable;
    private int medicalTeamsAvailable;
    private int helicoptersAvailable;

    /*
    Constructor
    -this initializes all the resources
     */
    public ResourcePool(int ndrfUnitsAvailable,
                        int trucksAvailable,
                        int boatsAvailable,
                        int helicoptersAvailable,
                        int medicalTeamsAvailable) {
        this.ndrfUnitsAvailable = Math.max(0, ndrfUnitsAvailable);
        this.trucksAvailable = Math.max(0, trucksAvailable);
        this.boatsAvailable = Math.max(0, boatsAvailable);
        this.helicoptersAvailable = Math.max(0, helicoptersAvailable);
        this.medicalTeamsAvailable = Math.max(0, medicalTeamsAvailable);
    }

    // ------------------------- Thread‑safe Getters -------------------------
    public synchronized int getHelicoptersAvailable() {
        return helicoptersAvailable;
    }

    public synchronized int getNdrfUnitsAvailable() {
        return ndrfUnitsAvailable;
    }

    public synchronized int getMedicalTeamsAvailable() {
        return medicalTeamsAvailable;
    }

    public synchronized int getBoatsAvailable() {
        return boatsAvailable;
    }

    public synchronized int getTrucksAvailable() {
        return trucksAvailable;
    }
    //why they are thread-safe because they won't allow multiple threads to access them at the same time


    /*
   Methods to allocate the resources and update the resource pool according to the availability of the resources
     */
    public synchronized boolean AllocateResources(Map<String, Integer> request) {
        for (Map.Entry<String, Integer> e : request.entrySet()) {
            String key = e.getKey();
            int value = e.getValue();
            if (value < 0) return false;
            switch (key.toLowerCase()) {
                case "ndrf":
                    if (ndrfUnitsAvailable < value) return false;
                    break;
                case "trucks":
                    if (trucksAvailable < value) return false;
                    break;
                case "boats":
                    if (boatsAvailable < value) return false;
                    break;
                case "medical_teams":
                    if (medicalTeamsAvailable < value) return false;
                    break;
                case "helicopters":
                    if (helicoptersAvailable < value) return false;
                    break;
                default:
                    return false;
            }
        }
        for (Map.Entry<String, Integer> e : request.entrySet()) {
            String key = e.getKey();
            int value = e.getValue();
            if (value < 0) return false;
            switch (key.toLowerCase()) {
                case "ndrf":
                    ndrfUnitsAvailable -= value;
                    break;
                case "trucks":
                    trucksAvailable -= value;
                    break;
                case "boats":
                    boatsAvailable -= value;
                    break;
                case "medical_teams":
                    medicalTeamsAvailable -= value;
                    break;
                case "helicopters":
                    helicoptersAvailable -= value;
                    break;
                default:
                    return false;
            }
        }
        return true;

    }
// Release

    /**
     * Releases resources back to the pool.
     */
    public synchronized void releaseResources(Map<String, Integer> request) {
        for (Map.Entry<String, Integer> e : request.entrySet()) {
            String key = e.getKey();
            int value = e.getValue();
            switch (key.toLowerCase()) {
                case "ndrf":
                    ndrfUnitsAvailable += value;
                    break;
                case "trucks":
                    trucksAvailable += value;
                    break;
                case "boats":
                    boatsAvailable += value;
                    break;
                case "medical_teams":
                    medicalTeamsAvailable += value;
                    break;
                case "helicopters":
                    helicoptersAvailable += value;
                    break;
            }
        }

    }

    /**
     * Returns a read‑only snapshot of all resource counts.
     */
    public synchronized Map<String, Integer> snapshot() {
        Map<String, Integer> map = new HashMap<>();
        map.put("helicopters", helicoptersAvailable);
        map.put("ndrfUnits", ndrfUnitsAvailable);
        map.put("medicalTeams", medicalTeamsAvailable);
        map.put("boats", boatsAvailable);
        map.put("trucks", trucksAvailable);
        return Collections.unmodifiableMap(map);
    }


    // ----------------------------- Summary ----------------------------------
    public synchronized String getSummary() {
        return String.format(
                "Resources → helicopters=%d, ndrf=%d, medical=%d, boats=%d, trucks=%d",
                helicoptersAvailable, ndrfUnitsAvailable, medicalTeamsAvailable,
                boatsAvailable, trucksAvailable
        );


    }
}