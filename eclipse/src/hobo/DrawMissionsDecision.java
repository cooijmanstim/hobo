package hobo;

import java.util.HashSet;

public class DrawMissionsDecision extends Decision {
	@Override public String toString() {
		return "DrawMissionsDecision()";
	}
	
	@Override public boolean equals(Object o) {
		return o instanceof DrawMissionsDecision;
	}

	private static final int classHashCode = "DrawMissionsDecision".hashCode();
	@Override public int hashCode() {
		return classHashCode;
	}

	@Override public String reasonForIllegality(State s) {
		PlayerState p = s.currentPlayerState();

		if (p.drawn_card != null) return "you drew a card and now must decide which other card to draw";
		if (p.drawn_missions != null) return "you drew mission cards and now must decide which to keep";
		// NOTE: the rules don't forbid deciding to draw mission cards if the mission deck is empty
		
		return null;
	}
	
	@Override public void apply(State s) {
		PlayerState p = s.currentPlayerState();

		p.drawn_missions = new HashSet<Mission>();
		for (int i = 0; i < 3; i++) {
			if (!s.missions.isEmpty())
				p.drawn_missions.add(s.missions.removeFirst());
		}
	}
}
