package hobo;

import java.util.*;

public class MonteCarloPlayer extends Player {
	// if use_signum is true, only win/draw/loss will be recorded
	// if false, aheadness will be recorded, and squashed by a sigmoid
	// with weight sigmoid_steepness
	// alpha is the sigmoid steepness for draw card probability in the playout
	// beta is the exponent for the hand utility, to make the distribution more
	// pronounced
	private int decision_time, expansion_threshold, chance_node_width;
	private double uct_weight, sigmoid_steepness, alpha, beta;
	private boolean verbose, strategic, use_signum;
	private final MersenneTwisterFast random;

	public MonteCarloPlayer(String name, long seed, int decision_time, int expansion_threshold, int chance_node_width, double uct_weight, double sigmoid_steepness, double alpha, double beta, boolean verbose, boolean strategic, boolean use_signum) {
		this.name = name;
		this.decision_time = decision_time;
		this.expansion_threshold = expansion_threshold;
		this.chance_node_width = chance_node_width;
		this.uct_weight = uct_weight;
		this.sigmoid_steepness = sigmoid_steepness;
		this.alpha = alpha;
		this.beta = beta;
		this.verbose = verbose;
		this.strategic = strategic;
		this.use_signum = use_signum;
		this.random = new MersenneTwisterFast(seed);
	}
	
