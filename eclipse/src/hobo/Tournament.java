package hobo;

import java.util.Arrays;

public class Tournament {
	public static void play() {
		// TODO: use the tuned belief parameters here
		String common_configuration = "verbose:false decision_time:1 belief_relevance_weight:1 belief_alpha:1 belief_beta:1 belief_gamma:1";
		String[] player_configurations = {
				// TODO: use the tuned parameters for each combination of strategic and use_signum in here
				"montecarlo strategic:false use_signum:false expansion_threshold:10 uct_weight:1 sigmoid_steepness:"+(1.0/70),
				"montecarlo strategic:false use_signum:true  expansion_threshold:10 uct_weight:1",
				"montecarlo strategic:true  use_signum:false expansion_threshold:10 uct_weight:1 sigmoid_steepness:"+(1.0/70)+" alpha:"+(1.0/20),
				"montecarlo strategic:true  use_signum:true  expansion_threshold:10 uct_weight:1 alpha:"+(1.0/20),
				"minimax",
		};

		System.out.println("configurations:");
		for (int i = 0; i < player_configurations.length; i++) {
			player_configurations[i] = "uncertain " + player_configurations[i] + " " + common_configuration;
			System.out.println(i+": "+player_configurations[i]);
		}

		System.out.println("results:");
		while (true) {
			for (int i = 0; i < player_configurations.length; i++) {
				for (int j = i; j < player_configurations.length; j++) {
					try {
						Player a = Player.fromConfiguration(player_configurations[i]),
						       b = Player.fromConfiguration(player_configurations[j]);
						Game g = new Game("verbose:false", a, b);
						g.play();
						// format: two lines per game
						// i {-1,0,1} j a.stats
						// j {-1,0,1} i b.stats
						int w = (int)Math.signum(g.state.aheadness(0));
						System.out.println(i+" "+(+w)+" "+j+" "+Arrays.toString(a.statistics()));
						System.out.println(j+" "+(-w)+" "+i+" "+Arrays.toString(b.statistics()));
					} catch (Throwable t) {
						t.printStackTrace();
					}
				}
			}
		}
	}
}
