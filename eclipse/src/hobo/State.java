package hobo;

import java.util.*;

public class State implements Cloneable {
	public static final int NCARDS_PER_COLOR = 12,
		                    INITIAL_HAND_SIZE = 4,
                            OPEN_DECK_SIZE = 5;

	// store all playerstates in one place, keyed by player name.
	// players are referred to by name in most other places.
	private Map<String,PlayerState> players_by_name = new HashMap<String,PlayerState>();

	// keep track of player order
	private LinkedList<String> player_sequence = new LinkedList<String>();

	private boolean game_over = false;
	private String last_player = null;
	
	public State() {}
	
	public State(List<String> players) {
		for (String pn: players)
			players_by_name.put(pn, new PlayerState(pn));
		player_sequence.addAll(players);
	}

	public State clone() {
		State that = new State();
		for (PlayerState ps: this.players_by_name.values())
			that.players_by_name.put(ps.name, ps.clone());
		that.player_sequence.addAll(this.player_sequence);
		that.owner_by_railway.putAll(this.owner_by_railway);
		that.deck.addAll(this.deck);
		that.open_deck.addAll(this.deck);
		that.discarded.addAll(this.discarded);
		that.missions.addAll(this.missions);
		that.game_over = this.game_over;
		that.last_player = this.last_player;
		return that;
	}

	public void setup() {
		for (Color c: Color.values())
			deck.addAll(Collections.nCopies(NCARDS_PER_COLOR, c));

		missions.addAll(Mission.missions);
		Collections.shuffle(missions);

		for (PlayerState p: players_by_name.values())
			for (int i = 0; i < INITIAL_HAND_SIZE; i++)
				p.hand.add(deck.draw());

		// TODO: not initially dealing destination ticket cards yet, because it
		// requires players to choose at least two to keep, instead of the one
		// when they draw these cards themselves.  fuck these moronic rules.
	}

	public void switchTurns() {
		Collections.rotate(player_sequence, 1);
	}

	public PlayerState currentPlayerState() {
		return players_by_name.get(player_sequence.getFirst());
	}

	// TODO: maybe move this into railway
	private Map<Railway,String> owner_by_railway = new HashMap<Railway,String>();

	public boolean isClaimed(Railway r) {
		return owner_by_railway.containsKey(r);
	}

	private CardBag deck      = new CardBag();
	private CardBag open_deck = new CardBag();
	private CardBag discarded = new CardBag();

	// deck of destination tickets
	private LinkedList<Mission> missions = new LinkedList<Mission>();

	// TODO: don't use exceptions, instead define an isLegal(d) method
	// expectation that that will be faster
	private void illegalIf(boolean condition) { if (condition) throw new IllegalDecisionException(); }
	private void illegalUnless(boolean condition) { illegalIf(!condition); }

	public void applyDecision(Decision d) throws IllegalDecisionException {
		if      (d instanceof ClaimRailwayDecision) applyDecision((ClaimRailwayDecision)d);
		else if (d instanceof     DrawCardDecision) applyDecision((    DrawCardDecision)d);
		else if (d instanceof DrawMissionsDecision) applyDecision((DrawMissionsDecision)d);
		else if (d instanceof KeepMissionsDecision) applyDecision((KeepMissionsDecision)d);
		else throw new IllegalDecisionException();
	}

	public void applyDecision(ClaimRailwayDecision d) throws IllegalDecisionException {
		PlayerState p = currentPlayerState();

		illegalIf(p.drawn_card != null || p.drawn_missions != null);
		illegalUnless(p.ncars >= d.railway.length);
		illegalIf(isClaimed(d.railway)); // TODO: do the more complicated dance with double railways
		illegalUnless(p.hand.containsAll(d.cards));
		illegalUnless(d.railway.costs(d.cards));

		p.hand.removeAll(d.cards);
		discarded.addAll(d.cards);

		p.ncars -= d.railway.length;
		p.score += d.railway.score();

		p.railways.add(d.railway);
		owner_by_railway.put(d.railway, p.name);

		// impending doom?
		if (p.ncars < PlayerState.MIN_NCARS) {
			if (last_player == null) {
				last_player = p.name;
			} else if (last_player == p.name) {
				game_over = true;
			}
		}

		if (!game_over)
			switchTurns();
	}

	public void applyDecision(DrawCardDecision d) throws IllegalDecisionException {
		PlayerState p = currentPlayerState();

		illegalIf(p.drawn_missions != null);

		// if drew a card last time, then can draw one more
		boolean last_draw = p.drawn_card != null;

		if (d.color == null) {
			illegalIf(deck.isEmpty());
			p.drawn_card = deck.draw();

			if (deck.isEmpty()) {
				deck = discarded;
				discarded = deck;
			}
		} else {
			illegalUnless(open_deck.contains(d.color));
			p.drawn_card = open_deck.draw(d.color);

			// TODO: drawing a grey card from the open deck means you don't get to draw another one
			if (p.drawn_card == Color.GREY)
				last_draw = true;

			// TODO: if the replacement is grey, you can't pick that one after this
			// (these rules are motherfucking stoopid!)
		}

		p.hand.add(p.drawn_card);

		if (last_draw) {
			p.drawn_card = null;
			switchTurns();
		}
	}

	public void applyDecision(DrawMissionsDecision d) throws IllegalDecisionException {
		PlayerState p = currentPlayerState();

		illegalIf(p.drawn_card != null || p.drawn_missions != null);
		// NOTE: the rules don't forbid deciding to draw mission cards if the mission deck is empty

		p.drawn_missions = new HashSet<Mission>();
		for (int i = 0; i < 3; i++) {
			if (!missions.isEmpty())
				p.drawn_missions.add(missions.removeFirst());
		}
	}

	public void applyDecision(KeepMissionsDecision d) throws IllegalDecisionException {
		PlayerState p = currentPlayerState();

		illegalIf(p.drawn_card != null || p.drawn_missions == null);
		illegalIf(d.missions.isEmpty());

		for (Mission m: p.drawn_missions) {
			if (d.missions.contains(m))
				p.missions.add(m);
			else
				missions.addLast(m);
		}
		p.drawn_missions = null;

		switchTurns();
	}
}
