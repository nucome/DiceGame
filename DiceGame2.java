import java.util.Random;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.stream.IntStream;

/**
 * A dice game simulator that plays a game with configurable number of dice.
 * 
 * Game Rules:
 * - All dice are thrown initially
 * - If any dice show 3, all 3s are removed and 0 points are awarded for this roll
 * - If no 3s are present, the lowest die is removed and its value is added to the score
 * - Remaining dice are re-rolled and the process repeats until no dice remain
 * - The total score is the sum of all non-zero rolls
 * 
 * This class can simulate thousands of games and provide statistical analysis
 * of score distributions.
 * 
 * @author Generated
 * @version 1.0
 */
public class DiceGame2 {
    private static final int DIE_FACES = 6;
    private static final int NEUTRAL_VALUE = 3;
    private static final int MIN_DIE_VALUE = 1;
    private static final int MAX_DIE_VALUE = 6;
    private static final int INITIAL_LOWEST_VALUE = MAX_DIE_VALUE + 1;
    private static final int PERCENTAGE_MULTIPLIER = 100;
    private static final int DEFAULT_NUM_DICE = 5;
    private static final int DEFAULT_NUM_SIMULATIONS = 10000;
    
    private final int numDice;
    private final int numSimulations;
    private final Random random;
    
    /**
     * Constructs a new DiceGame with the specified parameters.
     * 
     * @param numDice the number of dice to use in each game (must be positive)
     * @param numSimulations the number of game simulations to run (must be positive)
     * @throws IllegalArgumentException if either parameter is non-positive
     */
    public DiceGame2(int numDice, int numSimulations) {
        if (numDice <= 0 || numSimulations <= 0) {
            throw new IllegalArgumentException("Number of dice and simulations must be positive");
        }
        this.numDice = numDice;
        this.numSimulations = numSimulations;
        this.random = new Random();
    }
    
    /**
     * Plays a single game according to the dice game rules.
     * 
     * @return the total score for this game (sum of all scoring rolls)
     */
    public int playGame() {
        int[] dice = new int[numDice];
        int activeDice = numDice;
        
        for (int i = 0; i < activeDice; i++) {
            dice[i] = rollDie();
        }
        
        int totalScore = 0;
        
        while (activeDice > 0) {
            boolean hasNeutralValues = false;
            int lowestValue = INITIAL_LOWEST_VALUE;
            int lowestIndex = -1;
            
            for (int i = 0; i < activeDice; i++) {
                if (dice[i] == NEUTRAL_VALUE) {
                    hasNeutralValues = true;
                }
                if (dice[i] < lowestValue) {
                    lowestValue = dice[i];
                    lowestIndex = i;
                }
            }
            
            if (hasNeutralValues) {
                activeDice = removeNeutralValues(dice, activeDice);
            } else {
                totalScore += lowestValue;
                dice[lowestIndex] = dice[activeDice - 1];
                activeDice--;
            }
            
            rollActiveDice(dice, activeDice);
        }
        
        return totalScore;
    }
    
    /**
     * Rolls a single six-sided die.
     * 
     * @return a random integer between 1 and 6 inclusive
     */
    private int rollDie() {
        return random.nextInt(DIE_FACES) + MIN_DIE_VALUE;
    }
    
    /**
     * Removes all neutral values (3s) from the active dice array.
     * This is done in-place by compacting the array to the left.
     * 
     * @param dice the array containing current dice values
     * @param activeDice the number of currently active dice
     * @return the new number of active dice after removal
     */
    private int removeNeutralValues(int[] dice, int activeDice) {
        int writeIndex = 0;
        for (int i = 0; i < activeDice; i++) {
            if (dice[i] != NEUTRAL_VALUE) {
                dice[writeIndex++] = dice[i];
            }
        }
        return writeIndex;
    }
    
    /**
     * Re-rolls all currently active dice with new random values.
     * 
     * @param dice the array to update with new dice values
     * @param activeDice the number of dice positions to re-roll
     */
    private void rollActiveDice(int[] dice, int activeDice) {
        IntStream.range(0, activeDice).forEach(i -> dice[i] = rollDie());
    }
    
    /**
     * Runs the specified number of game simulations and prints statistical results.
     * 
     * Results include:
     * - Frequency distribution of all possible scores
     * - Percentage occurrence of each score
     * - Total execution time
     * 
     * Output is formatted for easy analysis of game statistics.
     */
    public void runSimulation() {
        long startTime = System.currentTimeMillis();
        AtomicIntegerArray scoreFrequency = new AtomicIntegerArray(numDice * MAX_DIE_VALUE + 1);
        
        IntStream.range(0, numSimulations)
                .parallel()
                .forEach(i -> {
                    int gameScore = playGame();
                    if (gameScore < scoreFrequency.length()) {
                        scoreFrequency.incrementAndGet(gameScore);
                    }
                });
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.println("Number of simulations was " + numSimulations + " using " + numDice + " dice.");
        
        for (int score = 0; score < scoreFrequency.length(); score++) {
            int count = scoreFrequency.get(score);
            if (count > 0) {
                double percentage = (double) count * PERCENTAGE_MULTIPLIER / numSimulations;
                System.out.printf("Total %d occurs %.2f%% occurred %.1f times.%n",
                    score, percentage, (double) count);
            }
        }
        
    System.out.printf("Total simulation took %d milliseconds.%n", duration);
    }
    
    /**
     * Main entry point for the dice game simulator.
     * 
     * Command line arguments:
     * - args[0]: number of dice (optional, default: 5)
     * - args[1]: number of simulations (optional, default: 10000)
     * 
     * @param args command line arguments for customizing the simulation
     */
    public static void main(String[] args) {
        int numDice = DEFAULT_NUM_DICE;
        int numSimulations = DEFAULT_NUM_SIMULATIONS;
        
        if (args.length >= 1) {
            numDice = Integer.parseInt(args[0]);
        }
        if (args.length >= 2) {
            numSimulations = Integer.parseInt(args[1]);
        }
        
        DiceGame game = new DiceGame(numDice, numSimulations);
        game.runSimulation();
    }
}