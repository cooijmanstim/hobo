package hobo;

import java.util.*;

public class State implements Cloneable {
	public static final int NCARDS_PER_COLOR = 12,
	                        INITIAL_HAND_SIZE = 4,
	                        INITIAL_MISSION_COUNT = 3,
	                        OPEN_DECK_SIZE = 5;

	// the index into this array is used to refer to a player in many places here.
	private PlayerState[] players;

	// keep track of player order
	private int[] player_order;

	private Map<Railway,Integer> owner_by_railway = new HashMap<Railway,Integer>();

	private CardBag deck      = new CardBag();
	private CardBag open_deck = new CardBag();
	private CardBag discarded = new CardBag();

	// deck of destination tickets
	private LinkedList<Mission> missions = new LinkedList<Mission>();

	private boolean game_over = false;
	private int last_player = -1;
	
	public State clone() {
		State that = new State();
		that.players = this.players.clone();
		for (int i = 0; i < players.length; i++)
			that.players[i] = that.players[i].clone();
		that.player_order = this.player_order.clone();
		that.owner_by_railway.putAll(this.owner_by_railway);
		that.deck.addAll(this.deck);
		that.open_deck.addAll(this.deck);
		that.discarded.addAll(this.discarded);
		that.missions.addAll(this.missions);
		that.game_over = this.game_over;
		that.last_player = this.last_player;
		return that;
	}

	public State() {}
	
	public State(String... player_names) {
		int ni = player_names.length;
		players = new PlayerState[ni];
		player_order = new int[ni];
		for (int i = 0; i < ni; i++) {
			players[i] = new PlayerState(i, player_names[i]);
			player_order[i] = i;
		}
	}

	public void setup() {
		for (Color c: Color.values())
			deck.addAll(Collections.nCopies(NCARDS_PER_COLOR, c));
		// two more grey cards than other colors
		deck.add(Color.GREY); deck.add(Color.GREY);

		missions.addAll(Mission.missions);
		Collections.shuffle(missions);

		for (PlayerState p: players) {
			for (int i = 0; i < INITIAL_HAND_SIZE; i++)
				p.hand.add(deck.draw());
			// XXX: officially, players can choose to discard one
			// of the missions dealt.
			for (int i = 0; i < INITIAL_MISSION_COUNT; i++)
				p.missions.add(missions.removeFirst());
		}

		for (int i = 0; i < OPEN_DECK_SIZE; i++)
			open_deck.add(deck.draw());
	}

	public void switchTurns() {
		// shifts all elements forward, putting the first element in the back
		int x = player_order[0];
		int ni = player_order.length;
		for (int i = 1; i < ni; i++)
			player_order[i-1] = player_order[i];
		player_order[ni-1] = x;
	}

	public int[] players() {
		return player_order.clone();
	}
	
	public List<PlayerState> playerStates() {
		List<PlayerState> playerStates = new ArrayList<PlayerState>();
		for (int i: player_order)
			playerStates.add(players[i]);
		return playerStates;
	}
	
	public int currentPlayer() {
		return player_order[0];
	}

	public PlayerState currentPlayerState() {
		return playerState(currentPlayer());
	}

	public PlayerState playerState(int handle) {
		return players[handle];
	}

	public boolean gameOver() {
		return game_over;
	}

	public boolean isDraw() {
		int winner = winner();
		for (int handle: player_order) {
			if (handle != winner && players[winner].finalScore() == players[handle].finalScore())
				return true;
		}
		return false;
	}

	public int winner() {
		int xmax = Integer.MIN_VALUE;
		int winner = -1;
		int ni = players.length;
		for (int i = 0; i < ni; i++) {
			int x = players[i].finalScore();
			if (x > xmax) {
				xmax = x;
				winner = i;
			}
		}
		return winner;
	}

	public boolean isClaimed(Railway r) {
		return owner_by_railway.containsKey(r);
	}

	public CardBag openCards() {
		return open_deck;
	}

