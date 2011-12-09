package hobo;

import java.util.*;

public class CoalitionalMinimaxPlayer implements Player {
	private final String name;
	private final double paranoia;

	public CoalitionalMinimaxPlayer(String name, double paranoia) {
		this.name = name;
		this.paranoia = paranoia;
	}
	
	public String name() { return name; }
	public String toString() { return name; }

	public void perceive(Event e) {}

	public static final int MAX_DEPTH = 3;
	public Decision decide(State s) {
		Set<Integer> coalition = selectCoalition(s);
		Decision d = minimax(s, MAX_DEPTH,
		                     Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
		                     coalition).decision;
		System.out.println("assumed coalition "+coalition);
		System.out.println("average branching factor: "+(total_nbranches * 1.0 / total_nbranches_nterms));
		return d;
	}

	private Set<Integer> selectCoalition(State s) {
		Set<Integer> coalition = new HashSet<Integer>(s.players().length);
		for (int player: s.players()) {
			if (Math.random() > paranoia)
				coalition.add(player);
		}
		coalition.add(s.playerHandleByName(name));
		return coalition;
	}

	public void illegal(State s, Decision d, String reason) {}
	public void loss   (State s) {}
	public void win    (State s) {}
	public void draw   (State s) {}

	private static long total_nbranches = 0;
	private static long total_nbranches_nterms = 0;
	
	public static EvaluatedDecision minimax(State s, int depth, double a, double b, Set<Integer> coalition) {
		if (depth <= 0 || s.gameOver())
			return new EvaluatedDecision(null, utility(s, coalition));
		boolean maximizing = coalition.contains(s.currentPlayer());
		Decision dbest = null;
		for (Decision d: s.allPossibleDecisions()) {
			total_nbranches++;

			State t = s.clone();
			t.applyDecision(d);

			double u = minimax(t, depth - 1, a, b, coalition).utility;

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

	public static double utility(State s, Set<Integer> coalition) {
		// advantage of coalition over opposition
		int u = 0;
		for (PlayerState ps: s.playerStates())
			u += (coalition.contains(ps.handle) ? 1 : -1) * ps.finalScore();
		return u;
	}
}
