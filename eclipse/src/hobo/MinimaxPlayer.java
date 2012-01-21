package hobo;

import java.util.*;

public class MinimaxPlayer extends Player {
	private static final int N_KILLER_MOVES = 3,
	                         KILLER_MOVES_HORIZON = 2;
	private final double paranoia;
	private int max_depth, decision_time;
	private boolean best_reply, verbose;

	public MinimaxPlayer(String name, double paranoia, boolean best_reply, boolean verbose, int max_depth, int decision_time) {
		this.name = name;
		this.paranoia = paranoia;
		this.best_reply = best_reply;
		this.verbose = verbose;
		this.max_depth = max_depth;
		this.decision_time = decision_time;
		// store N_KILLER_MOVES killer moves per ply
		// max_depth is an upper bound on number of plies
		this.killer_moves = new Decision[max_depth][N_KILLER_MOVES];
	}

	public static MinimaxPlayer fromConfiguration(String configuration) {
		String name = "minimax";
		double paranoia = 1;
		boolean best_reply = false;
		boolean verbose = true;
		int max_depth = 25;
		int decision_time = 5;
		
		for (Map.Entry<String,String> entry: Util.parseConfiguration(configuration).entrySet()) {
			String k = entry.getKey(), v = entry.getValue();
			if (k.equals("name"))          name = v;
			if (k.equals("paranoia"))      paranoia = Double.parseDouble(v);
			if (k.equals("best_reply"))    best_reply = Boolean.parseBoolean(v);
			if (k.equals("verbose"))       verbose = Boolean.parseBoolean(v);
			if (k.equals("max_depth"))     max_depth = Integer.parseInt(v);
			if (k.equals("decision_time")) decision_time = Integer.parseInt(v);
		}
		
		return new MinimaxPlayer(name, paranoia, best_reply, verbose, max_depth, decision_time);
	}
	
	@Override public void setDecisionTime(int decision_time) {
		this.decision_time = decision_time;
	}
	
	@Override public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public void output(String s) {
		if (verbose) System.out.println(s);
	}
	
	private long total_ndecisions = 0;
	private long total_depth = 0;
	private long total_depth_nterms = 0;
	private long total_nbranches = 0;
	private long total_nbranches_nterms = 0;
	private long killer_hits = 0, killer_tries = 0;

	public Decision decide(State s) {
		State t = s.clone(); // to be sure we don't mess with the real state
		if (verbose) {
			System.out.println("----------------------------------------------------");
			System.out.println(name+" ("+handle+") deciding...");
		}
		boolean[] coalition = selectCoalition(t);
		if (verbose) System.out.println("assumed coalition "+Arrays.toString(coalition));
		EvaluatedDecision ed = deepenIteratively(t, 0, coalition);
		if (verbose) {
			System.out.println("average branching factor: "+(total_nbranches * 1.0 / total_nbranches_nterms));
			System.out.println("killer hit rate: "+(killer_hits*1.0/killer_tries)+"; "+killer_hits+"/"+killer_tries);
		}
		assert(t.equals(s)); // to know when the undo code is broken
		total_ndecisions++;
		return ed.decision;
	}

	private boolean outOfTime = false;
	private EvaluatedDecision deepenIteratively(State s, int ply, boolean[] coalition) {
		outOfTime = false;
		Timer t = new Timer();
		t.schedule(new TimerTask() {
			public void run() {
				outOfTime = true;
			}
		}, decision_time * 1000);

		EvaluatedDecision ed = null;
		int depth = 0;
		try {
			for (depth = 0; depth <= max_depth; depth++) {
				ed = minimax(s, depth, ply,
				             Double.NEGATIVE_INFINITY,
				             Double.POSITIVE_INFINITY,
				             coalition);
				if (verbose) System.out.println("depth "+depth+" "+ed.utility+"\t"+ed.decision);
			}
		} catch (OutOfTimeException e) {
			if (verbose) System.out.println("out of time");
		} finally {
			total_depth += depth;
			total_depth_nterms++;
		}
		
		if (!outOfTime)
			t.cancel();
		
		return ed;
	}

	private final Decision[][] killer_moves;

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

			// calculate expected value of all possible outcomes of the decision
			double u = 0;
			Object[] outcomes = d.outcomeDesignators(s);
			for (int i = 0; i < outcomes.length; i++) {
				AppliedDecision ad = d.apply(s, outcomes[i], true);

				try {
					// next ply if decision ended a turn
					int newply = ply;
					if (s.currentPlayer() != d.player)
						newply++;

					u += d.outcomeLikelihood(s, outcomes[i]) * minimax(s, depth - 1, newply, a, b, coalition).utility;
				} finally {
					// recursion might throw outoftime
					ad.undo();
				}
 			}

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

	// decisions stored in the killer moves table should never be applied/undone
	// store them as clones of the decisions that were actually tried,
	// and clone them again before trying them in new nodes
	@SuppressWarnings("unused")
	private void recordKillerMove(Decision d, int ply) {
		if (N_KILLER_MOVES == 0)
			return;

		// figure out if it's already in the list
		int i;
		for (i = N_KILLER_MOVES - 1; i >= 0; i--) {
			if (d.equals(killer_moves[ply][i]))
				break;
		}
		
		if (i >= 0)
			killer_hits++;

		// if so, shift everything in front of it backward to make room for it in the front
		// if not, shift everything backward to make room for it in the front
		if (i < 0) i = N_KILLER_MOVES - 1;
		for (; i > 0; i--)
			killer_moves[ply][i] = killer_moves[ply][i-1];
		killer_moves[ply][0] = d;
	}

	@SuppressWarnings("unused")
	public void recallKillerMoves(int ply, State s, Set<Decision> ds) {
		if (N_KILLER_MOVES == 0)
			return;
		
		// look back several plies
		int step = best_reply ? 2 : s.players().length;
		int jmin = Math.max(0, ply - step * KILLER_MOVES_HORIZON);
		for (int j = ply; j >= jmin; j -= step) {
			for (int i = N_KILLER_MOVES - 1; i >= 0; i--) {
				Decision d = killer_moves[j][i];
				if (d != null && d.isLegalForPlayer(s)) {
					killer_tries++;
					ds.add(d);
				}
			}
		}
	}

	public static double utility(State s, boolean[] coalition) {
		// advantage of coalition over opposition
		int u = 0;
		for (PlayerState ps: s.playerStates())
			u += (coalition[ps.handle] ? 1 : -1) * ps.utility(s);
		return u;
	}
	
	public double averageBranchingFactor() {
		return total_nbranches * 1.0 / total_nbranches_nterms;
	}
	
	public double averageDepth() {
		return total_depth * 1.0 / total_depth_nterms;
	}
	
	public double averageNodesPerDecision() {
		return total_nbranches * 1.0 / total_ndecisions;
	}
	
	@Override public double[] statistics() {
		return new double[]{ averageNodesPerDecision(), averageBranchingFactor(), averageDepth() };
	}
}
