package hobo;

import java.util.Arrays;
import java.util.Comparator;

public class ParameterTuning {
	public static final MersenneTwisterFast random = new MersenneTwisterFast();
	
	public static void main(String[] args) {
		tuneMinimax();
	}

	public static void tuneMCTS() {
		tuneByCrossEntropy(5, 2, 8, new double[]{ 15, 1, 70, 20, 2 }, new double[]{ 5, 1, 10, 5, 2 }, new Function<double[], Double>() {
			@Override public Double call(double[] xs) {
				if (xs[0] < 0)
					return Double.NEGATIVE_INFINITY;

				try {
					String parameters = " expansion_threshold:"+((int)Math.round(xs[0]))
					                  + " uct_weight:"+xs[1]
					                  + " sigmoid_steepness:"+(1/xs[2])
					                  + " alpha:"+(1/xs[3])
					                  + " beta:"+xs[4]
					                  + " strategic:false use_signum:true";
					Game g = new Game("verbose:false",
					                  Player.fromConfiguration("uncertain montecarlo verbose:false sample_size:3 decision_time:3000"+parameters),
					                  Player.fromConfiguration("uncertain minimax    verbose:false sample_size:3 decision_time:3000"));
					g.play();
					return 1.0 * Math.signum(g.state.aheadness(0));
				} catch (Throwable t) {
					t.printStackTrace();
					return Double.NEGATIVE_INFINITY;
				}
			}
		});
	}

	public static void tuneMinimax() {
		tuneByCrossEntropy(5, 2, 8, new double[]{ 1, 1, 1, 1, 1 }, new double[]{ 1, 1, 1, 1, 1 }, new Function<double[], Double>() {
			@Override public Double call(double[] xs) {
				try {
					String parameters = " alpha:"+xs[0]+" beta:"+xs[1]+" gamma:"+xs[2]+" delta:"+xs[3]+" zeta:"+xs[4];
					Game g = new Game("verbose:false",
					                  Player.fromConfiguration("uncertain minimax    verbose:false sample_size:3 decision_time:3000"+parameters),
					                  Player.fromConfiguration("uncertain montecarlo verbose:false sample_size:3 decision_time:3000"));
					g.play();
					return 1.0 * Math.signum(g.state.aheadness(0));
				} catch (Throwable t) {
					t.printStackTrace();
					return Double.NEGATIVE_INFINITY;
				}
			}
		});
	}

	public static void tuneByCrossEntropy(int population_size, int selection_size, int sample_size,
		                                  double[] initial_means, double[] initial_stdevs,
		                                  Function<double[],Double> evaluation) {
		final double alpha = 0.1;
		double[] means = initial_means.clone(), stdevs = initial_stdevs.clone();
		final int nvariables = means.length;

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
			
			System.out.println("elites:");
			double[][] selection = new double[selection_size][nvariables];
			for (int i = 0; i < selection_size; i++) {
				selection[i] = population[permutation[i]];
				System.out.println("  "+evaluations[permutation[i]]+"\t"+Arrays.toString(selection[i]));
			}

			double[][] sampleT = Util.transpose(selection);
			for (int i = 0; i < nvariables; i++) {
				means [i] = alpha * Util.mean (sampleT[i]) + (1 - alpha) * means[i];
				stdevs[i] = alpha * Util.stdev(sampleT[i]) + (1 - alpha) * stdevs[i];
			}
		}
	}
}
