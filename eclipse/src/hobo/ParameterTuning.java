package hobo;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ParameterTuning {
	public static final MersenneTwisterFast random = new MersenneTwisterFast();
	
	public static void main(String[] args) {
		tuneBelief();
	}

	public static void tuneMCTS() {
		final boolean strategic = false;
		System.out.println("tuning "+(strategic ? "strategic" : "uniform")+" mcts");
		tuneByCrossEntropy(5, 2, 8, new double[]{ 15, 1, 70, 20, 2 }, new double[]{ 5, 1, 10, 5, 2 }, new Function<double[], Double>() {
			@Override public Double call(double[] xs) {
				// xs[0] must be positive, so return something < -1
				if (xs[0] < 0)
					return xs[0] - 1;

				String parameters = " expansion_threshold:"+((int)Math.round(xs[0]))
				                  + " uct_weight:"+xs[1]
				                  + " sigmoid_steepness:"+(1/xs[2])
				                  + " alpha:"+(1/xs[3])
				                  + " beta:"+xs[4]
				                  + " strategic:"+strategic+" use_signum:true";
				Game g = new Game("verbose:false",
				                  Player.fromConfiguration("uncertain montecarlo verbose:false sample_size:3 decision_time:3000"+parameters),
				                  Player.fromConfiguration("uncertain minimax    verbose:false sample_size:3 decision_time:3000"));
				g.play();
				return 1.0 * Math.signum(g.state.aheadness(0));
			}
		});
	}

	public static void tuneMinimax() {
		System.out.println("tuning minimax");
		tuneByCrossEntropy(5, 2, 8, new double[]{ 2, 1, 1, 1, 2 }, new double[]{ 1, 1, 1, 1, 1 }, new Function<double[], Double>() {
			@Override public Double call(double[] xs) {
				String parameters = " alpha:"+xs[0]+" beta:"+xs[1]+" gamma:"+xs[2]+" delta:"+xs[3]+" zeta:"+xs[4];
				Game g = new Game("verbose:false",
				                  Player.fromConfiguration("uncertain minimax    verbose:false sample_size:3 decision_time:3000"+parameters),
				                  Player.fromConfiguration("uncertain montecarlo verbose:false sample_size:3 decision_time:3000"));
				g.play();
				return 1.0 * Math.signum(g.state.aheadness(0));
			}
		});
	}

	public static void tuneBelief() {
		System.out.println("tuning belief");
		tuneByCrossEntropy(5, 2, 3, new double[]{ 2 }, new double[]{ 1 }, new Function<double[], Double>() {
			@Override public Double call(double[] xs) {
				String parameters = " belief_alpha:"+xs[0];
				Game g = new Game("verbose:false",
				                  Player.fromConfiguration("uncertain minimax    verbose:false sample_size:3 decision_time:6000"+parameters),
				                  Player.fromConfiguration("uncertain montecarlo verbose:false sample_size:3 decision_time:6000"+parameters));
				System.out.println(xs[0]);
				g.play();
				
				double[] accuracies = new double[g.players.length];
				for (int i = 0; i < g.players.length; i++) {
					double[] stats = g.players[i].statistics();
					accuracies[i] = stats[stats.length-1];
				}
				return Util.mean(accuracies);
			}
		});
	}

	private static final int nthreads = 2;
	public static void tuneByCrossEntropy(int population_size, int selection_size, int sample_size,
		                                  double[] initial_means, double[] initial_stdevs,
		                                  final Function<double[],Double> evaluation) {
		final double alpha = 0.1;
		double[] means = initial_means.clone(), stdevs = initial_stdevs.clone();
		final int nvariables = means.length;

		final double[][] population = new double[population_size][nvariables];
		final long[] failures_and_tries = new long[2];

		ExecutorService pool = Executors.newFixedThreadPool(nthreads);
		
		while (true) {
			System.out.println("means:  "+Arrays.toString(means));
			System.out.println("stdevs: "+Arrays.toString(stdevs));

			for (int i = 0; i < population_size; i++) {
				for (int j = 0; j < nvariables; j++)
					population[i][j] = means[j] + random.nextGaussian() * stdevs[j];
			}

			// fuck you java
			Object[] future_evaluations = new Object[population_size];
			for (int i = 0; i < population_size; i++) {
				for (int k = 0; k < sample_size; k++) {
					final int j = i;
					future_evaluations[i] = pool.submit(new Callable<Double>() {
						@Override public Double call() {
							return evaluation.call(population[j]);
						}
					});
				}
			}

			final double[] evaluations = new double[population_size];
			for (int i = 0; i < population_size; i++) {
				for (int k = 0; k < sample_size; k++) {
					try {
						failures_and_tries[1]++;

						while (true) {
							try {
								evaluations[i] = ((Future<Double>)future_evaluations[i]).get();
								break;
							} catch (InterruptedException e) {
								continue;
							}
						}
					} catch (ExecutionException e) {
						e.printStackTrace();
						failures_and_tries[0]++;
						if (failures_and_tries[1] > 100 && failures_and_tries[0] * 1.0 / failures_and_tries[1] > 0.5)
							throw new RuntimeException("too many failures -- something is wrong", e);
						evaluations[i] = Double.NEGATIVE_INFINITY;
					}
				}
			}

			// now jump through hoops to sort population by evaluations
			Integer[] permutation = new Integer[population_size];
			for (int i = 0; i < population_size; i++)
				permutation[i] = i;
			Arrays.sort(permutation, new Comparator<Integer>() {
				@Override public int compare(Integer a, Integer b) {
					return -Double.compare(evaluations[a], evaluations[b]);
				}
			});

			System.out.println("population:");
			for (int i = 0; i < population_size; i++) {
				int j = permutation[i];
				System.out.println("  "+evaluations[j]+"\t"+Arrays.toString(population[j]));
			}
			
			double[][] selection = new double[selection_size][nvariables];
			for (int i = 0; i < selection_size; i++) {
				selection[i] = population[permutation[i]];
			}

			double[][] sampleT = Util.transpose(selection);
			for (int i = 0; i < nvariables; i++) {
				means [i] = alpha * Util.mean (sampleT[i]) + (1 - alpha) * means[i];
				stdevs[i] = alpha * Util.stdev(sampleT[i]) + (1 - alpha) * stdevs[i];
			}
		}
	}
}
