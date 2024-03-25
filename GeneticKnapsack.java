import java.util.Random;

public class GeneticKnapsack {

    int[] weights;
    int[] values;
    int capacity;
    int populationSize = 50;
    int maxGenerations = 100;
   // Why the results of the original is not optimal --> generation are the same (stuck) we need to change the mutation prob
    //original
  //  double mutationRate = 0.01;
    //
    double mutationRate = 0.2;
    double crossoverRate = 0.8;
    Random rand = new Random();
    int[] bestChromosomeEver = null;
    int bestFitnessEver = Integer.MIN_VALUE;
    int bestGeneration = -1;
    
    public GeneticKnapsack(int[] weights, int[] values, int capacity) {
        this.weights = weights;
        this.values = values;
        this.capacity = capacity;
    }

    public int[] solve() {
        int[][] population = initializePopulation();
        for (int generation = 0; generation < maxGenerations; generation++) {
            System.out.println("\nGeneration " + (generation + 1) + ":");
            displayGeneration(population);

            int[] currentFittest = getFittest(population);
            int currentFitness = computeFitness(currentFittest);
            int currentWeight = computeTotalWeight(currentFittest);
            
            System.out.println("Best of Generation " + (generation + 1) + ":");
            System.out.println("Value = " + currentFitness + ", Weight = " + currentWeight);

            if (currentFitness > bestFitnessEver) {
                bestFitnessEver = currentFitness;
                bestChromosomeEver = currentFittest.clone();
                bestGeneration = generation + 1; // Adding 1 because generation index starts from 0
            }

            population = newGeneration(population);
        }

        System.out.println("\nOverall best observed in Generation " + bestGeneration + ":");
        System.out.println("Value = " + bestFitnessEver + ", Weight = " + computeTotalWeight(bestChromosomeEver)); return bestChromosomeEver;
    }

    
    private int[][] initializePopulation() {
        int[][] population = new int[populationSize][weights.length];
        for (int i = 0; i < populationSize; i++) {
            for (int j = 0; j < weights.length; j++) {
                population[i][j] = rand.nextInt(2);
            }
        }
        return population;
    }

    private int[] getFittest(int[][] population) {
        int[] fittest = null;
        int maxFitness = Integer.MIN_VALUE;
        for (int[] chromosome : population) {
            int fitness = computeFitness(chromosome);
            if (fitness > maxFitness) {
                maxFitness = fitness;
                fittest = chromosome;
            }
        }
        return fittest;
    }

    private int computeFitness(int[] chromosome) {
        int totalWeight = 0;
        int totalValue = 0;
        for (int i = 0; i < chromosome.length; i++) {
            if (chromosome[i] == 1) {
                totalWeight += weights[i];
                totalValue += values[i];
            }
        }
        if (totalWeight > capacity) return 0;
        return totalValue;
    }

    private int computeTotalWeight(int[] chromosome) {
        int totalWeight = 0;
        for (int i = 0; i < chromosome.length; i++) {
            if (chromosome[i] == 1) {
                totalWeight += weights[i];
            }
        }
        return totalWeight;
    }

    private void displayGeneration(int[][] population) {
        for (int i = 0; i < populationSize; i++) {
            System.out.print("Chromosome " + (i+1) + ": [");
            for (int j = 0; j < population[i].length; j++) {
                System.out.print(population[i][j]);
                if (j != population[i].length - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println("]");
        }
    }

    private int[][] newGeneration(int[][] population) {
        int[][] newPopulation = new int[populationSize][weights.length];
        for (int i = 0; i < populationSize; i++) {
            int[] parent1 = selectParent(population);
            int[] parent2 = selectParent(population);
            int[] child = crossover(parent1, parent2);
            mutate(child);
            newPopulation[i] = child;
        }
        return newPopulation;
    }

    private int[] selectParent(int[][] population) {
        // Tournament selection
        int tournamentSize = 5;
        int bestFitness = Integer.MIN_VALUE;
        int[] bestChromosome = null;
        for (int i = 0; i < tournamentSize; i++) {
            int randomId = rand.nextInt(populationSize);
            int fitness = computeFitness(population[randomId]);
            if (fitness > bestFitness) {
                bestFitness = fitness;
                bestChromosome = population[randomId];
            }
        }
        return bestChromosome;
    }

    // Uniform Crossover : For In the uniform crossover, 
    //for each gene (or position) in the chromosome, a random decision is made about which parent will contribute that gene to the offspring.
    //The decision is made based on the crossoverRate
    private int[] crossover(int[] parent1, int[] parent2) {
        int[] child = new int[weights.length];
        for (int i = 0; i < weights.length; i++) {
            if (rand.nextDouble() < crossoverRate) {
                child[i] = parent1[i];
            } else {
                child[i] = parent2[i];
            }
        }
        return child;
    }

    
    //BitFLip Mutation In the bit flip mutation, each gene (bit) of the chromosome has a small chance (mutationRate) of being flipped. 
    //That means if the gene is 1, it's changed to 0, and if it's 0, it's changed to 1.
    
    private void mutate(int[] chromosome) {
        for (int i = 0; i < weights.length; i++) {
            if (rand.nextDouble() < mutationRate) {
                chromosome[i] = 1 - chromosome[i];
            }
        }
    }

    public static void main(String[] args) {
       //original
    	// int[] weights = {2, 3, 4, 5};
       // int[] values = {3, 4, 5, 6};
       // int capacity = 5;

    	// 1 1 1 1
    	//int[] weights = {5, 3, 2, 5};
         //int[] values = {3, 4, 5, 6};
         //int capacity = 15;

      // 0, 1, 1, 1
     	int[] weights = {5, 3, 2, 5};
          int[] values = {3, 4, 5, 6};
          int capacity = 13;

    	
        GeneticKnapsack gk = new GeneticKnapsack(weights, values, capacity);
        int[] solution = gk.solve();
        System.out.println("Best solution observed:");
        for (int val : solution) {
            System.out.print(val + " ");
        }
    }
}