	public static MonteCarloPlayer fromConfiguration(String configuration) {
		String name = "carlo";
		int decision_time = 5, expansion_threshold = 10, chance_node_width = 10;
		double uct_weight = 1, sigmoid_steepness = 25, alpha = 1/20.0, beta = 2;
		boolean verbose = true, strategic = false, use_signum = true;
		long seed = System.currentTimeMillis();
		
		for (Map.Entry<String,String> entry: Util.parseConfiguration(configuration).entrySet()) {
			String k = entry.getKey(), v = entry.getValue();
			if (k.equals("name"))                name = v;
			if (k.equals("seed"))                seed = Long.parseLong(v);
			if (k.equals("decision_time"))       decision_time = Integer.parseInt(v);
			if (k.equals("expansion_threshold")) expansion_threshold = Integer.parseInt(v);
			if (k.equals("chance_node_width"))   chance_node_width = Integer.parseInt(v);
			if (k.equals("uct_weight"))          uct_weight = Double.parseDouble(v);
			if (k.equals("sigmoid_steepness"))   sigmoid_steepness = Double.parseDouble(v);
			if (k.equals("alpha"))               alpha = Double.parseDouble(v);
			if (k.equals("beta"))                beta = Double.parseDouble(v);
			if (k.equals("verbose"))             verbose = Boolean.parseBoolean(v);
			if (k.equals("strategic"))           strategic = Boolean.parseBoolean(v);
			if (k.equals("use_signum"))          use_signum = Boolean.parseBoolean(v);
		}

		return new MonteCarloPlayer(name, seed, decision_time, expansion_threshold, chance_node_width, uct_weight, sigmoid_steepness, alpha, beta, verbose, strategic, use_signum);
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
	
	@Override public double[] statistics() {
		return new double[]{ averageNodesPerDecision(), averageSimulations() };
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
		Node tree = new Node(false);
		// ds is for speed only
		if (ds != null)
			tree.all_possible_decisions = ds;
		while (!outOfTime) {
			tree.populate(s.clone(), null);
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
		private int visit_count = 0;
		private int total_value = 0;

		private boolean chance_node;

		// for decision nodes
		private boolean fully_expanded = false;
		private Set<Decision> all_possible_decisions = null;
		private Map<Decision,Node> children = null;

		// for draw card nodes; use a limited number of seeds
		private long[] seeds;
		private Node[] outcomes;

		public Node(boolean chance_node) {
			this.chance_node = chance_node;

			if (chance_node) {
				seeds = new long[chance_node_width];
				for (int i = 0; i < chance_node_width; i++)
					// TODO: improve
					seeds[i] = random.nextInt();
				outcomes = new Node[chance_node_width];
			} else {
				children = new LinkedHashMap<Decision,Node>(100);
			}
		}

		// pick the best decision after all the simulations are done
		public Decision decide() {
			assert(!chance_node);

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

		// return the evaluation of the top-level decisions
		public Set<EvaluatedDecision> evaluatedDecisions() {
			assert(!chance_node);

			Set<EvaluatedDecision> eds = new HashSet<EvaluatedDecision>(children.size());
			for (Map.Entry<Decision, Node> dn: children.entrySet())
				eds.add(new EvaluatedDecision(dn.getKey(), dn.getValue().expectedValue()));
			return eds;
		}

		public double expectedValue() {
			double ev = total_value * 1.0 / visit_count;
			if (!use_signum)
				ev = Util.logsig(sigmoid_steepness * ev);
			return ev;
		}
		
		// get or create child node
		public Node childFor(Decision d) {
			assert(!chance_node);
			Node n = children.get(d);
			if (n == null) {
				n = new Node(d instanceof DrawCardDecision || d instanceof DrawMissionsDecision);
				children.put(d, n);
			}
			return n;
		}
		
		public Node childFor(int seed_index) {
			assert(chance_node);
			Node n = outcomes[seed_index];
			if (n == null) {
				n = new Node(false);
				outcomes[seed_index] = n;
			}
			return n;
		}
		
		public void fullyExpand() {
			assert(!chance_node);
			for (Decision d: all_possible_decisions)
				childFor(d);
			fully_expanded = true;
			all_possible_decisions = null;
		}

		// select, expand
		public int populate(State s, Decision dprev) {
			Node nnext = null;
			Decision dnext = null;

			if (chance_node) {
				int seed_index = random.nextInt(chance_node_width);
				s.random.setSeed(seeds[seed_index]);
				dprev.apply(s, false);
				nnext = childFor(seed_index);
				dnext = null;
			} else {
				if (dprev != null)
					dprev.apply(s, false);

				// cache possible decisions
				if (all_possible_decisions == null && !fully_expanded)
					all_possible_decisions = s.allPossibleDecisions();

				if (visit_count >= expansion_threshold && !fully_expanded)
					fullyExpand();

				if (fully_expanded) {
					// 2 players or fully paranoid
					boolean maximizing = s.currentPlayer() == handle;

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
							u += 2 * uct_weight * Math.sqrt(Math.log(visit_count) / n.visit_count); // UCT
						}

						if (Double.isNaN(u) || u > ubest) {
							ubest = u;
							dnext = d;
							nnext = n;
						}
					}
				} else {
					dnext = sample(all_possible_decisions, s);
					nnext = childFor(dnext);
				}
			}

			// if visit count is zero, we created our new node for this simulation,
			// so do a playout and be done with it.  otherwise select/expand further.
			int value = nnext.visit_count == 0 ? nnext.playout(s, dnext) : nnext.populate(s, dnext);

			total_value += value;
			visit_count++;
			total_nnodes++;
			return value;
		}

		public int playout(State s, Decision dprev) {
			s.random.setSeed(random.nextInt());
			if (dprev != null)
				dprev.apply(s, false);

			while (!s.gameOver()) {
				Decision d = chooseDecision(s);
				d.apply(s, false);
				total_nnodes++;
			}
			int value = s.aheadness(handle);
			if (use_signum)
				value = (int)Math.signum(value);
			total_value += value;
			visit_count++;
			return value;
		}


		// playout strategy
		public Decision chooseDecision(State s) {
			if (!strategic)
				return Util.sample(s.allPossibleDecisions(), random);
			
			PlayerState ps = s.currentPlayerState();
			Set<Decision> ds = new LinkedHashSet<Decision>(50);
			if (ps.drawn_card != null) {
				ds = DrawCardDecision.availableTo(s, ps, ds);
			} else if (ps.drawn_missions != null) {
				ds = KeepMissionsDecision.availableTo(s, ps, ds);
			} else {
				// endpoints for probability integral
				double dcd_end = 2 * Util.logsig(-ps.hand.size() * alpha),
				       crd_end = 0.995;

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
				hand.add(c);
				double newu = hand.utilityAsHand();
				hand.remove(c);
				return Math.pow(newu, beta);
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
		
		public void printStatistics() {
			assert(!chance_node);
			
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
	}
}
