package hobo;

import java.util.*;

public class RandomPlayer implements Player {
	private final String name;

	public RandomPlayer(String name) {
		this.name = name;
	}
	
	public String name() { return name; }
	public String toString() { return name; }

	public void perceive(Event e) {}
	public Decision decide(State s) {
		List<Decision> ds = s.allPossibleDecisions();
		if (ds.isEmpty())
				return null;
		return ds.get(s.random.nextInt(ds.size()));
	}

	public void illegal(State s, Decision d, String reason) {}
	public void loss   (State s) {}
	public void win    (State s) {}
	public void draw   (State s) {}
}
