import java.util.Random;
import java.util.Arrays;

class Particle {
	int[] position;
	double[] velocity;
	int[] pbestPosition;
	int pbestFitness;

	public Particle(int numItems) {
		this.position = new int[numItems];
		this.velocity = new double[numItems];
		this.pbestPosition = new int[numItems];
		this.pbestFitness = Integer.MAX_VALUE;
	}

}

class SubsetSumPSO {

	public Particle[] swarm; // Array of the particles
	int[] gbestPosition; // Best fitness of all particles
	int gbestFitness; // Best position of all particles
	int[] values; // Array of values that is searched
	int target; // Target value
	int numParticles; // Number of particles in swarm
	double accuracy; // Accuracy for each algorithm execution

	public SubsetSumPSO(int[] values, int target, int numParticles) {
		final Random rand = new Random();
		this.values = values;
		this.target = target;
		this.numParticles = numParticles;

		swarm = new Particle[numParticles];
		gbestFitness = Integer.MAX_VALUE;
		gbestPosition = new int[values.length];
		accuracy = 0;

		for (int i = 0; i < swarm.length; i++) {

			swarm[i] = new Particle(values.length);

			for (int j = 0; j < values.length; j++) {
				swarm[i].position[j] = rand.nextInt(2); // Generate random start point for each particle
				swarm[i].velocity[j] = rand.nextDouble();
			}
		}

	}

	/*
	 *
	 * c1=importance of personal best
	 * c2=importance of GLOBAL best
	 */
	public void optimize(int iterations, double c1, double c2) {

		final Random rand = new Random();
		int fitness; // Placeholder for each particles fitness value
		double w = 0.5; // intertia value

		for (int i = 0; i < iterations; i++) {

			// Find the fittest
			for (Particle particle : swarm) {
				fitness = evaluateFitness(particle.position);
				// System.out.println(" Particle Position : " +
				// Arrays.toString(particle.position) + " Fitness : " + fitness);

				// Check if we found solution
				if (fitness == 0) {
					System.out.println("Solution found : " + Arrays.toString(convert(particle.position)));
					accuracy = calcAccuracy(particle.position);
					System.out.println("Accuracy : " + accuracy);
					System.out.println("Number of Interations : " + i + "\n");
					return;
				}

				// Check for personal best
				if (fitness <= particle.pbestFitness) {
					particle.pbestFitness = fitness;
					particle.pbestPosition = particle.position.clone();
				}

				// Check for global best
				if (fitness <= gbestFitness) {
					gbestFitness = fitness;
					gbestPosition = particle.position.clone();
				}

				// Calculate Velocity and Adjust Position
				for (int j = 0; j < particle.position.length; j++) {
					particle.velocity[j] = w * particle.velocity[j]
							+ c1 * rand.nextDouble() * (particle.pbestPosition[j] - particle.position[j])
							+ c2 * rand.nextDouble() * (gbestPosition[j] - particle.position[j]);

					if (sigmoid(particle.velocity[j]) > rand.nextDouble()) {
						particle.position[j] = 1;
					} else {
						particle.position[j] = 0;
					}
				}

			}

			// System.out.println("Best Global Position : " +
			// Arrays.toString(convert(gbestPosition)));

		}

		System.out.println("No solution was found(30 iterations)");
		System.out.println("Closest solution : " + Arrays.toString(convert(gbestPosition)));
		accuracy = calcAccuracy(gbestPosition);
		System.out.println("Accuracy : " + accuracy + "\n");
	

	}

	// Absolute value of (target-subsetSum), smaller value means more fit
	private int evaluateFitness(int[] position) {

		int subsetSum = 0;
		int fitness = Integer.MAX_VALUE;

		for (int i = 0; i < position.length; i++) {
			if (position[i] == 1) {
				subsetSum += values[i];
			}
		}

		fitness = Math.abs(target - subsetSum);

		return fitness;

	}

	// A sigmoid function is a mathematical function that maps any real value to a
	// number between 0 and 1.
	private double sigmoid(double x) {
		return 1.0 / (1.0 + Math.exp(-x));
	}

	// Converts a position array back to the original format of integer values
	private int[] convert(int[] position) {
		int[] og = new int[position.length];
		for (int i = 0; i < position.length; i++) {
			if (position[i] == 1) {
				og[i] = values[i];
			} else {
				og[i] = 0;
			}
		}
		return og;
	}

	private double calcAccuracy(int[] position) {
		double sum = 0;
		int[] og = new int[position.length];
		og = convert(position);
		for (int i = 0; i < og.length; i++) {
			sum += og[i];
		}
		return (sum / target) * 100;
	}

	public static void main(String[] args) {
		int[] values = { 3, 34, 4, 12, 5, 2, 25, 31, 60, 91, 47, 73, 17, 53, 28, 39, 67, 80, 36, 50, 
			15, 95, 44, 78, 20, 10, 13, 56, 89, 14, 38, 70, 9, 40, 22, 7, 76, 58, 49, 85 };
		int target = 300;
		double avgAccuracy = 0;

		for (int i = 0; i < 50; i++) {
			System.out.println("Execution : " + (i + 1));
			SubsetSumPSO pso = new SubsetSumPSO(values, target, 10);
			pso.optimize(30, 1.5, 2);
			avgAccuracy += pso.accuracy;
		}
		System.out.println("Average accuracy : " + (avgAccuracy / 50));
	}
}