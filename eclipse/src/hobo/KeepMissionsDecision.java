package hobo;

import java.util.Set;

public class KeepMissionsDecision extends Decision {
	public final Set<Mission> missions;

	public KeepMissionsDecision(Set<Mission> missions) {
		this.missions = missions;
	}
	
	@Override public String toString() {
		return "KeepMissionsDecision(missions: "+missions+")";
	}
	
	@Override public boolean equals(Object o) {
		if (!(o instanceof KeepMissionsDecision))
			return false;
		KeepMissionsDecision that = (KeepMissionsDecision)o;
		return that.missions.equals(this.missions);
	}
	
	private static final int classHashCode = "KeepMissionsDecision".hashCode();	
	@Override public int hashCode() {
		return missions.hashCode() ^ classHashCode;
	}
	
	@Override public String reasonForIllegality(State s) {
		PlayerState p = s.currentPlayerState();

		if (p.drawn_card != null) return "you drew a card and now must decide which other card to draw";
		if (p.drawn_missions == null) return "you did not draw mission cards";
		if (missions.isEmpty()) return "you must keep at least one of the mission cards";
		if (!p.drawn_missions.containsAll(missions)) return "you drew different mission cards than the ones you're trying to keep now";		
		
		return null;
	}
	
	@Override public void apply(State s) {
		PlayerState p = s.currentPlayerState();

		// don't modify drawn_missions; it isn't cloned along with playerstate
		for (Mission m: p.drawn_missions) {
			if (missions.contains(m))
				p.missions.add(m);
			else
				s.missions.addLast(m);
		}
		p.drawn_missions = null;

		s.switchTurns();
	}
}
