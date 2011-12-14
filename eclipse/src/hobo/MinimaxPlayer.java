package hobo;

import java.util.*;

public class MinimaxPlayer extends Player {
	private static final int N_KILLER_MOVES = 3,
	                         KILLER_MOVES_HORIZON = 2,
	                         MAX_DECISION_TIME = 10000;
	private final double paranoia;
	private final int max_depth;
	private final boolean best_reply;

	public MinimaxPlayer(String name, double paranoia, boolean best_reply, int max_depth) {
		this.name = name;
		this.paranoia = paranoia;
		this.best_reply = best_reply;
		this.max_depth = max_depth;
		// store N_KILLER_MOVES killer moves per ply
		// max_depth is an upper bound on number of plies
		this.killerMoves = new Decision[max_depth][N_KILLER_MOVES];
	}

	public Decision decide(State s) {
		System.out.println("----------------------------------------------------");
		System.out.println(name+" deciding...");
		boolean[] coalition = selectCoalition(s);
		System.out.println("assumed coalition "+Arrays.toString(coalition));
		Decision d = deepenIteratively(s, coalition);
		System.out.println("assumed coalition "+Arrays.toString(coalition));
		System.out.println("average branching factor: "+(total_nbranches * 1.0 / total_nbranches_nterms));
		System.out.println("killer hit rate: "+(killer_hits*1.0/killer_tries)+"; "+killer_hits+"/"+killer_tries);
		return d;
	}
	
	private boolean outOfTime = false;
	private Decision deepenIteratively(State s, boolean[] coalition) {
		outOfTime = false;
		Timer t = new Timer();
		t.schedule(new TimerTask() {
			public void run() {
				outOfTime = true;
			}
		}, MAX_DECISION_TIME);

		Decision d = null;
		try {
			for (int depth = 0; depth <= max_depth; depth++) {
				EvaluatedDecision ed = minimax(s, depth, 0,
				                               Double.NEGATIVE_INFINITY,
				                               Double.POSITIVE_INFINITY,
				                               coalition);
				System.out.println("depth "+depth+" "+ed.utility+"\t"+ed.decision);
				d = ed.decision;
			}
		} catch (OutOfTimeException e) {
			System.out.println("out of time");
		}
		
		if (!outOfTime)
			t.cancel();
		
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
			if (i == handle || Math.random() > paranoia) {
				coalition[i] = true;
				coalition_size++;
			}
		}

		/* If all players are in the coalition, there are no minimizing
		 * plies in the tree, and so no alpha-beta pruning can happen.
		 * This sucks big time, so disallow it.  Just go paranoid instead.
		 * (Except if paranoia == 0; then the coalition is intended to be
		 * maximal.)
		 */
		if (coalition_size == coalition.length && paranoia != 0)
			Arrays.fill(coalition, false);
		
		coalition[handle] = true;
		return coalition;
	}

	// depth is measured in decisions, ply is measured in turns between min and max
	// depth is used to limit recursion, ply is used to keep track of killer moves for min and max
	public EvaluatedDecision minimax(State s, int depth, int ply, double a, double b, boolean[] coalition)
			throws OutOfTimeException {
		if (outOfTime)
			throw new OutOfTimeException();
		if (depth <= 0 || s.gameOver())
			return new EvaluatedDecision(null, utility(s, coalition));

		Set<Decision> ds = new LinkedHashSet<Decision>(100);
		recallKillerMoves(ply, s, ds);

		boolean maximizing;
		if (best_reply) {
			// In best-reply search, we can't rely on s.currentPlayer(), because it depends
			// on who performed the last decision.  But we know that every other ply is
			// maximizing.
			maximizing = (ply % 2) == 0;
			if (ply == 0) {
				// In the end, we have to return a decision for ourselves, and not
				// for another coalition member.
				ds.addAll(s.allPossibleDecisionsFor(handle));
			} else {
				for (int player: s.players()) {
					// NOTE: if both true or both false
					if (coalition[player] == maximizing)
						ds.addAll(s.allPossibleDecisionsFor(player));
				}
			}
		} else {
			int player = s.currentPlayer();
			maximizing = coalition[player];
			ds.addAll(s.allPossibleDecisionsFor(player));
		}

		Decision dbest = null;
		for (Decision d: ds) {
			total_nbranches++;

			if (!best_reply && s.currentPlayer() != d.player)
				throw new RuntimeException("nooooooo: "+d);

			State t = s.clone();
			t.switchToPlayer(d.player); // not necessary, but to be clear
			d.apply(t);

			// next ply if decision ended a turn
			int newply = ply;
			if (t.currentPlayer() != d.player)
				newply++;
			
			double u = minimax(t, depth - 1, newply, a, b, coalition).utility;

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
				recordKillerMove(d, ply);
				break;
			}
		}
		total_nbranches_nterms++;
		return new EvaluatedDecision(dbest, maximizing ? a : b);
	}
	
	private void recordKillerMove(Decision d, int ply) {
		if (N_KILLER_MOVES == 0)
			return;

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
	}

	public void recallKillerMoves(int ply, State s, Set<Decision> ds) {
		if (N_KILLER_MOVES == 0)
			return;
		
		// look back several plies
		int step = best_reply ? 2 : s.players().length;
		int jmin = Math.max(0, ply - step * KILLER_MOVES_HORIZON);
		for (int j = ply; j >= jmin; j -= step) {
			for (int i = N_KILLER_MOVES - 1; i >= 0; i--) {
				Decision d = killerMoves[j][i];
				if (d != null && d.isLegalForPlayer(s)) {
					killer_tries++;
					ds.add(d);
				}
			}
		}
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
