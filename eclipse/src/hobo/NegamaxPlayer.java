package hobo;

import java.util.*;

public class NegamaxPlayer implements Player {
	private final String name;
	
	public NegamaxPlayer(String name) {
		this.name = name;
	}
	
	public String name() { return name; }
	public String toString() { return name; }

	public void perceive(Event e) {}
	public void illegal(State s, Decision d) {}
	public void loss(State s) {}
	public void win(State s) {}
	public void draw(State s) {}

	public Decision decide(State s) {
		return negamax(s, 10, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1).decision;
	}

	public EvaluatedDecision negamax(State s, int depth, double a, double b, int valence) {
		if (depth <= 0 || s.gameOver())
			return new EvaluatedDecision(null, valence * utility(s));
		Decision pv = null;
		for (Decision d: possibleDecisions(s)) {
			State t = s.clone();
			t.applyDecision(d);
			t.switchTurns();

			double u = -negamax(t, depth - 1, -b, -a, -valence).utility;
			if (u > a) {
				a = u;
				pv = d;
			}

			if (a >= b)
				break;
		}
		return new EvaluatedDecision(pv, a);
	}
	
	private static class EvaluatedDecision {
		public final Decision decision;
		public final double utility;
		public EvaluatedDecision(Decision d, double u) {
			decision = d; utility = u;
		}
	}
	
	public double utility(State s) {
		if (s.gameOver()) {
			if (s.isDraw()) return 0;
			if (s.winner().equals(name)) return 1;
			return -1;
		}
		return 0;
	}
	
	public List<Decision> possibleDecisions(State s) {
		List<Decision> ds = new ArrayList<Decision>();
		for (int x = 0; x < s.size; x++) {
			for (int y = 0; y < s.size; y++) {
				Decision d = new Decision(x, y);
				if (s.isLegal(d))
					ds.add(d);
			}
		}
		Collections.shuffle(ds); // some stochasticitude to make things interesting
		return ds;
	}
}
