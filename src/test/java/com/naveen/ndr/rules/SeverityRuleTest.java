package com.naveen.ndr.rules;

import com.naveen.ndr.model.DisasterEvent;
import com.naveen.ndr.model.EventType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * SeverityRuleTest
 * Comprehensive unit tests for all severity rule implementations.
 * Tests each rule with various event configurations to ensure
 * proper severity calculation behavior.
 */
public class SeverityRuleTest {

    // ==================== FLOOD SEVERITY RULE TESTS ====================

    @Test
    public void testFloodSeverityRule_LowSeverity() {
        FloodSeverityRule rule = new FloodSeverityRule();
        DisasterEvent event = new DisasterEvent(
                "FLOOD-001",
                EventType.FLOOD,
                "TestState",
                50,        // low population
                10,        // low infrastructure damage
                20,        // good accessibility
                0.5,       // slow spread
                10,        // low cascading risk
                System.currentTimeMillis()
        );

        int severity = rule.calculateSeverity(event);
        assertTrue(severity >= 0 && severity <= 100, "Severity must be in range 0-100");
        assertTrue(severity < 40, "Expected low severity for minimal impact flood");
    }

    @Test
    public void testFloodSeverityRule_HighSeverity() {
        FloodSeverityRule rule = new FloodSeverityRule();
        DisasterEvent event = new DisasterEvent(
                "FLOOD-002",
                EventType.FLOOD,
                "TestState",
                50000,     // high population
                80,        // high infrastructure damage
                70,        // poor accessibility
                6.0,       // fast spread
                85,        // high cascading risk
                System.currentTimeMillis()
        );

        int severity = rule.calculateSeverity(event);
        assertTrue(severity >= 0 && severity <= 100, "Severity must be in range 0-100");
        assertTrue(severity > 70, "Expected high severity for major flood");
    }

    @Test
    public void testFloodSeverityRule_NullEvent() {
        FloodSeverityRule rule = new FloodSeverityRule();
        assertThrows(IllegalArgumentException.class, () -> rule.calculateSeverity(null));
    }

    // ==================== CYCLONE SEVERITY RULE TESTS ====================

    @Test
    public void testCycloneSeverityRule_LowSeverity() {
        CycloneSeverityRule rule = new CycloneSeverityRule();
        DisasterEvent event = new DisasterEvent(
                "CYCLONE-001",
                EventType.CYCLONE,
                "TestState",
                100,       // low population
                15,        // low infrastructure damage
                25,        // good accessibility
                1.0,       // slow spread
                15,        // low cascading risk
                System.currentTimeMillis()
        );

        int severity = rule.calculateSeverity(event);
        assertTrue(severity >= 0 && severity <= 100, "Severity must be in range 0-100");
        assertTrue(severity < 40, "Expected low severity for weak cyclone");
    }

    @Test
    public void testCycloneSeverityRule_HighSeverity() {
        CycloneSeverityRule rule = new CycloneSeverityRule();
        DisasterEvent event = new DisasterEvent(
                "CYCLONE-002",
                EventType.CYCLONE,
                "TestState",
                80000,     // high population
                90,        // high infrastructure damage
                80,        // poor accessibility
                7.0,       // fast spread
                90,        // high cascading risk
                System.currentTimeMillis()
        );

        int severity = rule.calculateSeverity(event);
        assertTrue(severity >= 0 && severity <= 100, "Severity must be in range 0-100");
        assertTrue(severity > 75, "Expected high severity for major cyclone");
    }

    @Test
    public void testCycloneSeverityRule_NullEvent() {
        CycloneSeverityRule rule = new CycloneSeverityRule();
        assertThrows(IllegalArgumentException.class, () -> rule.calculateSeverity(null));
    }

    // ==================== WILDFIRE SEVERITY RULE TESTS ====================

    @Test
    public void testWildfireSeverityRule_LowSeverity() {
        WildfireSeverityRule rule = new WildfireSeverityRule();
        DisasterEvent event = new DisasterEvent(
                "WILDFIRE-001",
                EventType.WILDFIRE,
                "TestState",
                20,        // very low population
                5,         // minimal infrastructure damage
                30,        // moderate accessibility
                1.0,       // slow spread (ha/hour)
                10,        // low cascading risk
                System.currentTimeMillis()
        );

        int severity = rule.calculateSeverity(event);
        assertTrue(severity >= 0 && severity <= 100, "Severity must be in range 0-100");
        assertTrue(severity < 35, "Expected low severity for contained wildfire");
    }

    @Test
    public void testWildfireSeverityRule_HighSeverity() {
        WildfireSeverityRule rule = new WildfireSeverityRule();
        DisasterEvent event = new DisasterEvent(
                "WILDFIRE-002",
                EventType.WILDFIRE,
                "TestState",
                15000,     // high population
                70,        // high infrastructure damage
                75,        // poor accessibility (mountainous)
                55.0,      // extreme spread rate
                80,        // high cascading risk (smoke, air quality)
                System.currentTimeMillis()
        );

        int severity = rule.calculateSeverity(event);
        assertTrue(severity >= 0 && severity <= 100, "Severity must be in range 0-100");
        assertTrue(severity > 80, "Expected very high severity for extreme wildfire");
    }

