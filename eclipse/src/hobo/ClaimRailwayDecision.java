package hobo;

public class ClaimRailwayDecision extends Decision {
	public final Railway railway;
	public final CardBag cards;

	public ClaimRailwayDecision(Railway railway, CardBag cards) {
		assert(railway != null); assert(cards != null);
		this.railway = railway; this.cards = cards;
	}
}
