package hobo;

import java.util.*;

public class BestReplyMinimaxPlayer extends Player {
	public final int max_depth;

	public BestReplyMinimaxPlayer(String name, int max_depth) {
		this.name = name;
		this.max_depth = max_depth;
	}
	
	public Decision decide(State s) {
		System.out.println("----------------------------------------------------");
		System.out.println(name+" deciding...");
		boolean[] coalition = new boolean[s.players().length];
		coalition[handle] = true;
		Decision d = minimax(s, max_depth, 0,
		                     Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
		                     coalition).decision.decision;
		System.out.println("average branching factor: "+(total_nbranches * 1.0 / total_nbranches_nterms));
		//System.out.println("killer hit rate: "+(killer_hits*1.0/killer_tries)+"; "+killer_hits+"/"+killer_tries);
		return d;
	}

	private static long total_nbranches = 0;
	private static long total_nbranches_nterms = 0;

	public EvaluatedDecision minimax(State s, int depth, int ply, double a, double b, boolean coalition[]) {
		if (depth <= 0 || s.gameOver())
			return new EvaluatedDecision(null, utility(s, coalition));

		Set<PlayerDecision> pds = new LinkedHashSet<PlayerDecision>(100);
		
		boolean maximizing = (ply % 2) == 0;
		if (maximizing) {
			for (Decision d: s.allPossibleDecisionsFor(handle))
				pds.add(new PlayerDecision(handle, d));
		} else {
			// gather decisions for all other players
			for (int player: s.players()) {
				if (coalition[player])
					continue;
				for (Decision d: s.allPossibleDecisionsFor(player))
					pds.add(new PlayerDecision(player, d));
			}
		}

		PlayerDecision pdbest = null;
		for (PlayerDecision pd: pds) {
			total_nbranches++;

			State t = s.clone();
			t.switchToPlayer(pd.player);
			t.applyDecision(pd.decision);

			int newply = ply;
			if (t.currentPlayer() != pd.player)
				newply++;

			double u = minimax(t, depth - 1, newply, a, b, coalition).utility;

			if (maximizing) {
				if (u > a) {
					a = u;
					pdbest = pd;
				}
			} else {
				if (u < b) {
					b = u;
					pdbest = pd;
				}
			}

			if (b <= a)
				break;
		}
		total_nbranches_nterms++;
		return new EvaluatedDecision(pdbest, maximizing ? a : b);
	}

	// decision along with player
	private static class PlayerDecision {
		public final int player;
		public final Decision decision;
		public PlayerDecision(int player, Decision decision) {
			this.player = player;
			this.decision = decision;
		}
	}
	
	private static class EvaluatedDecision {
		public final PlayerDecision decision;
		public final double utility;
		public EvaluatedDecision(PlayerDecision d, double u) {
			decision = d; utility = u;
		}
	}

	public double utility(State s, boolean[] coalition) {
		// advantage of coalition over opposition
		int u = 0;
		for (PlayerState ps: s.playerStates())
			u += (coalition[ps.handle] ? 1 : -1) * ps.finalScore();
		return u;
	}
}