	// TODO: don't use exceptions, instead define an isLegal(d) method
	// expectation that that will be faster
	private void illegalIf(boolean condition, String reason) {
		if (condition)
			throw new IllegalDecisionException(reason);
	}
	private void illegalUnless(boolean condition, String reason) {
		illegalIf(!condition, reason);
	}

	public void applyDecision(Decision d) throws IllegalDecisionException {
		if      (d instanceof ClaimRailwayDecision) applyDecision((ClaimRailwayDecision)d);
		else if (d instanceof     DrawCardDecision) applyDecision((    DrawCardDecision)d);
		else if (d instanceof DrawMissionsDecision) applyDecision((DrawMissionsDecision)d);
		else if (d instanceof KeepMissionsDecision) applyDecision((KeepMissionsDecision)d);
		else throw new IllegalDecisionException("unknown decision type: "+d);
	}

	public void applyDecision(ClaimRailwayDecision d) throws IllegalDecisionException {
		PlayerState p = currentPlayerState();

		illegalIf(p.drawn_card != null, "you drew a card and now must decide what other card to draw");
		illegalIf(p.drawn_missions != null, "you drew mission cards and now must decide which to keep");
		illegalUnless(p.ncars >= d.railway.length, "you do not have enough cars");
		illegalIf(isClaimed(d.railway), "that railway has already been claimed");
		illegalIf(d.railway.dual != null && owner_by_railway.get(d.railway.dual) == (Integer)p.handle, "you already own the other railway between these cities");
		illegalUnless(p.hand.containsAll(d.cards), "you do not have these cards you claim to have");
		illegalUnless(d.railway.costs(d.cards), "the cards offered do not correspond to the railway cost");

		p.hand.removeAll(d.cards);
		discarded.addAll(d.cards);

		p.claim(d.railway);

		owner_by_railway.put(d.railway, p.handle);

		// impending doom?
		if (p.ncars < PlayerState.MIN_NCARS) {
			if (last_player < 0) {
				last_player = p.handle;
			} else if (last_player == p.handle) {
				game_over = true;
			}
		}

		if (!game_over)
			switchTurns();
	}

	public void applyDecision(DrawCardDecision d) throws IllegalDecisionException {
		PlayerState p = currentPlayerState();

		illegalIf(p.drawn_missions != null, "you drew mission cards and now must decide which to keep");

		// if drew a card last time, then can draw one more
		boolean last_draw = p.drawn_card != null;

		if (d.color == null) {
			illegalIf(deck.isEmpty(), "deck is empty");
			p.drawn_card = deck.draw();

			if (deck.isEmpty()) {
				deck = discarded;
				discarded = deck;
			}
		} else {
			illegalUnless(open_deck.contains(d.color), "no such card in the open deck");

			p.drawn_card = open_deck.draw(d.color);
			if (p.drawn_card == Color.GREY)
				last_draw = true;
			// TODO: if the replacement is grey, you can't pick that one after this
			// (these rules are motherfucking stoopid!)
			if (!deck.isEmpty())
				open_deck.add(deck.draw());
		}

		p.hand.add(p.drawn_card);

		if (last_draw) {
			p.drawn_card = null;
			switchTurns();
		}
	}

	public void applyDecision(DrawMissionsDecision d) throws IllegalDecisionException {
		PlayerState p = currentPlayerState();

		illegalIf(p.drawn_card != null, "you drew a card and now must decide which other card to draw");
		illegalIf(p.drawn_missions != null, "you drew mission cards and now must decide which to keep");
		// NOTE: the rules don't forbid deciding to draw mission cards if the mission deck is empty

		p.drawn_missions = new HashSet<Mission>();
		for (int i = 0; i < 3; i++) {
			if (!missions.isEmpty())
				p.drawn_missions.add(missions.removeFirst());
		}
	}

