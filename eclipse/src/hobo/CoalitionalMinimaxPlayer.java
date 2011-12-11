package hobo;

import java.util.*;

public class CoalitionalMinimaxPlayer implements Player {
	private static final int N_KILLER_MOVES = 5;
	private final String name;
	private final double paranoia;
	private final int max_depth;

	public CoalitionalMinimaxPlayer(String name, double paranoia, int max_depth) {
		this.name = name;
		this.paranoia = paranoia;
		this.max_depth = max_depth;
		// store N_KILLER_MOVES killer moves per ply
		// max_depth*2 is an upper bound on number of plies
		this.killerMoves = new Decision[max_depth*2+1][N_KILLER_MOVES];
	}
	
	public String name() { return name; }
	public String toString() { return name; }

	public void perceive(Event e) {}

	public Decision decide(State s) {
		Set<Integer> coalition = selectCoalition(s);
		Decision d = minimax(s, max_depth, 0,
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

	private long total_nbranches = 0;
	private long total_nbranches_nterms = 0;

	private final Decision[][] killerMoves;

	// depth is measured in decisions, ply is measured in turns between min and max
	// depth is used to limit recursion, ply is used to keep track of killer moves for min and max
	public EvaluatedDecision minimax(State s, int depth, int ply, double a, double b, Set<Integer> coalition) {
		if (depth <= 0 || s.gameOver())
			return new EvaluatedDecision(null, utility(s, coalition));
		boolean maximizing = coalition.contains(s.currentPlayer());
		Decision dbest = null;
		
		LinkedList<Decision> ds = new LinkedList<Decision>(s.allPossibleDecisions());

		for (int i = N_KILLER_MOVES - 1; i >= 0; i--) {
			Decision d = killerMoves[ply][i];
			if (d != null && d.isLegal(s))
				ds.addFirst(d);
		}

		for (Decision d: ds) {
			if (!d.isLegal(s)) continue;

			total_nbranches++;

			State t = s.clone();
			d.apply(t);

			int newply = ply;
			if (	    maximizing && !coalition.contains(t.currentPlayer())
					|| !maximizing &&  coalition.contains(t.currentPlayer()))
				newply += 1;
			
			double u = minimax(t, depth - 1, newply, a, b, coalition).utility;

			if (depth == max_depth)
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

			if (b <= a) {
				// record this decision as a killer move

				// figure out if it's already in the list
				int i;
				for (i = N_KILLER_MOVES - 1; i >= 0; i--) {
					if (d.equals(killerMoves[ply][i]))
						break;
				}

				// if not, shift everything backward to make room for it in the front
				// otherwise shift everything in front of it backward to make room for it in the front
				if (i < 0) i = N_KILLER_MOVES - 1;
				for (; i > 0; i--)
					killerMoves[ply][i] = killerMoves[ply][i-1];
				killerMoves[ply][0] = d;

				// prune
				break;
			}
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
