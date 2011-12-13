package hobo;

import java.util.*;

public class BestReplyMinimaxPlayer extends Player {
	private static final int N_KILLER_MOVES = 3,
	                         KILLER_MOVES_HORIZON = 2,
	                         MAX_DECISION_TIME = 60000;
	public final int max_depth;

	public BestReplyMinimaxPlayer(String name, int max_depth) {
		this.name = name;
		this.max_depth = max_depth;
		// store N_KILLER_MOVES killer moves per ply
		// max_depth*2 is an upper bound on number of plies
		this.killerMoves = new Decision[max_depth*2+1][N_KILLER_MOVES];
	}
	
	public Decision decide(State s) {
		System.out.println("----------------------------------------------------");
		System.out.println(name+" deciding...");
		boolean[] coalition = new boolean[s.players().length];
		coalition[handle] = true;
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

	public EvaluatedDecision minimax(State s, int depth, int ply, double a, double b, boolean coalition[])
			throws OutOfTimeException {
		if (outOfTime)
			throw new OutOfTimeException();
		if (depth <= 0 || s.gameOver())
			return new EvaluatedDecision(null, utility(s, coalition));

		Set<Decision> ds = new LinkedHashSet<Decision>(100);
		recallKillerMoves(ply, s, ds);
		
		boolean maximizing = (ply % 2) == 0;
		if (maximizing) {
			ds.addAll(s.allPossibleDecisionsFor(handle));
		} else {
			// gather decisions for all other players
			for (int handle: s.players()) {
				if (coalition[handle])
					continue;
				ds.addAll(s.allPossibleDecisionsFor(handle));
			}
		}

		Decision dbest = null;
		for (Decision d: ds) {
			total_nbranches++;

			State t = s.clone();
			d.apply(t);

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
	
	private static class EvaluatedDecision {
		public final Decision decision;
		public final double utility;
		public EvaluatedDecision(Decision d, double u) {
			decision = d; utility = u;
		}
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
		// XXX: this also considers irrelevant plies where it's not the player's turn
		int jmin = Math.max(0, ply - KILLER_MOVES_HORIZON);
		for (int j = ply; j >= jmin; j--) {
			for (int i = N_KILLER_MOVES - 1; i >= 0; i--) {
				Decision d = killerMoves[j][i];
				// FIXME: using isLegal here will not give us all opponents' moves
				if (d != null && d.isLegal(s)) {
					killer_tries++;
					ds.add(d);
				}
			}
		}
	}

	public double utility(State s, boolean[] coalition) {
		// advantage of coalition over opposition
		int u = 0;
		for (PlayerState ps: s.playerStates())
			u += (coalition[ps.handle] ? 1 : -1) * ps.finalScore();
		return u;
	}
	
	private class OutOfTimeException extends Exception {}
}