    @Test
    public void testWildfireSeverityRule_NullEvent() {
        WildfireSeverityRule rule = new WildfireSeverityRule();
        assertThrows(IllegalArgumentException.class, () -> rule.calculateSeverity(null));
    }

    // ==================== LANDSLIDE SEVERITY RULE TESTS ====================

    @Test
    public void testLandslideSeverityRule_LowSeverity() {
        LandslideSeverityRule rule = new LandslideSeverityRule();
        DisasterEvent event = new DisasterEvent(
                "LANDSLIDE-001",
                EventType.LANDSLIDE,
                "TestState",
                30,        // low population
                20,        // low infrastructure damage
                40,        // moderate accessibility
                0.5,       // slow spread
                15,        // low cascading risk
                System.currentTimeMillis()
        );

        int severity = rule.calculateSeverity(event);
        assertTrue(severity >= 0 && severity <= 100, "Severity must be in range 0-100");
        assertTrue(severity < 40, "Expected low severity for minor landslide");
    }

    @Test
    public void testLandslideSeverityRule_HighSeverity() {
        LandslideSeverityRule rule = new LandslideSeverityRule();
        DisasterEvent event = new DisasterEvent(
                "LANDSLIDE-002",
                EventType.LANDSLIDE,
                "TestState",
                60000,     // high population
                85,        // high infrastructure damage
                90,        // very poor accessibility
                3.5,       // fast spread
                85,        // high cascading risk
                System.currentTimeMillis()
        );

        int severity = rule.calculateSeverity(event);
        assertTrue(severity >= 0 && severity <= 100, "Severity must be in range 0-100");
        assertTrue(severity > 75, "Expected high severity for major landslide");
    }

    @Test
    public void testLandslideSeverityRule_NullEvent() {
        LandslideSeverityRule rule = new LandslideSeverityRule();
        assertThrows(IllegalArgumentException.class, () -> rule.calculateSeverity(null));
    }

    // ==================== BOUNDARY VALUE TESTS ====================

    @Test
    public void testSeverityRule_ZeroValues() {
        FloodSeverityRule rule = new FloodSeverityRule();
        DisasterEvent event = new DisasterEvent(
                "ZERO-001",
                EventType.FLOOD,
                "TestState",
                0,         // zero population
                0,         // zero infrastructure damage
                0,         // zero accessibility
                0.0,       // zero spread
                0,         // zero cascading risk
                System.currentTimeMillis()
        );

        int severity = rule.calculateSeverity(event);
        assertTrue(severity >= 0, "Severity must be non-negative");
        assertTrue(severity < 20, "Expected very low severity for zero-impact event");
    }

    @Test
    public void testSeverityRule_MaxValues() {
        FloodSeverityRule rule = new FloodSeverityRule();
        DisasterEvent event = new DisasterEvent(
                "MAX-001",
                EventType.FLOOD,
                "TestState",
                1000000,   // very high population
                100,       // max infrastructure damage
                100,       // worst accessibility
                10.0,      // very fast spread
                100,       // max cascading risk
                System.currentTimeMillis()
        );

        int severity = rule.calculateSeverity(event);
        assertTrue(severity >= 0 && severity <= 100, "Severity must be clamped to 0-100");
        assertTrue(severity > 85, "Expected very high severity for max-impact event");
    }

    // ==================== CONSISTENCY TESTS ====================

    @Test
    public void testSeverityRule_Consistency() {
        FloodSeverityRule rule = new FloodSeverityRule();
        DisasterEvent event = new DisasterEvent(
                "CONSISTENCY-001",
                EventType.FLOOD,
                "TestState",
                5000,
                50,
                50,
                3.0,
                50,
                System.currentTimeMillis()
        );

        // Calculate severity multiple times - should be deterministic
        int severity1 = rule.calculateSeverity(event);
        int severity2 = rule.calculateSeverity(event);
        int severity3 = rule.calculateSeverity(event);

        assertEquals(severity1, severity2, "Severity calculation should be deterministic");
        assertEquals(severity2, severity3, "Severity calculation should be deterministic");
    }

    // ==================== RULE NAME TESTS ====================

    @Test
    public void testRuleName() {
        FloodSeverityRule floodRule = new FloodSeverityRule();
        CycloneSeverityRule cycloneRule = new CycloneSeverityRule();
        WildfireSeverityRule wildfireRule = new WildfireSeverityRule();
        LandslideSeverityRule landslideRule = new LandslideSeverityRule();

        assertNotNull(floodRule.name());
        assertNotNull(cycloneRule.name());
        assertNotNull(wildfireRule.name());
        assertNotNull(landslideRule.name());

        assertTrue(floodRule.name().contains("Flood"));
        assertTrue(cycloneRule.name().contains("Cyclone"));
        assertTrue(wildfireRule.name().contains("Wildfire"));
        assertTrue(landslideRule.name().contains("Landslide"));
    }
}
