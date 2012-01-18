package hobo;

import java.util.*;

public class MonteCarloPlayer extends Player {
	private int decision_time;
	private boolean verbose, strategic;
	private final MersenneTwisterFast random;

	public MonteCarloPlayer(String name, long seed, int decision_time, boolean verbose, boolean strategic) {
		this.name = name;
		this.decision_time = decision_time;
		this.verbose = verbose;
		this.strategic = strategic;
		this.random = new MersenneTwisterFast(seed);
	}
	
	public static MonteCarloPlayer fromConfiguration(String configuration) {
		String name = "carlo";
		int decision_time = 5;
		boolean verbose = true, strategic = false;
		long seed = System.currentTimeMillis();
		
		for (Map.Entry<String,String> entry: Util.parseConfiguration(configuration).entrySet()) {
			String k = entry.getKey(), v = entry.getValue();
			if (k.equals("name"))          name = v;
			if (k.equals("seed"))          seed = Long.parseLong(v);
			if (k.equals("decision_time")) decision_time = Integer.parseInt(v);
			if (k.equals("verbose"))       verbose = Boolean.parseBoolean(v);
			if (k.equals("strategic"))     strategic = Boolean.parseBoolean(v);
		}

		return new MonteCarloPlayer(name, seed, decision_time, verbose, strategic);
	}
	
	@Override public void setDecisionTime(int decision_time) {
		this.decision_time = decision_time;
	}

	@Override public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	private long total_ndecisions = 0;
	private long total_nnodes = 0;
	private long total_nsimulations = 0;
	
	public double averageNodesPerDecision() {
		return total_nnodes * 1.0 / total_ndecisions;
	}
	
	public double averageSimulations() {
		return total_nsimulations * 1.0 / total_ndecisions;
	}

	private boolean outOfTime = false;

	@Override public Decision decide(State s) {
		if (verbose) {
			System.out.println("----------------------------------------------------");
			System.out.println(name+" ("+handle+") deciding...");
		}
		Node tree = buildTree(s, null);
		total_ndecisions++;
		return tree.decide();
	}

	@Override public Set<EvaluatedDecision> evaluateDecisions(Set<Decision> ds, State s) {
		Node tree = buildTree(s, ds);
		return tree.evaluatedDecisions();
	}

