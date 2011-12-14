package hobo;

import java.util.*;

public class MaxNPlayer extends Player {
	private static final int MAX_DECISION_TIME = 2000;
	private final int max_depth;

	public MaxNPlayer(String name, int max_depth) {
		this.name = name;
		this.max_depth = max_depth;
	}

	public Decision decide(State s) {
//		System.out.println("----------------------------------------------------");
//		System.out.println(name+" deciding...");
		Decision d = deepenIteratively(s);
		System.out.println("average branching factor: "+(total_nbranches * 1.0 / total_nbranches_nterms));
		return d;
	}

	private boolean outOfTime = false;
	private Decision deepenIteratively(State s) {
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
				EvaluatedDecision ed = maxn(s, depth);
				System.out.println("depth "+depth+" "+Arrays.toString(ed.utility)+"\t"+ed.decision);
				d = ed.decision;
			}
		} catch (OutOfTimeException e) {
//			System.out.println("out of time");
		}
		
		if (!outOfTime)
			t.cancel();
		
		return d;
	}

	private long total_nbranches = 0;
	private long total_nbranches_nterms = 0;

	public EvaluatedDecision maxn(State s, int depth) throws OutOfTimeException {
		if (outOfTime)
			throw new OutOfTimeException();
		if (depth <= 0 || s.gameOver())
			return new EvaluatedDecision(null, utility(s));
		Decision dbest = null;
		double[] ubest = null;
		int handle = s.currentPlayer();
		for (Decision d: s.allPossibleDecisions()) {
			total_nbranches++;

			State t = s.clone();
			t.applyDecision(d);

			double[] u = maxn(t, depth - 1).utility;

			if (ubest == null || u[handle] > ubest[handle]) {
				ubest = u;
				dbest = d;
			} else if (ubest != null && u[handle] == ubest[handle]) {
				// break ties in a way that disadvantages the best opponent
				double umaxother = Double.NEGATIVE_INFINITY,
				       ubestmaxother = Double.NEGATIVE_INFINITY;
				for (int i = 0; i < u.length; i++) {
					if (i == handle) continue;
					umaxother = Math.max(u[i], umaxother);
					ubestmaxother = Math.max(ubest[i], ubestmaxother);
				}
				if (umaxother < ubestmaxother) {
					ubest = u;
					dbest = d;
				}
			}
		}
		total_nbranches_nterms++;
		return new EvaluatedDecision(dbest, ubest);
	}

	private static class EvaluatedDecision {
		public final Decision decision;
		public final double[] utility;
		public EvaluatedDecision(Decision d, double[] u) {
			decision = d; utility = u;
		}
	}

	public static double[] utility(State s) {
		double[] utility = new double[s.players().length];
		for (int handle: s.players())
			utility[handle] = s.playerState(handle).utility(s);
		return utility;
	}
}
