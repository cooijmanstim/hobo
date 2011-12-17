package hobo;

public class DrawMissionsDecision extends Decision {
	public DrawMissionsDecision(int player) {
		this.player = player;
	}
	
	public DrawMissionsDecision(DrawMissionsDecision that) {
		this.player = that.player;
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
	
	@Override public DrawMissionsDecision clone() {
		return new DrawMissionsDecision(this);
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
	
	private State appliedTo = null;
	private Random old_random;
	private int old_player;
	
	@Override public void apply(State s) {
		assert(appliedTo == null);
		appliedTo = s;

		old_random = s.random.clone();
		old_player = s.currentPlayer();
		s.switchToPlayer(player);
		PlayerState p = s.playerState(player);

		p.drawn_missions = Util.remove_sample(s.missions, 3, s.random);
	}
	
	@Override public void undo(State s) {
		assert(appliedTo == s);
		appliedTo = null;

		PlayerState p = s.playerState(player);
		
		s.missions.addAll(p.drawn_missions);
		p.drawn_missions = null;
		
		s.switchToPlayer(old_player);
		s.random = old_random;
	}
}
