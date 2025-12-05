# NDR-CSIM Quick Start Guide

## 🚀 Quick Start (5 minutes)

### 1. Prerequisites Check
```bash
java -version  # Should show Java 25+
mvn -version   # Should show Maven 3.8+
```

### 2. Build the Project
```bash
cd /path/to/ndr-csim
mvn clean compile
```

### 3. Run the Simulation
```bash
mvn exec:java
```

The simulation will run for 60 seconds (configurable in `config.properties`).

### 4. Run Tests
```bash
mvn test
```

---

## 📊 Understanding the Output

### System Status Reports
Every 5 seconds, you'll see:
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

**What this means:**
- **Resources**: Current available resources in the national pool
- **Producer Queue**: Raw events waiting for severity calculation
- **Dispatch Queue**: Prioritized events waiting for resource allocation
- **Top Pending Events**: Highest severity events currently in queue

### Event Generation
```
[StateNode-KL] Generated: KL-ef5305fe type=CYCLONE state=Kerala
[Coordinator] Event queued for dispatch: KL-ef5305fe severity=100 CYCLONE
```

**What this means:**
- State node generated a new disaster event
- Coordinator calculated severity (0-100 scale)
- Event added to dispatch queue

### Resource Allocation
```
[Dispatcher] Allocated resources for event=KL-7d88cc95 sev=87 request={helicopters=3, ndrf=4, medical_teams=3}
[Dispatcher] Completed response for event=KL-7d88cc95 released={helicopters=3, ndrf=4, medical_teams=3}
```

**What this means:**
- Dispatcher successfully allocated resources
- Response simulated (time based on severity)
- Resources released back to pool

### Resource Unavailability
```
[Dispatcher] Resources unavailable for event=KL-7d88cc95 sev=100 - requeueing
```

**What this means:**
- Not enough resources to handle this event right now
- Event is requeued (maintains priority)
- Will retry after delay

---

## ⚙️ Configuration

### Adjust Simulation Duration
Edit `src/main/resources/config.properties`:
```properties
# Run for 2 minutes instead of 1
simulation.duration.ms=120000
```

### Adjust Event Generation Rate
```properties
# Slower event generation (5 seconds between events)
state.interval.ms=5000
```

### Increase Resources
```properties
resources.ndrf.units=100
resources.helicopters=60
resources.medical.teams=120
```

### Faster Status Reports
```properties
# Status every 3 seconds instead of 5
reporter.interval.ms=3000
```

---

## 🎯 Common Scenarios

### Scenario 1: Resource Abundance
**Goal**: See all events get handled quickly

**Config Changes:**
```properties
resources.ndrf.units=200
resources.trucks=300
resources.boats=150
resources.medical.teams=200
resources.helicopters=100
state.interval.ms=5000
```

### Scenario 2: Crisis Mode
**Goal**: Simulate overwhelming disaster conditions

**Config Changes:**
```properties
resources.ndrf.units=20
resources.trucks=30
resources.boats=15
resources.medical.teams=25
resources.helicopters=10
state.interval.ms=1000
```

### Scenario 3: Single State Focus
**Goal**: Only generate events from Kerala

**Code Change in Main.java:**
```java
// Comment out other states, keep only Kerala
nodes.add(new StateNode(kerala, queueManager.getProducerQueue(), baseInterval));
```

---

## 📈 Performance Metrics to Watch

1. **Queue Growth Rate**
   - If Dispatch Queue keeps growing → Need more resources or slower event generation
   - If queue stays small → System handling load well

2. **Resource Utilization**
   - Resources near zero → System under heavy load
   - Resources staying high → Light load or slow event generation

3. **Severity Distribution**
   - Many 80-100 severity → High-risk states active
   - Mixed severity → Normal distribution

---

## 🐛 Troubleshooting

### Problem: Queue keeps growing infinitely
**Solution**: Increase resources or slow down event generation:
```properties
state.interval.ms=5000
resources.ndrf.units=100
```

### Problem: No events being generated
**Solution**: Check that state interval is reasonable:
```properties
state.interval.ms=2000  # Generate every 2 seconds
```

### Problem: Compilation errors
**Solution**: Ensure Java 25+ is being used:
```bash
export JAVA_HOME=/path/to/java25
mvn clean compile
```

### Problem: Tests failing
**Solution**: Check JUnit dependency:
```bash
mvn dependency:tree | grep junit
```

---

## 🎓 Learning Exercises

### Exercise 1: Add Priority Levels
Modify `DisasterEvent` to include priority labels (Critical/High/Medium/Low) based on severity thresholds.

### Exercise 2: Add Earthquake Rule
Create `EarthquakeSeverityRule.java` and register it in Main.

### Exercise 3: Resource Prediction
Add logic to predict resource needs based on historical event patterns.

### Exercise 4: State Networking
Implement resource sharing between neighboring states.

### Exercise 5: Event Cascading
Model how one disaster can trigger secondary disasters.

---

## 📚 Further Reading

- `README.md` - Full project documentation
- `src/main/java/com/naveen/ndr/rules/` - Severity calculation algorithms
- `src/main/java/com/naveen/ndr/engine/` - Core simulation engine
- `src/test/java/` - Unit test examples

---

## 💡 Tips

1. **Start with default config** - Run once to see baseline behavior
2. **Monitor the reports** - Watch queue sizes and resource levels
3. **Experiment gradually** - Change one parameter at a time
4. **Use tests** - `mvn test` to verify rule correctness
5. **Read the logs** - Each log line tells a story about the simulation

---

**Happy Simulating!** 🎉
