import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Random;

/**
 * Unit tests for the DiceGame class.
 * 
 * Tests cover constructor validation, game logic, and simulation functionality.
 * Uses reflection to test private methods and fields for comprehensive coverage.
 */
@DisplayName("DiceGame Unit Tests")
public class DiceGameTest {
    
    private DiceGame game;
    private static final int TEST_NUM_DICE = 3;
    private static final int TEST_NUM_SIMULATIONS = 100;
    
    @BeforeEach
    void setUp() {
        game = new DiceGame(TEST_NUM_DICE, TEST_NUM_SIMULATIONS);
    }
    
    @Test
    @DisplayName("Constructor should create valid DiceGame with positive parameters")
    void testConstructorValid() {
        assertNotNull(game);
        // Use reflection to verify private fields are set correctly
        try {
            Field numDiceField = DiceGame.class.getDeclaredField("numDice");
            Field numSimulationsField = DiceGame.class.getDeclaredField("numSimulations");
            Field randomField = DiceGame.class.getDeclaredField("random");
            
            numDiceField.setAccessible(true);
            numSimulationsField.setAccessible(true);
            randomField.setAccessible(true);
            
            assertEquals(TEST_NUM_DICE, numDiceField.get(game));
            assertEquals(TEST_NUM_SIMULATIONS, numSimulationsField.get(game));
            assertNotNull(randomField.get(game));
        } catch (Exception e) {
            fail("Failed to access private fields: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("Constructor should throw IllegalArgumentException for zero dice")
    void testConstructorZeroDice() {
        assertThrows(IllegalArgumentException.class, () -> {
            new DiceGame(0, TEST_NUM_SIMULATIONS);
        });
    }
    
    @Test
    @DisplayName("Constructor should throw IllegalArgumentException for negative dice")
    void testConstructorNegativeDice() {
        assertThrows(IllegalArgumentException.class, () -> {
            new DiceGame(-1, TEST_NUM_SIMULATIONS);
        });
    }
    
    @Test
    @DisplayName("Constructor should throw IllegalArgumentException for zero simulations")
    void testConstructorZeroSimulations() {
        assertThrows(IllegalArgumentException.class, () -> {
            new DiceGame(TEST_NUM_DICE, 0);
        });
    }
    
    @Test
    @DisplayName("Constructor should throw IllegalArgumentException for negative simulations")
    void testConstructorNegativeSimulations() {
        assertThrows(IllegalArgumentException.class, () -> {
            new DiceGame(TEST_NUM_DICE, -1);
        });
    }
    
    @Test
    @DisplayName("rollDie should return values between 1 and 6")
    void testRollDie() throws Exception {
        Method rollDieMethod = DiceGame.class.getDeclaredMethod("rollDie");
        rollDieMethod.setAccessible(true);
        
        // Test multiple rolls to ensure range
        for (int i = 0; i < 100; i++) {
            int result = (Integer) rollDieMethod.invoke(game);
            assertTrue(result >= 1 && result <= 6, 
                "Die roll result " + result + " is not between 1 and 6");
        }
    }
    
    @Test
    @DisplayName("removeNeutralValues should remove all 3s from dice array")
    void testRemoveNeutralValues() throws Exception {
        Method removeNeutralValuesMethod = DiceGame.class.getDeclaredMethod("removeNeutralValues", int[].class, int.class);
        removeNeutralValuesMethod.setAccessible(true);
        
        // Test case with 3s present
        int[] dice = {1, 3, 2, 3, 5};
        int result = (Integer) removeNeutralValuesMethod.invoke(game, dice, 5);
        
        assertEquals(3, result); // Should have 3 dice left after removing two 3s
        assertEquals(1, dice[0]);
        assertEquals(2, dice[1]);
        assertEquals(5, dice[2]);
    }
    
    @Test
    @DisplayName("removeNeutralValues should return same count when no 3s present")
    void testRemoveNeutralValuesNo3s() throws Exception {
        Method removeNeutralValuesMethod = DiceGame.class.getDeclaredMethod("removeNeutralValues", int[].class, int.class);
        removeNeutralValuesMethod.setAccessible(true);
        
        int[] dice = {1, 2, 4, 5, 6};
        int result = (Integer) removeNeutralValuesMethod.invoke(game, dice, 5);
        
        assertEquals(5, result); // Should have all 5 dice remaining
    }
    
    @Test
    @DisplayName("rollActiveDice should update all active dice positions")
    void testRollActiveDice() throws Exception {
        Method rollActiveDiceMethod = DiceGame.class.getDeclaredMethod("rollActiveDice", int[].class, int.class);
        rollActiveDiceMethod.setAccessible(true);
        
        int[] dice = {0, 0, 0, 0, 0}; // Initialize with zeros
        rollActiveDiceMethod.invoke(game, dice, 3);
        
        // Check that first 3 positions are updated with valid die values
        for (int i = 0; i < 3; i++) {
            assertTrue(dice[i] >= 1 && dice[i] <= 6, 
                "Die at position " + i + " has invalid value: " + dice[i]);
        }
        
        // Check that remaining positions are unchanged
        assertEquals(0, dice[3]);
        assertEquals(0, dice[4]);
    }
    
    @Test
    @DisplayName("playGame should return non-negative score")
    void testPlayGameReturnsValidScore() {
        // Test multiple games to ensure scores are always valid
        for (int i = 0; i < 10; i++) {
            int score = game.playGame();
            assertTrue(score >= 0, "Game score should be non-negative, got: " + score);
        }
    }
    
    @Test
    @DisplayName("playGame should return reasonable score range")
    void testPlayGameScoreRange() {
        // For 3 dice, maximum possible score would be around 3*6 = 18
        // But due to game rules, actual max should be lower
        for (int i = 0; i < 50; i++) {
            int score = game.playGame();
            assertTrue(score <= 18, "Game score seems too high: " + score);
        }
    }
    
    @Test
    @DisplayName("runSimulation should produce output without errors")
    void testRunSimulation() {
        // Capture console output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        
        try {
            // Create a small simulation to test
            DiceGame smallGame = new DiceGame(2, 10);
            smallGame.runSimulation();
            
            String output = outputStream.toString();
            
            // Verify key elements are present in output
            assertTrue(output.contains("Number of simulations was 10 using 2 dice"));
            assertTrue(output.contains("Total simulation took"));
            assertTrue(output.contains("occurs"));
            
        } finally {
            System.setOut(originalOut);
        }
    }
    
    @Test
    @DisplayName("runSimulation should handle single die game")
    void testRunSimulationSingleDie() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        
        try {
            DiceGame singleDieGame = new DiceGame(1, 50);
            singleDieGame.runSimulation();
            
            String output = outputStream.toString();
            assertTrue(output.contains("using 1 dice"));
            
        } finally {
            System.setOut(originalOut);
        }
    }
    
    @Test
    @DisplayName("Multiple games should produce varied results")
    void testGameVariability() {
        // Play multiple games and ensure we get some variation in scores
        // This tests that the random number generation is working
        int[] scores = new int[20];
        for (int i = 0; i < scores.length; i++) {
            scores[i] = game.playGame();
        }
        
        // Check that we don't get all identical scores (very unlikely with proper randomization)
        boolean hasVariation = false;
        for (int i = 1; i < scores.length; i++) {
            if (scores[i] != scores[0]) {
                hasVariation = true;
                break;
            }
        }
        assertTrue(hasVariation, "All game scores were identical - randomization may not be working");
    }
    
    @Test
    @DisplayName("Game with many dice should still complete")
    void testLargeDiceGame() {
        DiceGame largeGame = new DiceGame(10, 5);
        
        // Should complete without throwing exceptions
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 5; i++) {
                int score = largeGame.playGame();
                assertTrue(score >= 0);
            }
        });
    }
}