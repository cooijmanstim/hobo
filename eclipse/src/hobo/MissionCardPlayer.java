package hobo;

import java.util.*;

public class MissionCardPlayer extends Player {
	
	public final int max_depth;
	private static long total_nbranches = 0;
	private static long total_nbranches_nterms = 0;
	
	public MissionCardPlayer(String name, int max_depth) {
		this.name = name;
		this.max_depth = max_depth;
	}
	
	public Decision decide(State s) {
		Decision d = minimax(s, max_depth,
                Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
                handle).decision;
//		System.out.println("average branching factor: "+(total_nbranches * 1.0 / total_nbranches_nterms));
		return d;
	}

	
	// (this is pretty gruesome.)
	public EvaluatedDecision minimax(State s, int depth, double a, double b, int inquirer) {
//		ArrayList<Railway> rails = new ArrayList<Railway>();
//		PlayerState state = s.currentPlayerState().clone();
//		Set<Mission> missions = state.missions;
//		missions.removeAll(s.currentPlayerState().completed_missions);
//		
//		for(Mission m : missions) {
//			rails = Util.getShortestWay(m.source, m.destination, new ArrayList<Railway>(), s);			
//		}
		
		if (depth <= 0 || s.gameOver())
			return new EvaluatedDecision(null, utility(s, inquirer));
		boolean maximizing = s.currentPlayer() == inquirer;
		Decision dbest = null;
		if (maximizing) {
			for (Decision d: s.allPossibleDecisions()) {
				total_nbranches++;

				State t = s.clone();
				t.applyDecision(d);

				double u = minimax(t, depth - 1, a, b, inquirer).utility;

//				if (depth == max_depth)
//					System.out.println(u + "\t" + d);

				if (u > a) {
					a = u;
					dbest = d;
				}

				if (b <= a)
					break;
			}
		} else {
			// let all other players make one decision
			while (s.currentPlayer() != inquirer && !s.gameOver()) {
				for (Decision d: s.allPossibleDecisions()) {
					total_nbranches++;

					State t = s.clone();
					t.applyDecision(d);
					
					// forward to maximizing player
					while (t.currentPlayer() != inquirer && !t.gameOver())
						t.switchTurns();

					double u = minimax(t, depth - 1, a, b, inquirer).utility;

					if (u < b) {
						b = u;
						dbest = d;
					}

					if (b <= a)
						break;
				}
				s.switchTurns();
			}
		}
		total_nbranches_nterms++;
		// if not maximizing, dbest is meaningless because it does not
		// have player information.  but EvaluatedDecision.decision is
		// only used on the top level, after maximizing.
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
		ArrayList<Railway> rails = new ArrayList<Railway>();
		PlayerState state = s.currentPlayerState().clone();
		Set<Mission> missions = state.missions;
		missions.removeAll(s.currentPlayerState().completed_missions);
		
		for(Mission m : missions) {
			rails = Util.getShortestWay(m.source, m.destination, new ArrayList<Railway>(), s);			
		}
		int length = 0;
		for(Railway r : rails)
			length = r.length;
		
		int total = 0;
		for (PlayerState ps: s.playerStates()) {
			if (ps.handle == inquirer) continue;
			total += ps.finalScore();
		}
		rails.removeAll(s.currentPlayerState().railways);
		int length2 = 0;
		for(Railway r : rails)
			length2 = r.length;
		
		length2 -= length;
		return s.playerState(inquirer).finalScore() - total + length2;
	}
}
