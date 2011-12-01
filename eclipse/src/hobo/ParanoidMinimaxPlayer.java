package hobo;

import java.util.*;

public class ParanoidMinimaxPlayer implements Player {
	private final String name;

	public ParanoidMinimaxPlayer(String name) {
		this.name = name;
	}
	
	public String name() { return name; }
	public String toString() { return name; }

	public void perceive(Event e) {}
	
	public static final int MAX_DEPTH = 3;
	public Decision decide(State s) {
		Decision d = minimax(s, MAX_DEPTH,
		                     Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
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
	
	public static EvaluatedDecision minimax(State s, int depth, double a, double b, int inquirer) {
		if (depth <= 0 || s.gameOver())
			return new EvaluatedDecision(null, utility(s, inquirer));
		boolean maximizing = s.currentPlayer() == inquirer;
		Decision dbest = null;
		for (Decision d: s.allPossibleDecisions()) {
			total_nbranches++;

			State t = s.clone();
			t.applyDecision(d);

			double u = minimax(t, depth - 1, a, b, inquirer).utility;

			if (depth == MAX_DEPTH)
				System.out.println(u + "\t" + d);
			
			if (maximizing) {
				if (u > a) {
					a = u;
					dbest = d;
				}
			} else {
				if (u < b) {
					b = u;
					dbest = d;
				}
			}

			if (b <= a)
				break;
		}
		total_nbranches_nterms++;
		return new EvaluatedDecision(dbest, maximizing ? a : b);
	}

	private static class EvaluatedDecision {
		public final Decision decision;
		public final double utility;
		public EvaluatedDecision(Decision d, double u) {
			decision = d; utility = u;
		}
	}

	public static double utility(State s, int inquirer) {
		// advantage over the other players combined
		int total = 0;
		for (PlayerState ps: s.playerStates()) {
			if (ps.handle == inquirer) continue;
			total += ps.finalScore();
		}
		return s.playerState(inquirer).finalScore() - total;
	}
}
