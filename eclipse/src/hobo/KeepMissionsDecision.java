package hobo;

import java.util.Set;

public class KeepMissionsDecision extends Decision {
	public final Set<Mission> missions;

	public KeepMissionsDecision(int player, Set<Mission> missions) {
		this.missions = missions;
		this.player = player;
	}
	
	@Override public String toString() {
		return "KeepMissionsDecision(player: "+player+" missions: "+missions+")";
	}
	
	@Override public boolean equals(Object o) {
		if (!(o instanceof KeepMissionsDecision))
			return false;
		KeepMissionsDecision that = (KeepMissionsDecision)o;
		return that.player == this.player && that.missions.equals(this.missions);
	}
	
	private static final int classHashCode = "KeepMissionsDecision".hashCode();	
	@Override public int hashCode() {
		return player ^ missions.hashCode() ^ classHashCode;
	}
	
	@Override public String reasonForIllegality(State s) {
		PlayerState p = s.playerState(player);
		if (s.currentPlayer() != player) return "it's not your turn";
		if (p.drawn_card != null) return "you drew a card and now must decide which other card to draw";
		if (p.drawn_missions == null) return "you did not draw mission cards";
		if (missions.isEmpty()) return "you must keep at least one of the mission cards";
		if (!p.drawn_missions.containsAll(missions)) return "you drew different mission cards than the ones you're trying to keep now";
		return null;
	}
	
	@Override public void apply(State s) {
		s.switchToPlayer(player);
		PlayerState p = s.playerState(player);

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
