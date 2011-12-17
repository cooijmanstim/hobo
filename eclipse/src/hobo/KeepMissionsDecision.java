package hobo;

import java.util.Set;

public class KeepMissionsDecision extends Decision {
	public final Set<Mission> missions;

	public KeepMissionsDecision(int player, Set<Mission> missions) {
		this.player = player; this.missions = missions;
	}
	
	public KeepMissionsDecision(KeepMissionsDecision that) {
		this.player = that.player; this.missions = that.missions;
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
	
	@Override public KeepMissionsDecision clone() {
		return new KeepMissionsDecision(this);
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
	
	private State appliedTo = null;
	private int old_player;
	private Set<Mission> drawn_missions = null;

	@Override public void apply(State s) {
		assert(appliedTo == null);
		appliedTo = s;

		old_player = s.currentPlayer();
		s.switchToPlayer(player);
		PlayerState p = s.playerState(player);

		// XXX: don't modify p.drawn_missions
		p.missions.addAll(missions);
		s.missions.addAll(p.drawn_missions);
		s.missions.removeAll(missions);

		drawn_missions = p.drawn_missions;
		p.drawn_missions = null;

		s.switchTurns();
	}

	@Override public void undo(State s) {
		assert(appliedTo == s);
		appliedTo = null;
		
		s.unswitchTurns();
		PlayerState p = s.playerState(player);

		p.missions.removeAll(drawn_missions);
		s.missions.removeAll(drawn_missions);
		p.drawn_missions = drawn_missions;

		s.switchToPlayer(old_player);
	}
}
