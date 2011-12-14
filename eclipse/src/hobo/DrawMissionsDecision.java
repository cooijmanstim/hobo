package hobo;

import java.util.HashSet;

public class DrawMissionsDecision extends Decision {
	public DrawMissionsDecision(int player) {
		this.player = player;
	}
	
	@Override public String toString() {
		return "DrawMissionsDecision(player: "+player+")";
	}
	
	@Override public boolean equals(Object o) {
		if (!(o instanceof DrawMissionsDecision))
			return false;
		DrawMissionsDecision that = (DrawMissionsDecision)o;
		return that.player == this.player;
	}

	private static final int classHashCode = "DrawMissionsDecision".hashCode();
	@Override public int hashCode() {
		return player ^ classHashCode;
	}

	@Override public String reasonForIllegality(State s) {
		PlayerState p = s.playerState(player);
		if (s.currentPlayer() != player) return "it's not your turn";
		if (p.drawn_card != null) return "you drew a card and now must decide which other card to draw";
		if (p.drawn_missions != null) return "you drew mission cards and now must decide which to keep";
		if (s.missions.isEmpty()) return "no missions to draw";
		return null;
	}
	
	@Override public void apply(State s) {
		s.switchToPlayer(player);
		PlayerState p = s.playerState(player);

		p.drawn_missions = new HashSet<Mission>();
		for (int i = 0; i < 3; i++) {
			if (!s.missions.isEmpty())
				p.drawn_missions.add(s.missions.removeFirst());
		}
	}
}
