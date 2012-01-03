package hobo;

import java.util.EnumSet;

public class DrawMissionsDecision extends Decision {
	public DrawMissionsDecision(int player) {
		this.player = player;
	}
	
	@Override public String toString() {
		return "DrawMissionsDecision(player: "+player+")";
	}
	
	@Override public boolean equals(Object o) {
		if (this == o)
			return true;
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
	
	@Override public AppliedDecision apply(State s, boolean undoably) {
		Application a = undoably ? new Application(this, s) : null;

		s.switchToPlayer(player);
		PlayerState p = s.playerState(player);

		p.drawn_missions = Util.remove_sample(s.missions, 3, s.random, EnumSet.noneOf(Mission.class));
		
		return a;
	}

	private class Application extends AppliedDecision {
		public Application(Decision d, State s) { super(d, s); }

		@Override public void undo() {
			PlayerState p = state.playerState(player);

			state.missions.addAll(p.drawn_missions);
			p.drawn_missions = null;

			super.undo();
		}
	}
}
