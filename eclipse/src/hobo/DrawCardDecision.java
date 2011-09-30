package hobo;

public class DrawCardDecision extends Decision {
	public final Card card;

	// if card is not null, draw from open deck
	public DrawCardDecision(Card card) {
		this.card = card;
	}
}