	public void applyDecision(KeepMissionsDecision d) throws IllegalDecisionException {
		PlayerState p = currentPlayerState();

		illegalIf(p.drawn_card != null, "you drew a card and now must decide which other card to draw");
		illegalIf(p.drawn_missions == null, "you did not draw mission cards");
		illegalIf(d.missions.isEmpty(), "you must keep at least one of the mission cards");

		for (Mission m: p.drawn_missions) {
			if (d.missions.contains(m))
				p.missions.add(m);
			else
				missions.addLast(m);
		}
		p.drawn_missions = null;

		switchTurns();
	}


	public static void main(String[] args) {
		// do some automated testing
		String[] players = "alice bob charlie".split("\\s+");
		State s = new State(players);
		s.setup();

		for (int i = 0; i < players.length; i++) {
			PlayerState p = s.players[i];
			assert(!p.hand.isEmpty());
		}

		PlayerState p = s.currentPlayerState();
		// make sure p has enough yellow cards to claim some particular route later
		while (s.deck.contains(Color.YELLOW))
			p.hand.add(s.deck.draw(Color.YELLOW));

		int k = p.hand.count(Color.YELLOW);
		assert(k >= 2);
		Railway r = City.NEW_YORK.railwayTo(City.BOSTON, Color.YELLOW);
		s.applyDecision(new ClaimRailwayDecision(r, new CardBag(Color.YELLOW, Color.YELLOW)));
		assert(p.railways.contains(r));
		assert(p.hand.count(Color.YELLOW) == k - 2);

		assert(p.score == 2);

		assert(s.currentPlayerState() != p);
		p = s.currentPlayerState();

		CardBag oldhand = p.hand.clone();

		// test mission completion: two missions will be completed simultaneously
		p.missions.add(Mission.connecting(City.PORTLAND,      City.NASHVILLE));
		p.missions.add(Mission.connecting(City.SAN_FRANCISCO, City.ATLANTA));
		
		ClaimRailwayDecision[] decisions = new ClaimRailwayDecision[]{
			new ClaimRailwayDecision(City.PORTLAND.railwayTo(City.SALT_LAKE_CITY),
			                         new CardBag(Color.BLUE, Color.BLUE, Color.BLUE,
			                                     Color.BLUE, Color.BLUE, Color.BLUE)),
			new ClaimRailwayDecision(City.SAN_FRANCISCO.railwayTo(City.SALT_LAKE_CITY, Color.WHITE),
			                         new CardBag(Color.WHITE, Color.WHITE, Color.WHITE,
			                                     Color.WHITE, Color.WHITE)),
			new ClaimRailwayDecision(City.ATLANTA.railwayTo(City.NASHVILLE),
			                         new CardBag(Color.YELLOW)),
			new ClaimRailwayDecision(City.NASHVILLE.railwayTo(City.SAINT_LOUIS),
			                         new CardBag(Color.GREEN, Color.GREEN)),
			new ClaimRailwayDecision(City.SALT_LAKE_CITY.railwayTo(City.DENVER, Color.RED),
			                         new CardBag(Color.RED, Color.RED, Color.RED)),
			new ClaimRailwayDecision(City.SAINT_LOUIS.railwayTo(City.KANSAS_CITY, Color.PINK),
			                         new CardBag(Color.PINK, Color.PINK)),
			new ClaimRailwayDecision(City.KANSAS_CITY.railwayTo(City.DENVER, Color.BLACK),
			                         new CardBag(Color.BLACK, Color.GREY, Color.GREY, Color.BLACK)),
			new ClaimRailwayDecision(City.SEATTLE.railwayTo(City.PORTLAND),
			                         new CardBag(Color.GREY)),
		};
		
		for (ClaimRailwayDecision d: decisions) {
			// make sure the player has the cards he needs
			p.hand.addAll(d.cards);
			s.applyDecision(d);
			s.switchTurns(); s.switchTurns();
		}
		
		assert(oldhand.equals(p.hand));
		assert(p.completed_missions.size() == 2);
	}
}
