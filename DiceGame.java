import java.util.*;

public class DiceGame {
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
            boolean hasThrees = false;
            int lowestValue = 7;
            int lowestIndex = -1;
            
            for (int i = 0; i < activeDice; i++) {
                if (dice[i] == 3) {
                    hasThrees = true;
                }
                if (dice[i] < lowestValue) {
                    lowestValue = dice[i];
                    lowestIndex = i;
                }
            }
            
            if (hasThrees) {
                int writeIndex = 0;
                for (int i = 0; i < activeDice; i++) {
                    if (dice[i] != 3) {
                        dice[writeIndex++] = dice[i];
                    }
                }
                activeDice = writeIndex;
            } else {
                totalScore += lowestValue;
                dice[lowestIndex] = dice[activeDice - 1];
                activeDice--;
            }
            
            for (int i = 0; i < activeDice; i++) {
                dice[i] = rollDie();
            }
        }
        
        return totalScore;
    }
    
    private int rollDie() {
        return random.nextInt(6) + 1;
    }
    
    public void runSimulation() {
        long startTime = System.currentTimeMillis();
        int[] scoreFrequency = new int[numDice * 6 + 1];
        
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
                double percentage = (double) count * 100 / numSimulations;
                System.out.printf("Total %d occurs %.2f%% occurred %.1f times.%n",
                    score, percentage, (double) count);
            }
        }
        
    System.out.printf("Total simulation took %d milliseconds.%n", duration);
    }
    
    public static void main(String[] args) {
        int numDice = 5;
        int numSimulations = 10000;
        
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