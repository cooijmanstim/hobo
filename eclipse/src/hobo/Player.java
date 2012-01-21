package hobo;

import java.util.Set;

public abstract class Player {
	protected String name;
	protected int handle = -1;
	
	public String name() { return name; }
	public String toString() { return name; }
	public void setHandle(int handle) { this.handle = handle; }

	public abstract void setDecisionTime(int t);
	public abstract void setVerbose(boolean v);

	public void perceive(Event e) {};
	public abstract Decision decide(State s);

	public void loss(State s) {}
	public void win(State s) {}
	public void draw(State s) {}
	public void illegal(State s, Decision d, String reason) {}

	public static Player fromConfiguration(String s) {
		s = s.trim();
		
		int j = s.indexOf(' ');
		if (j == -1)
			j = s.length();
		String impl = s.substring(0, j);
		s = s.substring(j);

		if (impl.equals("random"))
			return RandomPlayer.fromConfiguration(s);
		else if (impl.equals("human"))
			return HumanPlayer.fromConfiguration(s);
		else if (impl.equals("minimax"))
			return MinimaxPlayer.fromConfiguration(s);
		else if (impl.equals("montecarlo"))
			return MonteCarloPlayer.fromConfiguration(s);
		else if (impl.equals("uncertain"))
			return UncertainPlayer.fromConfiguration(s);
		else
			throw new RuntimeException("no such player implementation: "+impl);
	}

	public double[] statistics() { return null; };
	
	public Set<EvaluatedDecision> evaluateDecisions(Set<Decision> ds, State s) {
		throw new RuntimeException();
	}
}
