package hobo;

public class DrawCardDecision extends Decision {
	// if color is not null, draw from open deck
	public final Color color;
	
	public DrawCardDecision(int player) {
		this.player = player;
		this.color = null;
	}
	
	public DrawCardDecision(int player, Color color) {
		this.player = player;
		this.color = color;
	}
	
	public DrawCardDecision(DrawCardDecision that) {
		this.player = that.player;
		this.color = that.color;
	}

	@Override public String toString() {
		return "DrawCardDecision(player: "+player+" color: "+color+")";
	}

	@Override public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof DrawCardDecision))
			return false;
		DrawCardDecision that = (DrawCardDecision)o;
		return that.player == this.player && that.color == this.color;
	}
	
	@Override public DrawCardDecision clone() {
		return new DrawCardDecision(this);
	}

	private static final int classHashCode = "DrawCardDecision".hashCode();
	@Override public int hashCode() {
		return player ^ (color == null ? -1 : color.hashCode()) ^ classHashCode;
	}

	@Override public String reasonForIllegality(State s) {
		if (s.currentPlayer() != player)
			return "it's not your turn";
		
		PlayerState p = s.playerState(player);

		if (p.drawn_missions != null)
			return "you drew mission cards and now must decide which to keep";

		if (color == null) {
			if (s.deck.isEmpty())
				return "deck is empty";
		} else {
			if (!s.open_deck.contains(color))
				return "no such card in the open deck";
		}
		return null;
	}

	// this undo crap shouldn't affect equality and cloning
	private State appliedTo = null;
	private Random old_random;
	private int old_player;
	private Color old_drawn_card; // old p.drawn_card
	private Color drawn_card; // card drawn for this decision

	@Override public void apply(State s) {
		assert(appliedTo == null);
		appliedTo = s;

		old_random = s.random.clone();
		old_player = s.currentPlayer();
		s.switchToPlayer(player);
		PlayerState p = s.playerState(player);

		// if drew a card last time, then can draw one more
		boolean last_draw = p.drawn_card != null;
		old_drawn_card = p.drawn_card;

		if (color == null) {
			p.drawn_card = s.deck.draw(s.random);
		} else {
			p.drawn_card = s.open_deck.draw(color);
			if (p.drawn_card == Color.GREY)
				last_draw = true;
		}

		drawn_card = p.drawn_card;
		p.hand.add(p.drawn_card);
		
		s.restoreDecks();

		if (last_draw) {
			p.drawn_card = null;
			s.switchTurns();
		}
	}
	
	@Override public void undo(State s) {
		assert(appliedTo == s);
		appliedTo = null;
		
		// second draw or single open deck draw
		if (old_drawn_card != null || color == Color.GREY)
			s.unswitchTurns();
		PlayerState p = s.playerState(player);
		s.unrestoreDecks();
		
		p.hand.remove(drawn_card);
		
		if (color == null) {
			s.deck.add(drawn_card);
		} else {
			s.open_deck.add(drawn_card);
		}
		
		p.drawn_card = old_drawn_card;
		
		s.random = old_random;
		s.switchToPlayer(old_player);
	}
}
