package hobo;

import java.util.*;

public class UncertainPlayer extends Player {
	private int sample_size, decision_time;
	private /* pretend final */ Belief belief;
	private final Player player;

	public UncertainPlayer(String name, Player player, int sample_size, int decision_time) {
		this.name = name;
		this.player = player;
		this.sample_size = sample_size;
		this.decision_time = decision_time;
	}
	
	public static UncertainPlayer fromConfiguration(String configuration) {
		String name = "uncertain";
		int sample_size = 100;
		int decision_time = 5;

		for (Map.Entry<String,String> entry: Util.parseConfiguration(configuration).entrySet()) {
			String k = entry.getKey(), v = entry.getValue();
			if (k.equals("name"))          name = v;
			if (k.equals("sample_size"))   sample_size = Integer.parseInt(v);
			if (k.equals("decision_time")) decision_time = Integer.parseInt(v);
		}

		return new UncertainPlayer(name, Player.fromConfiguration(configuration), sample_size, decision_time);
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
		belief = new Belief(handle);
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
}
