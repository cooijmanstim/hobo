package hobo;

import java.util.Set;

public class KeepMissionsDecision extends Decision {
	public final Set<Mission> missions;

	public KeepMissionsDecision(int player, Set<Mission> missions) {
		this.player = player; this.missions = missions;
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
	
	@Override public double weight(State s) {
		// maybe look at whether some are already complete
		// when this type of decision is possible, all possible decisions
		// will be of this type, so the prior weight for keep missions
		// decision has no influence
		return 1;
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
	
	@Override public AppliedDecision apply(State s, boolean undoably) {
		Application a = undoably ? new Application(this, s) : null;
		
		s.switchToPlayer(player);
		PlayerState p = s.playerState(player);

		// XXX: don't modify p.drawn_missions
		p.missions.addAll(missions);
		s.missions.addAll(p.drawn_missions);
		s.missions.removeAll(missions);

		if (undoably) a.drawn_missions = p.drawn_missions;
		p.drawn_missions = null;

		s.switchTurns();
		
		return a;
	}

	private class Application extends AppliedDecision {
		private Set<Mission> drawn_missions = null;
		
		public Application(Decision d, State s) { super(d, s); }

		@Override public void undo() {
			state.unswitchTurns();
			PlayerState p = state.playerState(player);

			p.missions.removeAll(drawn_missions);
			state.missions.removeAll(drawn_missions);
			p.drawn_missions = drawn_missions;

			super.undo();
		}
	}
}
