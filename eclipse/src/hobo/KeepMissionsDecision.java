package hobo;

import java.util.EnumSet;
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

	public static Set<Decision> availableTo(State s, PlayerState ps, Set<Decision> ds) {
		if (ps.drawn_missions == null)
			return ds;
		for (Set<Mission> ms: Util.powerset(ps.drawn_missions, EnumSet.noneOf(Mission.class))) {
			if (ms.isEmpty())
				continue;
			ds.add(new KeepMissionsDecision(ps.handle, ms));
		}
		return ds;
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
	
	@Override public AppliedDecision apply(State s, Object outcome_designator, boolean undoably) {
		AppliedDecision a = undoably ? new AppliedDecision(this, s) : null;
		
		s.switchToPlayer(player);
		PlayerState p = s.playerState(player);

		// XXX: don't modify p.drawn_missions
		p.receiveMissions(missions);
		s.missions.addAll(p.drawn_missions);
		s.missions.removeAll(missions);
		
		if (undoably) a.drawn_missions = p.drawn_missions;
		p.drawn_missions = null;

		s.switchTurns();
		
		return a;
	}

	public class AppliedDecision extends hobo.AppliedDecision {
		public Set<Mission> drawn_missions = null;

		public AppliedDecision(Decision d, State s) { super(d, s); }

		@Override public void undo() {
			state.unswitchTurns();
			PlayerState p = state.playerState(player);

			p.unreceiveMissions(missions);
			state.missions.removeAll(drawn_missions);
			p.drawn_missions = drawn_missions;

			super.undo();
		}

		@Override public String toString() {
			return "KeepMissionsDecision.AppliedDecision(drawn_missions: "+drawn_missions+")";
		}
	}
}
