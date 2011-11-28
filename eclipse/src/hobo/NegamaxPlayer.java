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

	private int handle;
	public Decision decide(State s) {
		// TODO: figure out some way to do this better
		handle = s.playerHandleByName(name);
		return negamax(s, 2, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1).decision;
	}

	public void illegal(State s, Decision d, String reason) {}
	public void loss   (State s) {}
	public void win    (State s) {}
	public void draw   (State s) {}
	
	public EvaluatedDecision negamax(State s, int depth, double a, double b, int inquirer) {
		int valence = inquirer == s.currentPlayer() ? 1 : -1;
		if (depth <= 0 || s.gameOver())
			return new EvaluatedDecision(null, valence * utility(s));
		Decision pv = null;
		for (Decision d: s.allPossibleDecisions()) {
			State t = s.clone();
			t.applyDecision(d);

			double u = -negamax(t, depth - 1, -b, -a, inquirer).utility;
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
		PlayerState ps = s.playerState(handle);
		if (s.gameOver()) {
			if (s.isDraw()) return 0;
			if (s.winner() == handle) return 1;
			return -1;
		}

		// even the highest score should have lower utility than winning
		return ps.finalScore() / 1000.0;
	}
}
