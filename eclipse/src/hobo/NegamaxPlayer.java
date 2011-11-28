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
	
	public static final int MAX_DEPTH = 3;
	public Decision decide(State s) {
		Decision d = negamax(s, MAX_DEPTH,
		                     Double.NEGATIVE_INFINITY,
		                     Double.POSITIVE_INFINITY,
		                     s.playerHandleByName(name)).decision;
		System.out.println("average branching factor: "+(total_nbranches * 1.0 / total_nbranches_nterms));
		return d;
	}

	public void illegal(State s, Decision d, String reason) {}
	public void loss   (State s) {}
	public void win    (State s) {}
	public void draw   (State s) {}

	private static long total_nbranches = 0;
	private static long total_nbranches_nterms = 0;
	
	public static EvaluatedDecision negamax(State s, int depth, double a, double b, int inquirer) {
		if (depth <= 0 || s.gameOver())
			return new EvaluatedDecision(null, (inquirer == s.currentPlayer() ? 1 : -1) * utility(s, inquirer));
		Decision pv = null;
		for (Decision d: s.allPossibleDecisions()) {
			total_nbranches++;

			State t = s.clone();
			t.applyDecision(d);

			double u = t.currentPlayer() == s.currentPlayer()
					?  negamax(t, depth - 1,  a,  b, inquirer).utility
					: -negamax(t, depth - 1, -b, -a, inquirer).utility;
			if (depth == MAX_DEPTH)
				System.out.println(u + "\t" + d);
			if (u > a) {
				a = u;
				pv = d;
			}

			//if (a >= b)
			//	break;
		}
		total_nbranches_nterms++;
		return new EvaluatedDecision(pv, a);
	}

	private static class EvaluatedDecision {
		public final Decision decision;
		public final double utility;
		public EvaluatedDecision(Decision d, double u) {
			decision = d; utility = u;
		}
	}

	public static double utility(State s, int inquirer) {
		// advantage over best other player
		int maxother = Integer.MIN_VALUE;
		for (PlayerState ps: s.playerStates()) {
			if (ps.handle == inquirer) continue;
			maxother = Math.max(maxother, ps.finalScore());
		}
		return s.playerState(inquirer).finalScore() - maxother;
	}
}
