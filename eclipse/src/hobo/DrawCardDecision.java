package hobo;

import java.util.*;

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
	
	private static final int classHashCode = "DrawCardDecision".hashCode();
	@Override public int hashCode() {
		return player ^ (color == null ? -1 : color.hashCode()) ^ classHashCode;
	}
	
	public static Set<Decision> availableTo(State s, PlayerState ps, Set<Decision> ds) {
		if (ps.drawn_missions != null)
			return ds;
		
		for (Color c: Color.all)
			if (s.open_deck.contains(c))
				ds.add(new DrawCardDecision(ps.handle, c));

		if (!s.deck.isEmpty())
			ds.add(new DrawCardDecision(ps.handle, null));

		return ds;
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
	
	@Override public AppliedDecision apply(State s, boolean undoably) {
		Application a = undoably ? new Application(this, s) : null;

		s.switchToPlayer(player);
		PlayerState p = s.playerState(player);

		// if drew a card last time, then can draw one more
		boolean last_draw = p.drawn_card != null;
		if (undoably) a.old_drawn_card = p.drawn_card;

		if (color == null) {
			p.drawn_card = s.deck.draw(s.random);
		} else {
			p.drawn_card = s.open_deck.draw(color);
			if (p.drawn_card == Color.GREY)
				last_draw = true;
		}

		if (undoably) a.drawn_card = p.drawn_card;
		p.hand.add(p.drawn_card);
		
		s.restoreDecks();

		if (last_draw) {
			p.drawn_card = null;
			s.switchTurns();
		}
		
		return a;
	}

	private class Application extends AppliedDecision {
		private Color old_drawn_card; // card drawn before
		private Color drawn_card; // card drawn during
		
		public Application(Decision d, State s) { super(d, s); }

		@Override public void undo() {
			// second draw or single open deck draw
			if (old_drawn_card != null || color == Color.GREY)
				state.unswitchTurns();
			PlayerState p = state.playerState(player);
			state.unrestoreDecks();
			
			p.hand.remove(drawn_card);
			
			if (color == null) {
				state.deck.add(drawn_card);
			} else {
				state.open_deck.add(drawn_card);
			}
			
			p.drawn_card = old_drawn_card;
			
			super.undo();
		}
	}
}
