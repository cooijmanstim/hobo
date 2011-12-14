package hobo;

import java.util.*;

public class RandomPlayer extends Player {
	public RandomPlayer(String name) {
		this.name = name;
	}

	public Decision decide(State s) {
		Set<Decision> ds = s.allPossibleDecisions();
		if (ds.isEmpty())
				return null;
		return Util.sample(ds, s.random);
	}
}
