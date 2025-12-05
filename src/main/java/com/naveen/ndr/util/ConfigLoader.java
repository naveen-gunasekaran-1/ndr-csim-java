package com.naveen.ndr.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ConfigLoader
 * A simple configuration loader for the NDR simulation.
 * Reads key-value pairs from a classpath resource: ndr-config.properties
 * Purpose:
 *   - Centralize configuration (weights, intervals, resource counts, etc.)
 *   - Avoid hardcoding values inside rules or engine classes
 *   - Allow tuning simulation behavior without recompiling
 * Usage:
 *   ConfigLoader config = new ConfigLoader();
 *   int interval = config.getInt("state.interval.ms", 2000);
 */
public class ConfigLoader {

    private final Properties props = new Properties();
    private static final String DEFAULT_FILE = "ndr-config.properties";

    public ConfigLoader() {
        this(DEFAULT_FILE);
    }

    public ConfigLoader(String fileName) {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (in == null) {
                System.err.println("[ConfigLoader] WARNING: config file not found: " + fileName + " (using defaults)");
                return;
            }
            props.load(in);
            System.out.println("[ConfigLoader] Loaded config: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String get(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(props.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public long getLong(String key, long defaultValue) {
        try {
            return Long.parseLong(props.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public double getDouble(String key, double defaultValue) {
        try {
            return Double.parseDouble(props.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(props.getProperty(key, String.valueOf(defaultValue)));
    }

    @Override
    public String toString() {
        return "ConfigLoader{" + props + '}';
    }
}