package hobo;

import java.util.*;

public class MonteCarloPlayer extends Player {
	private static final int MAX_DECISION_TIME = 5000;
	private static final Random random = new Random();

	public MonteCarloPlayer(String name) {
		this.name = name;
	}

	private boolean outOfTime = false;
	
	public Decision decide(State s) {
		System.out.println("----------------------------------------------------");
		System.out.println(name+" ("+handle+") deciding...");

		outOfTime = false;
		new Timer().schedule(new TimerTask() {
			public void run() {
				outOfTime = true;
			}
		}, MAX_DECISION_TIME);

		int simulation_count = 0;
		Node tree = new Node();
		while (!outOfTime) {
			tree.populate(s.clone());
			simulation_count++;
		}
		Decision d = tree.decide();
		tree = null;

		System.out.println("performed "+simulation_count+" simulations");
		return d;
	}

	private class Node {
		private static final int FULL_EXPANSION_THRESHOLD = 10;
		
		private int visit_count = 0;
		private int total_value = 0;
		private boolean fully_expanded = false;

		private Set<Decision> all_possible_decisions = null;
		private final Map<Decision,Node> children = new LinkedHashMap<Decision,Node>(100);

		public Node() {}

		public double expectedValue() {
			return total_value * 1.0 / visit_count;
		}

		public Node childFor(Decision d) {
			Node n = children.get(d);
			if (n == null) {
				n = new Node();
				children.put(d, n);
			}
			return n;
		}
		
		public void fullyExpand() {
			for (Decision d: all_possible_decisions)
				childFor(d);
			fully_expanded = true;
		}
		
		public Decision decide() {
			double ubest = Double.NEGATIVE_INFINITY;
			Decision dbest = null;
			for (Map.Entry<Decision,Node> dn: children.entrySet()) {
				Decision d = dn.getKey();
				Node n = dn.getValue();

				double u = n.expectedValue();
				if (u > ubest) {
					ubest = u;
					dbest = d;
				}
			}
			return dbest;
		}

		public int populate(State s) {
			// cache all possible decisions
			if (all_possible_decisions == null && !fully_expanded)
				all_possible_decisions = s.allPossibleDecisions();
			// ensure cache is correct
			//if (all_possible_decisions != null)
			//	assert(all_possible_decisions.equals(s.allPossibleDecisions()));
			
			if (visit_count >= FULL_EXPANSION_THRESHOLD)
				fullyExpand();
			
			int value;
			if (fully_expanded) {
				// 2 players or fully paranoid
				boolean maximizing = s.currentPlayer() == handle;
				
				Node nbest = null;
				Decision dbest = null;
				double ubest = Double.NEGATIVE_INFINITY;
				for (Map.Entry<Decision,Node> dn: children.entrySet()) {
					Decision d = dn.getKey();
					Node n = dn.getValue();

					// TODO: uct
					double u = (maximizing ? 1 : -1) * n.expectedValue();
					if (u > ubest) {
						ubest = u;
						dbest = d;
						nbest = n;
					}
				}
				dbest.apply(s, false);
				value = nbest.populate(s);
			} else {
				Decision d = Util.sample(all_possible_decisions, random); // TODO: distribution
				d.apply(s, false);
				Node n = childFor(d);
				if (n.visit_count == 0) {
					// new node, play it out
					value = n.playout(s);
				} else {
					// existing node, search further
					value = n.populate(s);
				}
			}
			
			total_value += value;
			visit_count++;
			return value;
		}

		public int playout(State s) {
			while (!s.gameOver()) {
				Decision d = Util.sample(s.allPossibleDecisions(), random); // TODO: distribution
				d.apply(s, false);
			}
			int value = s.aheadness(handle); // more than just win or loss
			total_value += value;
			visit_count++;
			return value;
		}
		
		@Override public String toString() {
			return "Node(visit_count: "+visit_count+" total_value: "+total_value+" expected_value: "+expectedValue()+" fully_expanded: "+fully_expanded+" children: "+children+")";
		}
	}
}
