package hobo;

public class Event {
	public final State    state;
	public final Player   player;
	public final Decision decision;
	public final AppliedDecision applied_decision;

	public Event(State s, Player p, Decision d, AppliedDecision ad) {
		state = s; player = p; decision = d; applied_decision = ad;
	}
	
	public String toString() {
		return "Event(" + player.name + " " + decision + " " + applied_decision + ")";
	}
}
