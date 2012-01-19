package hobo;

import java.util.*;

public class UncertainPlayer extends Player {
	private int sample_size, decision_time;
	public /* pretend final */ Belief belief;
	private final Player player;
	private long seed;
	private double relevance_weight, alpha, beta, gamma;
	private boolean verbose;

	public UncertainPlayer(String name, Player player, int sample_size, int decision_time, long seed, double relevance_weight, double alpha, double beta, double gamma, boolean verbose) {
		this.name = name;
		this.player = player;
		this.sample_size = sample_size;
		this.decision_time = decision_time;
		this.seed = seed;
		this.relevance_weight = relevance_weight;
		this.alpha = alpha;
		this.beta = beta;
		this.gamma = gamma;
		this.verbose = verbose;
	}

	public static UncertainPlayer fromConfiguration(String configuration) {
		String name = "uncertain";
		int sample_size = -1;
		int decision_time = 5;
		long seed = System.currentTimeMillis();
		double relevance_weight = 1, alpha = 1, beta = 1, gamma = 1;
		boolean verbose = true;

		for (Map.Entry<String,String> entry: Util.parseConfiguration(configuration).entrySet()) {
			String k = entry.getKey(), v = entry.getValue();
			if (k.equals("name"))                    name = v;
			if (k.equals("sample_size"))             sample_size = Integer.parseInt(v);
			if (k.equals("decision_time"))           decision_time = Integer.parseInt(v);
			if (k.equals("belief_seed"))             seed = Long.parseLong(v);
			if (k.equals("belief_relevance_weight")) relevance_weight = Double.parseDouble(v);
			if (k.equals("belief_alpha"))            alpha = Double.parseDouble(v);
			if (k.equals("belief_beta"))             beta = Double.parseDouble(v);
			if (k.equals("belief_gamma"))            gamma = Double.parseDouble(v);
			if (k.equals("verbose"))                 verbose = Boolean.parseBoolean(v);
		}

		return new UncertainPlayer(name, Player.fromConfiguration(configuration), sample_size, decision_time, seed, relevance_weight, alpha, beta, gamma, verbose);
	}

	@Override public void perceive(Event e) {
		if (e.player == null && e.decision == null) {
			belief.initialize(e.state);
		} else {
			belief.update(e);
		}
	}

	@Override public Decision decide(State s) {
		if (sample_size < 0)
			return decideByAssumption(s);
		else
			return decideBySampling(s);
	}

	@Override public void setHandle(int handle) {
		super.setHandle(handle);
		player.setHandle(handle);
		belief = new Belief(handle, seed, relevance_weight, alpha, beta, gamma);
	}
	
	@Override public void setDecisionTime(int decision_time) {
		this.decision_time = decision_time;
	}
	
	@Override public void setVerbose(boolean verbose) {
		// pfft
	}

	public Decision decideByAssumption(State s) {
		player.setDecisionTime(decision_time);
		State t = belief.maximumLikelihoodState(s);
		return player.decide(t);
	}

	public Decision decideBySampling(State s) {
		player.setVerbose(false);
		player.setDecisionTime((int)Math.round(decision_time * 1.0 / sample_size));

		Map<Decision,Double> averages = new LinkedHashMap<Decision,Double>();
		for (Decision d: s.allPossibleDecisions())
			averages.put(d, 0.0);

		for (int i = 0; i < sample_size; i++) {
			State t = belief.sample(s);
			for (EvaluatedDecision ed: player.evaluateDecisions(averages.keySet(), t)) {
				Double avg = averages.get(ed.decision);
				averages.put(ed.decision, (avg * i + ed.utility) / (i + 1));
			}
		}

		Decision dbest = null;
		double ubest = Double.NEGATIVE_INFINITY;
		for (Map.Entry<Decision, Double> entry: averages.entrySet()) {
			if (entry.getValue() > ubest) {
				dbest = entry.getKey();
				ubest = entry.getValue();
			}
		}
		return dbest;
	}

	@Override public Set<EvaluatedDecision> evaluateDecisions(Set<Decision> ds, State s) {
		return null; // pfft
	}
	
	@Override public double[] statistics() {
		return player.statistics();
	}
}
