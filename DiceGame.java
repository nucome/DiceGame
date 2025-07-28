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
        List<Integer> dice = new ArrayList<>();
        for (int i = 0; i < numDice; i++) {
            dice.add(rollDie());
        }
        
        int totalScore = 0;
        
        while (!dice.isEmpty()) {
            boolean hasThrees = dice.contains(3);
            
            if (hasThrees) {
                dice.removeAll(Collections.singleton(3));
            } else {
                int lowestValue = Collections.min(dice);
                totalScore += lowestValue;
                dice.remove(Integer.valueOf(lowestValue));
            }
            
            for (int i = 0; i < dice.size(); i++) {
                dice.set(i, rollDie());
            }
        }
        
        return totalScore;
    }
    
    private int rollDie() {
        return random.nextInt(6) + 1;
    }
    
    public void runSimulation() {
        long startTime = System.currentTimeMillis();
        Map<Integer, Integer> scoreFrequency = new HashMap<>();
        
        for (int i = 0; i < numSimulations; i++) {
            int gameScore = playGame();
            scoreFrequency.put(gameScore, scoreFrequency.getOrDefault(gameScore, 0) + 1);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.println("Number of simulations was " + numSimulations + " using " + numDice + " dice.");
        
        List<Integer> sortedScores = new ArrayList<>(scoreFrequency.keySet());
        Collections.sort(sortedScores);
        
        for (int score : sortedScores) {
            int count = scoreFrequency.get(score);
            double percentage = (double) count * 100 / numSimulations;
            System.out.printf("Total %d occurs %.2f%% occurred %.1f times.%n",
                score, percentage, (double) count);
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