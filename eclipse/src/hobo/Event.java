package hobo;

public class Event {
	public final State    state;
	public final Player   player;
	public final Decision decision;
	
	public Event(State s, Player p, Decision d) {
		state = s; player = p; decision = d;
	}
}
