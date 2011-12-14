package hobo;

public class ClaimRailwayDecision extends Decision {
	public final Railway railway;
	public final CardBag cards;

	public ClaimRailwayDecision(int player, Railway railway, CardBag cards) {
		this.player = player; this.railway = railway; this.cards = cards;
	}
	
	@Override public String toString() {
		return "ClaimRailwayDecision(player: "+player+" railway: "+railway+" cards: "+cards+")";
	}
	
	@Override public boolean equals(Object o) {
		if (!(o instanceof ClaimRailwayDecision))
			return false;
		ClaimRailwayDecision that = (ClaimRailwayDecision)o;
		return that.player == this.player && that.railway == this.railway && that.cards.equals(this.cards);
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

	@Override public void apply(State s) {
		s.switchToPlayer(player);
		PlayerState p = s.playerState(player);

		p.hand.removeAll(cards);
		s.discarded.addAll(cards);

		p.claim(railway);
		s.owner_by_railway.put(railway, p.handle);

		s.restoreDecks();
		s.switchTurns();
	}
}
