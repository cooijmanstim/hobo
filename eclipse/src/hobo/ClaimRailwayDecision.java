package hobo;

public class ClaimRailwayDecision extends Decision {
	public final Railway railway;
	public final CardBag cards;

	public ClaimRailwayDecision(int player, Railway railway, CardBag cards) {
		this.player = player; this.railway = railway; this.cards = cards;
	}
	
	public ClaimRailwayDecision(ClaimRailwayDecision that) {
		this.player = that.player; this.railway = that.railway; this.cards = that.cards;
	}
	
	@Override public String toString() {
		return "ClaimRailwayDecision(player: "+player+" railway: "+railway+" cards: "+cards+")";
	}
	
	@Override public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof ClaimRailwayDecision))
			return false;
		ClaimRailwayDecision that = (ClaimRailwayDecision)o;
		return that.player == this.player && that.railway == this.railway && that.cards.equals(this.cards);
	}
	
	@Override public ClaimRailwayDecision clone() {
		return new ClaimRailwayDecision(this);
	}

	private static final int classHashCode = "ClaimRailwayDecision".hashCode();
	@Override public int hashCode() {
		return player ^ railway.hashCode() ^ cards.hashCode() ^ classHashCode;
	}
	
	@Override public String reasonForIllegality(State s) {
		if (s.currentPlayer() != player) return "it's not your turn";
		PlayerState p = s.playerState(player);
		if (p.drawn_card != null) return "you drew a card and now must decide what other card to draw";
		if (p.drawn_missions != null) return "you drew mission cards and now must decide which to keep";
		if (p.ncars < railway.length) return "you do not have enough cars";
		if (s.isClaimed(railway)) return "that railway has already been claimed";
		if (p.railways.contains(railway.dual)) return "you already own the other railway between these cities";
		if (!p.hand.containsAll(cards)) return "you do not have these cards you claim to have";
		if (!railway.costs(cards)) return "the cards offered do not correspond to the railway cost";
		return null;
	}

	private State appliedTo = null;
	private int old_player;
	private Random old_random;
	
	@Override public void apply(State s) {
		assert(appliedTo == null);
		appliedTo = s;

		old_random = s.random.clone();
		old_player = s.currentPlayer();
		s.switchToPlayer(player);
		PlayerState p = s.playerState(player);

		p.hand.removeAll(cards);
		s.discarded.addAll(cards);

		p.claim(railway);
		s.owner_by_railway.put(railway, p.handle);

		s.restoreDecks();
		s.switchTurns();
	}

	@Override public void undo(State s) {
		assert(appliedTo == s);
		appliedTo = null;

		PlayerState p = s.playerState(player);
		s.unswitchTurns();
		s.unrestoreDecks();

		s.owner_by_railway.remove(railway);
		p.unclaim(railway);

		s.discarded.removeAll(cards);
		p.hand.addAll(cards);

		s.switchToPlayer(old_player);
		s.random = old_random;
	}
}
