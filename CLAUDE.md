# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Java dice game simulator that implements a specific scoring game with 5 dice. The game has unique rules where rolling 3s removes them from play with 0 points, while the lowest non-3 die is removed and adds to the score. The project includes two implementations and comprehensive unit tests.

## Development Commands

### Compilation
```bash
javac *.java
```

### Running the Game
```bash
# Run DiceGame with default settings (5 dice, 10000 simulations)
java DiceGame

# Run with custom parameters (number of dice, number of simulations)
java DiceGame 3 1000

# Run DiceGame2 (parallel processing version)
java DiceGame2 5 10000
```

### Running Tests
```bash
# Compile and run tests (requires JUnit 5.8.1)
javac -cp ".:junit-jupiter-5.8.1.jar" DiceGameTest.java
java -cp ".:junit-jupiter-5.8.1.jar" org.junit.platform.console.ConsoleLauncher --class-path . --select-class DiceGameTest
```

## Code Architecture

### Core Classes

**DiceGame.java** - Original sequential implementation
- Main game logic with configurable dice count and simulation iterations
- Uses single-threaded processing for simulations
- Key methods: `playGame()`, `runSimulation()`, `removeNeutralValues()`

**DiceGame2.java** - Optimized parallel implementation  
- Identical game logic but uses `AtomicIntegerArray` and parallel streams
- Better performance for large simulation counts
- Parallelizes the simulation loop using `IntStream.range().parallel()`

**DiceGameTest.java** - Comprehensive unit test suite
- Tests constructor validation, game logic, and simulation output
- Uses reflection to test private methods for complete coverage
- Includes boundary testing and randomization verification

### Game Logic Flow
1. Initialize dice array with random values (1-6)
2. While dice remain active:
   - Check for 3s (neutral values) - if found, remove all 3s, score 0
   - If no 3s, find lowest value, add to score, remove that die
   - Re-roll remaining active dice
3. Return total accumulated score

### Key Constants
- `DIE_FACES = 6` - Standard six-sided dice
- `NEUTRAL_VALUE = 3` - Value that gets removed without scoring
- `DEFAULT_NUM_DICE = 5` - Standard game uses 5 dice
- `DEFAULT_NUM_SIMULATIONS = 10000` - Default simulation count

## Project Structure
- Source files in root directory
- Compiled classes in `out/production/DiceGame/`
- IntelliJ IDEA project configuration (`.iml` file)
- JUnit 5.8.1 dependencies configured for testing