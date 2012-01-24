package hobo;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Tournament {
	public static void main(String[] args) {
		play();
	}
	
	private static final int nthreads = 2;

	public static void play() {
		final String common_configuration = "verbose:false sample_size:3 decision_time:6000 belief_alpha:1.9";
		final String[] player_configurations = {
				"montecarlo strategic:false use_signum:true hybrid:true  expansion_threshold:15 uct_weight:1",
				"montecarlo strategic:false use_signum:true hybrid:false expansion_threshold:15 uct_weight:1",
				"montecarlo strategic:true  use_signum:true expansion_threshold:14 uct_weight:1 alpha:0.05 beta:2",
				"minimax alpha:1 beta:1 gamma:1 delta:2 zeta:2",
		};

		System.out.println("configurations:");
		for (int i = 0; i < player_configurations.length; i++) {
			player_configurations[i] = "uncertain " + player_configurations[i] + " " + common_configuration;
			System.out.println(i+": "+player_configurations[i]);
		}

		int npairs = (int)Util.binomial_coefficient(2, player_configurations.length);
		final long[] failures_and_tries = new long[2];
		ExecutorService pool = Executors.newFixedThreadPool(nthreads);

		int round = 0;

		System.out.println("results:");
		while (true) {
			System.out.println("round "+round);
			round++;

			Object[] future_logs = new Object[npairs];

			try {
				int m = 0;
				for (int i = 0; i < player_configurations.length; i++) {
					for (int j = i + 1; j < player_configurations.length; j++) {
						failures_and_tries[1]++;

						final int k = i, l = j;
						future_logs[m++] = pool.submit(new Callable<String>() {
							@Override public String call() {
								Player a = Player.fromConfiguration(player_configurations[k]),
								       b = Player.fromConfiguration(player_configurations[l]);
								Game g = new Game("verbose:false", a, b);
								g.play();
								// line format: k l {-1,0,1} [a.stats...] [b.stats...]
								int w = (int)Math.signum(g.state.aheadness(0));
								return k+" "+l+" "+(+w)+" "+Arrays.toString(a.statistics())+" "+Arrays.toString(b.statistics());
							}
						});
					}
				}
				
				for (m = 0; m < npairs; m++) {
					while (true) {
						try {
							String log = ((Future<String>)future_logs[m]).get();
							System.out.println(log);
							break;
						} catch (InterruptedException e) {
							continue;
						}
					}
				}
			} catch (ExecutionException e) {
				e.printStackTrace();
				failures_and_tries[0]++;
				if (failures_and_tries[1] > 100 && failures_and_tries[0] * 1.0 / failures_and_tries[1] > 0.5)
					throw new RuntimeException("too many failures -- something is wrong", e);
			}
		}
	}
}
