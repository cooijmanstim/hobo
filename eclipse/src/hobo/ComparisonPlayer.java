package hobo;

import java.util.*;

public class ComparisonPlayer implements Player {
	private final String name;

	public ComparisonPlayer(String name) {
		this.name = name;
		m = new ParanoidMinimaxPlayer(name);
		n = new NegamaxPlayer(name);
	}
	
	public String name() { return name; }
	public String toString() { return name; }

	public void perceive(Event e) {}

	private final Player m, n;
	public Decision decide(State s) {
		Decision md = m.decide(s);
		System.out.println("m: "+md);
		Decision nd = n.decide(s);
		System.out.println("n: "+nd);
		if (!md.toString().equals(nd.toString())) {
			System.out.println("m: "+md);
			System.out.println("n: "+nd);
			System.exit(1);
		}
		return md;
	}

	public void illegal(State s, Decision d, String reason) {}
	public void loss   (State s) {}
	public void win    (State s) {}
	public void draw   (State s) {}
}
