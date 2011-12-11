package hobo;

import java.util.*;

public class CoalitionalMinimaxPlayer implements Player {
	private static final int N_KILLER_MOVES = 3;
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
	public void illegal(State s, Decision d, String reason) {}
	public void loss   (State s) {}
	public void win    (State s) {}
	public void draw   (State s) {}

	public Decision decide(State s) {
		System.out.println("----------------------------------------------------");
		System.out.println(name+" deciding...");
		boolean[] coalition = selectCoalition(s);
		System.out.println("assumed coalition "+Arrays.toString(coalition));
		Decision d = minimax(s, max_depth, 0,
		                     Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
		                     coalition).decision;
		System.out.println("assumed coalition "+Arrays.toString(coalition));
		System.out.println("average branching factor: "+(total_nbranches * 1.0 / total_nbranches_nterms));
		System.out.println("killer hit rate: "+(killer_hits*1.0/killer_tries)+"; "+killer_hits+"/"+killer_tries);
		return d;
	}

	private long total_nbranches = 0;
	private long total_nbranches_nterms = 0;
	private long killer_hits = 0, killer_tries = 0;

	private final Decision[][] killerMoves;

	private boolean[] selectCoalition(State s) {
		boolean[] coalition = new boolean[s.players().length];
		int coalition_size = 0;
		for (int i = 0; i < coalition.length; i++) {
			if (Math.random() > paranoia) {
				coalition[i] = true;
				coalition_size++;
			}
		}
		
		/* If all players are in the coalition, there are no minimizing
		 * plies in the tree, and so no alpha-beta pruning can happen.
		 * This sucks big time, so disallow it.  Just go paranoid instead.
		 */
		if (coalition_size == coalition.length)
			Arrays.fill(coalition, false);
		
		coalition[s.currentPlayer()] = true;
		return coalition;
	}

	// depth is measured in decisions, ply is measured in turns between min and max
	// depth is used to limit recursion, ply is used to keep track of killer moves for min and max
	public EvaluatedDecision minimax(State s, int depth, int ply, double a, double b, boolean[] coalition) {
		if (depth <= 0 || s.gameOver())
			return new EvaluatedDecision(null, utility(s, coalition));
		boolean maximizing = coalition[s.currentPlayer()];
		Decision dbest = null;
		
		Set<Decision> ds = new LinkedHashSet<Decision>(100);

		for (int i = N_KILLER_MOVES - 1; i >= 0; i--) {
			Decision d = killerMoves[ply][i];
			if (d != null && d.isLegal(s)) {
				ds.add(d);
				killer_tries++;
			}
		}

		ds.addAll(s.allPossibleDecisions());

		for (Decision d: ds) {
			total_nbranches++;

			State t = s.clone();
			d.apply(t);

			int newply = ply;
			if (	    maximizing && !coalition[t.currentPlayer()]
					|| !maximizing &&  coalition[t.currentPlayer()])
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
				
				if (i >= 0)
					killer_hits++;

				// if so, shift everything in front of it backward to make room for it in the front
				// if not, shift everything backward to make room for it in the front
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

	public static double utility(State s, boolean[] coalition) {
		// advantage of coalition over opposition
		int u = 0;
		for (PlayerState ps: s.playerStates())
			u += (coalition[ps.handle] ? 1 : -1) * ps.finalScore();
		return u;
	}
}
