package hobo;

import java.util.Arrays;
import java.util.Comparator;

public class ParameterTuning {
	public static final MersenneTwisterFast random = new MersenneTwisterFast();
	
	public static void main(String[] args) {
		tuneMCTS();
	}
	
	public static void tuneMCTS() {
		// need to tune it four times, for all combinations
		// of strategic/use_signum
		tuneByCrossEntropy(10, 10, 0.2, new double[]{ 15, 1, 70, 20, 2 }, new double[]{ 5, 1, 10, 5, 2 }, new Function<double[], Double>() {
			@Override public Double call(double[] xs) {
				if (xs[0] < 0)
					return Double.NEGATIVE_INFINITY;

				try {
					String parameters = " expansion_threshold:"+((int)Math.round(xs[0]))
					                  + " uct_weight:"+xs[1]
					                  + " sigmoid_steepness:"+(1/xs[2])
					                  + " alpha:"+(1/xs[3])
					                  + " beta:"+xs[4]
					                  + " strategic:false use_signum:false";
					Game g = new Game("verbose:false",
					                  Player.fromConfiguration("montecarlo name:carlo verbose:false decision_time:1"+parameters),
					                  Player.fromConfiguration("minimax    name:carlo verbose:false decision_time:1"));
					g.play();
					return 1.0 * Math.signum(g.state.aheadness(0));
				} catch (Throwable t) {
					t.printStackTrace();
					return Double.NEGATIVE_INFINITY;
				}
			}
		});
	}
	
	public static void tuneByCrossEntropy(int population_size, int sample_size, double rho,
		                                  double[] initial_means, double[] initial_stdevs,
		                                  Function<double[],Double> evaluation) {
		final double alpha = 0.1;
		double[] means = initial_means.clone(), stdevs = initial_stdevs.clone();
		final int nvariables = means.length, elites_size = (int)Math.round(rho * population_size);

		double[][] population = new double[population_size][nvariables];
		
		while (true) {
			System.out.println("means:  "+Arrays.toString(means));
			System.out.println("stdevs: "+Arrays.toString(stdevs));

			for (int i = 0; i < population_size; i++) {
				for (int j = 0; j < nvariables; j++)
					population[i][j] = means[j] + random.nextGaussian() * stdevs[j];
			}

			final double[] evaluations = new double[population_size];
			for (int i = 0; i < population_size; i++)
				for (int k = 0; k < sample_size; k++)
					evaluations[i] = evaluation.call(population[i]);

			// now jump through hoops to sort population by evaluations
			Integer[] permutation = new Integer[population_size];
			for (int i = 0; i < population_size; i++)
				permutation[i] = i;
			Arrays.sort(permutation, new Comparator<Integer>() {
				@Override public int compare(Integer a, Integer b) {
					return -Double.compare(evaluations[a], evaluations[b]);
				}
			});
			
			System.out.println("elites: ");
			double[][] elites = new double[elites_size][nvariables];
			for (int i = 0; i < elites_size; i++) {
				elites[i] = population[permutation[i]];
				System.out.println(evaluations[permutation[i]]+"\t"+Arrays.toString(elites[i]));
			}

			double[][] sampleT = Util.transpose(elites);
			for (int i = 0; i < nvariables; i++) {
				means [i] = alpha * Util.mean (sampleT[i]) + (1 - alpha) * means[i];
				stdevs[i] = alpha * Util.stdev(sampleT[i]) + (1 - alpha) * stdevs[i];
			}
		}
	}
}
