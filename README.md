# NDR-CSIM: National Disaster Response Computer Simulation

A comprehensive Java-based simulation of India's National Disaster Response Framework (NDRF), modeling disaster event generation, severity calculation, resource allocation, and coordinated response across multiple states.

## 🎯 Project Overview

NDR-CSIM simulates the complex dynamics of national disaster response by modeling:
- **Multi-state disaster event generation** based on terrain, risk profiles, and historical patterns
- **Type-specific severity calculation** using weighted rule-based algorithms
- **Priority-based resource allocation** from a shared national pool
- **Real-time monitoring and reporting** of system status

## 🏗️ Architecture

### Core Components

#### 1. **Engine Package** (`com.naveen.ndr.engine`)
- `NationalCoordinator`: Orchestrates the entire simulation lifecycle
- `Dispatcher`: Manages resource allocation and response operations
- `SeverityCalculator`: Aggregates multiple severity rules to compute disaster severity
- `EventQueueManager`: Manages producer and dispatch priority queues

#### 2. **Model Package** (`com.naveen.ndr.model`)
- `DisasterEvent`: Core event entity with severity factors
- `State`: Represents geographic regions with infrastructure and risk profiles
- `ResourcePool`: Thread-safe national resource inventory
- `EventType`: Enum for disaster types (FLOOD, CYCLONE, WILDFIRE, EARTHQUAKE, LANDSLIDE, INDUSTRIAL)

#### 3. **Rules Package** (`com.naveen.ndr.rules`)
Severity calculation rules implementing the `SeverityRule` interface:
- `FloodSeverityRule`: Flood-specific severity calculation
- `CycloneSeverityRule`: Cyclone-specific severity calculation
- `WildfireSeverityRule`: Wildfire-specific severity calculation
- `LandslideSeverityRule`: Landslide-specific severity calculation

Each rule uses weighted factors (P, I, A, T, C):
- **P**: Population affected (0-∞)
- **I**: Infrastructure damage % (0-100)
- **A**: Accessibility difficulty % (0-100)
- **T**: Spread rate (type-dependent units)
- **C**: Cascading risk % (0-100)

#### 4. **States Package** (`com.naveen.ndr.states`)
- `StateNode`: Background threads that generate disaster events based on state profiles

#### 5. **Reporting Package** (`com.naveen.ndr.reporting`)
- `Reporter`: Real-time system monitoring and status reporting

#### 6. **Utilities Package** (`com.naveen.ndr.util`)
- `ConfigLoader`: Centralized configuration management
- `RandomGenerator`: Advanced randomness utilities for simulation
- `SeverityInputExtractor`: Extracts severity factors from events

## 🚀 Getting Started

### Prerequisites
- Java 25 or higher
- Maven 3.8+

### Building the Project
```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package as JAR
mvn package
```

### Running the Simulation
```bash
# Using Maven
mvn exec:java

# Using compiled JAR
java -jar target/ndr-csim-1.0-SNAPSHOT.jar
```

## ⚙️ Configuration

Edit `src/main/resources/config.properties` to customize simulation parameters:

### Key Configuration Options
```properties
# Simulation duration (milliseconds)
simulation.duration.ms=60000

# Event generation interval per state
state.interval.ms=3000

# National resource pool
resources.ndrf.units=50
resources.trucks=100
resources.boats=40
resources.helicopters=30
resources.medical.teams=60

# Reporter update interval
reporter.interval.ms=5000
```

### State Definitions

Edit `src/main/resources/regions.json` to configure state profiles:

```json
{
  "states": [
    {
      "stateId": "KL",
      "stateName": "Kerala",
      "totalPopulation": 35000000,
      "terrainType": "COASTAL",
      "baselineRiskProfile": "HIGH",
      "eventGenerationWeight": 1.5
    }
  ]
}
```

## 🧪 Testing

Comprehensive unit tests for severity rules:

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=SeverityRuleTest
```

Test coverage includes:
- Low/high severity scenarios for each disaster type
- Boundary value testing (zero and maximum values)
- Null safety validation
- Deterministic calculation consistency

## 📊 Simulation Output

The simulation provides real-time status updates:

```
===== SYSTEM STATUS =====
Resources → helicopters=25, ndrf=42, medical=55, boats=35, trucks=92
Producer Queue Size: 3
Dispatch Queue Size: 5
Top Pending Events:
  → KL-a3b2c1d4 | Sev=87 | FLOOD
  → UK-f5e6d7c8 | Sev=82 | LANDSLIDE
  → OD-h9i8j7k6 | Sev=78 | CYCLONE
========================
```

## 🎛️ Design Patterns

- **Strategy Pattern**: Pluggable severity rules
- **Producer-Consumer**: Multi-threaded event processing
- **Priority Queue**: Severity-based event ordering
- **Singleton**: Centralized configuration and random generation
- **Thread-Safe Resource Management**: Synchronized allocation/deallocation

## 🔄 Workflow

1. **State nodes** generate disaster events based on terrain and risk profiles
2. **Producer queue** collects raw events from all states
3. **National Coordinator** calculates severity and prioritizes events
4. **Dispatch queue** orders events by severity (highest first)
5. **Dispatcher** allocates resources and simulates response
6. **Reporter** monitors and displays system status
7. **Resources** are released after response completion

## 📝 Key Features

✅ **Multi-threaded simulation** with concurrent event generation and processing  
✅ **Type-specific severity rules** for different disaster types  
✅ **Priority-based resource allocation** ensuring critical events are handled first  
✅ **Thread-safe resource management** preventing allocation conflicts  
✅ **Real-time monitoring** with periodic status reports  
✅ **Configurable parameters** via properties files  
✅ **Comprehensive test suite** with boundary value and consistency tests  

## 🏛️ Indian States Modeled

The simulation includes realistic profiles for major Indian states:
- **Kerala**: High flood/landslide risk, coastal terrain
- **Maharashtra**: Industrial hub, moderate risk
- **Uttarakhand**: Mountain state, high landslide risk
- **Rajasthan**: Desert state, low risk
- **Assam**: High flood risk, plain terrain
- **Odisha**: Cyclone-prone coastal state

## 🔧 Extending the Simulation

### Adding a New Severity Rule
```java
public class EarthquakeSeverityRule implements SeverityRule {
    @Override
    public int calculateSeverity(DisasterEvent event) {
        // Implement earthquake-specific logic
        return calculatedSeverity;
    }
}

// Register in Main.java
severityCalculator.addRule(new EarthquakeSeverityRule());
```

### Adding a New State
Edit `regions.json` or create programmatically in `Main.java`:
```java
State newState = new State(
    "TG", "Telangana", 39_000_000L, 112077.0,
    State.TerrainType.PLAIN, 350, 250, 80, true,
    State.RiskProfile.MEDIUM, 1.1,
    List.of("FLOOD", "INDUSTRIAL"),
    "telangana-ndrf@gov.in"
);
```

## 📄 License

This project is an educational simulation for understanding disaster response systems.

## 👨‍💻 Author

**Naveen Gunasekaran**  
Version 2.0 - December 5, 2025

## 🙏 Acknowledgments

Inspired by India's National Disaster Response Force (NDRF) and the National Disaster Management Authority (NDMA) framework.

---

**Note**: This is a simulation tool for educational purposes and does not represent actual NDRF operations or policies.