	public Node buildTree(State s, Set<Decision> ds) {
		outOfTime = false;
		new Timer().schedule(new TimerTask() {
			public void run() {
				outOfTime = true;
			}
		}, decision_time * 1000);

		int simulation_count = 0;
		Node tree = new Node();
		// ds is for speed only
		if (ds != null)
			tree.all_possible_decisions = ds;
		while (!outOfTime) {
			tree.populate(s.clone());
			total_nsimulations++;
			simulation_count++;
		}
		if (verbose) {
			tree.printStatistics();
			System.out.println("performed "+simulation_count+" simulations");
		}
		return tree;
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
		
		public void printStatistics() {
			int count = 0;
			double total = 0, min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
			for (Node n: children.values()) {
				// expectedValue will be NaN
				if (n.visit_count == 0)
					continue;
				double u = n.expectedValue();
				
				total += u;
				count++;
				
				min = Math.min(min, u);
				max = Math.max(max, u);
			}
			double mean = total / count, variance = 0;
			for (Node n: children.values()) {
				// expectedValue will be NaN
				if (n.visit_count == 0)
					continue;
				double u = n.expectedValue();
				
				variance += Math.pow(u - mean, 2);
			}
			List<Map.Entry<Decision,Node>> dns = new ArrayList<Map.Entry<Decision,Node>>(children.entrySet());
			// zzzzzzz <<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
			Iterator<Map.Entry<Decision,Node>> idns = dns.iterator();
			// I HATE THIS CODE!  I HATE JAVA!
			while (idns.hasNext()) {
				Map.Entry<Decision,Node> dn = idns.next();
				if (Double.isNaN(dn.getValue().expectedValue()))
					idns.remove();
			}
			Collections.sort(dns, new Comparator<Map.Entry<Decision,Node>>() {
				public int compare(Map.Entry<Decision,Node> a, Map.Entry<Decision,Node> b) {
					// AUGH,   URRGHLL
					return -Double.compare(a.getValue().expectedValue(), b.getValue().expectedValue());
				}
			});
			System.out.println("up to 10 best: ");
			for (Map.Entry<Decision,Node> dn: dns.subList(0, Math.min(10, /* nyarrr */ dns.size()))) {
				System.out.println(dn.getValue().expectedValue()+"\t"+dn.getValue().visit_count+" visits\t"+dn.getKey());
			}
			System.out.println("count "+count+" min "+min+" max "+max+" mean "+mean+" stdev "+Math.sqrt(variance));
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
		
		public Set<EvaluatedDecision> evaluatedDecisions() {
			Set<EvaluatedDecision> eds = new HashSet<EvaluatedDecision>(children.size());
			for (Map.Entry<Decision, Node> dn: children.entrySet())
				eds.add(new EvaluatedDecision(dn.getKey(), dn.getValue().expectedValue()));
			return eds;
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

					double u;
					if (n.visit_count == 0) {
						// division by zero results in NaN, not infinity
						u = Double.POSITIVE_INFINITY;
					} else {
						double eu = n.expectedValue();
						u = (maximizing ? 1 : -1) * eu;
						u += Math.sqrt(2 * Math.log(visit_count) / n.visit_count); // UCT
					}

					if (Double.isNaN(u) || u > ubest) {
						ubest = u;
						dbest = d;
						nbest = n;
					}
				}
				dbest.apply(s, false);
				value = nbest.populate(s);
			} else {
				Decision d = sample(all_possible_decisions, s);
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
			total_nnodes++;
			return value;
		}

		public int playout(State s) {
			while (!s.gameOver()) {
				Decision d = chooseDecision(s);
				d.apply(s, false);
				total_nnodes++;
			}
			int value = (int)Math.signum(s.aheadness(handle)); // more than just win or loss
			total_value += value;
			visit_count++;
			return value;
		}

		public Decision chooseDecision(State s) {
			PlayerState ps = s.currentPlayerState();
			if (!strategic)
				return Util.sample(s.allPossibleDecisions(), random);
			Set<Decision> ds = new LinkedHashSet<Decision>(50);
			if (ps.drawn_card != null) {
				ds = DrawCardDecision.availableTo(s, ps, ds);
			} else if (ps.drawn_missions != null) {
				ds = KeepMissionsDecision.availableTo(s, ps, ds);
			} else {
				// endpoints for probability integral
						double dcd_end = 2 * Util.logsig(-ps.hand.size() / 20.0),
						crd_end = 0.995;
						//int totalCards = 110;
						//double dcd_end = 1 - ps.hand.size() / totalCards ,
						//crd_end = 0.99;
				

				while (ds.isEmpty()) {
					double x = random.nextDouble();
					if (x < dcd_end) {
						ds = DrawCardDecision.availableTo(s, ps, ds);
						if (ds.isEmpty())
							dcd_end = 0;
					} else if (x < crd_end) {
						ds = ClaimRailwayDecision.availableTo(s, ps, ds);
						if (ds.isEmpty())
							crd_end = 0; // i guess...
					} else {
						ds = DrawMissionsDecision.availableTo(s, ps, ds);
					}
				}
			}
			return sample(ds, s);
		}

		// sample from non-normalized distribution defined by weight()
		public Decision sample(Set<Decision> ds, State s) {
			double total_weight = 0;
			for (Decision d: ds)
				total_weight += weight(d, s);

			double index = random.nextDouble()*total_weight;
			for (Decision d: ds) {
				index -= weight(d, s);
				if (index <= 0)
					return d;
			}

			// shouldn't get here
			System.out.print("distribution: ");
			for (Decision d: ds)
				System.out.println(weight(d, s) + "\t" + d);
			System.out.println(index+" of mass left");
			throw new RuntimeException();
		}

		// defines a probability distribution over decisions,
		// conditioned on type
		public double weight(Decision d, State s) {
			if (d instanceof ClaimRailwayDecision) {
				Railway r = ((ClaimRailwayDecision)d).railway;
				PlayerState ps = s.playerState(d.player);
				double relevance = Double.NEGATIVE_INFINITY;
				int i = r.ordinal();
				for (Mission m: ps.missions)
					relevance = Math.max(relevance, m.railwayRelevance[i]);
				return (1 + relevance) * ((ClaimRailwayDecision)d).railway.score();
			} else if (d instanceof DrawCardDecision) {
				DrawCardDecision dcd = (DrawCardDecision)d;
				CardBag hand = s.playerState(d.player).hand;
				Color c = dcd.color == null ? s.deck.cardOnTop(s.random) : dcd.color;
				double oldu = hand.utilityAsHand();
				hand.add(c);
				double newu = hand.utilityAsHand();
				hand.remove(c);
				double du = newu - oldu;
				return Util.logsig(du); // keep the log-function because of negative values
			} else if (d instanceof DrawMissionsDecision) {
				return 1;
			} else if (d instanceof KeepMissionsDecision) {
				// smaller probably better
				return 1/((KeepMissionsDecision)d).missions.size();
			} else throw new RuntimeException("yarrr");
		}
		
		@Override public String toString() {
			return "Node(visit_count: "+visit_count+" total_value: "+total_value+" expected_value: "+expectedValue()+" fully_expanded: "+fully_expanded+" children: "+children+")";
		}
	}
}
