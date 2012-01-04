package hobo;

import java.util.*;

public class RandomPlayer extends Player {
	private final Random random = new Random();

	public RandomPlayer(String name) {
		this.name = name;
	}

	public Decision decide(State s) {
		Set<Decision> ds = s.allPossibleDecisions();
		if (ds.isEmpty())
				return null;
		return Util.sample(ds, random);
	}
}
