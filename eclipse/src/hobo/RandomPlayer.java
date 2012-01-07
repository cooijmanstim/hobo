package hobo;

import java.util.*;

public class RandomPlayer extends Player {
	private final Random random;

	public RandomPlayer(String name, long seed) {
		this.name = name;
		this.random = new Random(seed);
	}
	
	public static RandomPlayer fromConfiguration(String configuration) {
		String name = "carlo";
		int decision_time = 5;
		long seed = System.currentTimeMillis();
		
		for (Map.Entry<String,String> entry: Util.parseConfiguration(configuration).entrySet()) {
			String k = entry.getKey(), v = entry.getValue();
			if (k.equals("name"))          name = v;
			if (k.equals("seed"))          seed = Long.parseLong(v);
		}

		return new RandomPlayer(name, seed);
	}

	public Decision decide(State s) {
		Set<Decision> ds = s.allPossibleDecisions();
		if (ds.isEmpty())
				return null;
		return Util.sample(ds, random);
	}
}
