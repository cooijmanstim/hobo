package hobo;

import java.util.ArrayList;
import java.util.List;

public class Belief {
	private final Random random;

	private int player; // beliefs of which player?
	private PlayerBelief[] players;
	private CardBag known_deck_cards;
	private CardBag cards_of_unknown_location;
	
	private List<Event> events;

	public Belief(int player) {
		this.random = new Random();
		this.events = new ArrayList<Event>();
		this.player = player;
	}
	
	public void initialize(State s) {
		cards_of_unknown_location = State.INITIAL_DECK.clone();
		cards_of_unknown_location.removeAll(s.open_deck);
		cards_of_unknown_location.removeAll(s.discarded); // usually empty at this point
		known_deck_cards = new CardBag();
		players = new PlayerBelief[s.players().length];
		for (int i = 0; i < players.length; i++)
			players[i] = new PlayerBelief(s.playerState(i));
	}

	public void update(Event e) {
		events.add(e);
		
		Decision d = e.decision;
		     if (d instanceof     DrawCardDecision) update((    DrawCardDecision)d, (DrawCardDecision.AppliedDecision)e.applied_decision, e.state);
		else if (d instanceof ClaimRailwayDecision) update((ClaimRailwayDecision)d, (ClaimRailwayDecision.AppliedDecision)e.applied_decision, e.state);
		else if (d instanceof DrawMissionsDecision) update((DrawMissionsDecision)d, (DrawMissionsDecision.AppliedDecision)e.applied_decision, e.state);
		else if (d instanceof KeepMissionsDecision) update((KeepMissionsDecision)d, (KeepMissionsDecision.AppliedDecision)e.applied_decision, e.state);
		else throw new RuntimeException("unknown decision type: "+d);

		requireSanity(e.state);
	}

	public void update(DrawCardDecision d, DrawCardDecision.AppliedDecision ad, State s) {
		// do we know which color card was drawn?
		if (d.player == player || d.color != null) {
			Color c = ad.drawn_card;
			players[d.player].known_cards.add(c);

			if (d.color == null) {
				if (known_deck_cards.contains(c))
					known_deck_cards.remove(c);
				else
					cards_of_unknown_location.remove(c);
			}
		} else {
			// don't know what color -- could have been any one of them,
			// so we lose a lot of information
			for (Color c: Color.all) {
				if (known_deck_cards.contains(c)) {
					known_deck_cards.remove(c);
					cards_of_unknown_location.add(c);
				}
			}
		}
		
		handleDeckRestoration(s);
	}
	
	public void update(ClaimRailwayDecision d, ClaimRailwayDecision.AppliedDecision ad, State s) {
		// the cards that were used but not known to be in the hand
		// are now known to be in the discard pile (which is visible)
		CardBag revealed_from_hand = d.cards.clone();
		revealed_from_hand.removeAll(players[d.player].known_cards);
		cards_of_unknown_location.removeAll(revealed_from_hand);

		players[d.player].known_cards.removeAll(d.cards);

		handleDeckRestoration(s);
	}
	
	public void update(DrawMissionsDecision d, DrawMissionsDecision.AppliedDecision ad, State s) {
	}
	
	public void update(KeepMissionsDecision d, KeepMissionsDecision.AppliedDecision ad, State s) {
	}

	private void handleDeckRestoration(State s) {
		// discard pile went back into the deck?
		CardBag discarded = s.discardeds.getFirst(); 
		if (discarded != null)
			known_deck_cards.addAll(discarded);

		// new cards in open deck?
		CardBag restoration = s.deck_restorations.getFirst(); 
		if (restoration != null) {
			// cards that went into the open deck but were not known
			// to be in the normal deck are now known to be in the
			// open deck (which is visible)
			CardBag revealed = restoration.clone();
			revealed.removeAll(known_deck_cards);
			cards_of_unknown_location.removeAll(revealed);

			known_deck_cards.removeAll(restoration);
		}
	}
	
	// return a possible state according to the distribution defined by this belief
	public State sample(State s) {
		CardBag unknown = cards_of_unknown_location.clone();
		s = s.clone();
		//s.random = new Random(random.nextLong());

		CardBag deck = known_deck_cards.clone();
		deck.addAll(unknown.remove_sample(s.deck.size() - known_deck_cards.size(), random));
		s.deck = deck;

		for (int i = 0; i < players.length; i++) {
			if (i == player)
				continue;
			PlayerState ps = s.playerState(i);
			PlayerBelief pb = players[i];
			CardBag hand = pb.known_cards.clone();
			hand.addAll(unknown.remove_sample(ps.hand.size() - hand.size(), random));
			ps.hand = hand;
		}
		assert(unknown.isEmpty());
		return s;
	}

	// return the likelihood (a probability) of a state given that this belief is true
	public double likelihoodOf(State s) {
		double p = 1;
		
		CardBag unknown = cards_of_unknown_location.clone();

		CardBag unknown_deck_cards = s.deck.clone();
		assert(unknown_deck_cards.containsAll(known_deck_cards));
		unknown_deck_cards.removeAll(known_deck_cards);

		p *= unknown.probabilityOfSample(unknown_deck_cards);
		unknown.removeAll(unknown_deck_cards);

		for (int i = 0; i < players.length; i++) {
			if (i == player)
				continue;

			CardBag unknown_hand_cards = s.playerState(i).hand.clone();
			assert(unknown_hand_cards.containsAll(players[i].known_cards));
			unknown_hand_cards.removeAll(players[i].known_cards);

			p *= unknown.probabilityOfSample(unknown_hand_cards);
			unknown.removeAll(unknown_hand_cards);
		}

		return p;
	}

	public void requireSanity(State s) {
		boolean sane = true;
		
		if (!s.deck.containsAll(known_deck_cards)) {
			CardBag surplus = known_deck_cards.clone();
			surplus.removeAll(s.deck);
			System.err.println("inconsistent belief: known_deck_cards "+known_deck_cards+" contains cards "+surplus+" that are not in the deck "+s.deck);
			sane = false;
		}
		for (int i = 0; i < players.length; i++) {
			if (!s.playerState(i).hand.containsAll(players[i].known_cards)) {
				System.err.println("inconsistent belief: for player "+s.playerState(i).name+", known_cards "+players[i].known_cards+" contains cards that are not in the hand "+s.playerState(i).hand);
				sane = false;
			}
		}

		int total_card_count = 0;
		total_card_count = cards_of_unknown_location.size();
		total_card_count += known_deck_cards.size();
		total_card_count += s.open_deck.size();
		total_card_count += s.discarded.size();
		for (int i = 0; i < players.length; i++)
			total_card_count += players[i].known_cards.size();
		if (total_card_count != State.INITIAL_DECK.size()) {
			System.err.println("inconsistent belief: "+total_card_count+" cards in the game, should be "+State.INITIAL_DECK.size());
			sane = false;
		}

		if (!sane) {
			System.err.println("events that led to this belief (most recent event last):");
			for (Event e: events)
				System.err.println("  "+e);
			throw new RuntimeException("inconsistent belief");
		}
	}
	
	private class PlayerBelief {
		private CardBag known_cards;

		public PlayerBelief(PlayerState s) {
			if (s.handle == player) {
				known_cards = s.hand.clone();
				cards_of_unknown_location.removeAll(known_cards);
			} else {
				known_cards = new CardBag();
			}
		}
	}
}
