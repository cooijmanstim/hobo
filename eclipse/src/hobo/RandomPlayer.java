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
		Set<Decision> ds = s.allPossibleDecisions();
		if (ds.isEmpty())
				return null;
		return Util.sample(ds, s.random);
	}

	public void illegal(State s, Decision d, String reason) {}
	public void loss   (State s) {}
	public void win    (State s) {}
	public void draw   (State s) {}
}
