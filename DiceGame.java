import java.util.*;
import java.util.stream.IntStream;

public class DiceGame {
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
    
    public DiceGame(int numDice, int numSimulations) {
        this.numDice = numDice;
        this.numSimulations = numSimulations;
        this.random = new Random();
    }
    
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
    
    private int rollDie() {
        return random.nextInt(DIE_FACES) + MIN_DIE_VALUE;
    }
    
    private int removeNeutralValues(int[] dice, int activeDice) {
        int writeIndex = 0;
        for (int i = 0; i < activeDice; i++) {
            if (dice[i] != NEUTRAL_VALUE) {
                dice[writeIndex++] = dice[i];
            }
        }
        return writeIndex;
    }
    
    private void rollActiveDice(int[] dice, int activeDice) {
        IntStream.range(0, activeDice).forEach(i -> dice[i] = rollDie());
    }
    
    public void runSimulation() {
        long startTime = System.currentTimeMillis();
        int[] scoreFrequency = new int[numDice * MAX_DIE_VALUE + 1];
        
        for (int i = 0; i < numSimulations; i++) {
            int gameScore = playGame();
            if (gameScore < scoreFrequency.length) {
                scoreFrequency[gameScore]++;
            }
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.println("Number of simulations was " + numSimulations + " using " + numDice + " dice.");
        
        for (int score = 0; score < scoreFrequency.length; score++) {
            int count = scoreFrequency[score];
            if (count > 0) {
                double percentage = (double) count * PERCENTAGE_MULTIPLIER / numSimulations;
                System.out.printf("Total %d occurs %.2f%% occurred %.1f times.%n",
                    score, percentage, (double) count);
            }
        }
        
    System.out.printf("Total simulation took %d milliseconds.%n", duration);
    }
    
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