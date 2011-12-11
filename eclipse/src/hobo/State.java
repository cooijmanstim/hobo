package hobo;

import java.util.*;

public class State implements Cloneable {
	public static final int NCARDS_PER_COLOR = 12,
	                        INITIAL_HAND_SIZE = 4,
	                        INITIAL_MISSION_COUNT = 3,
	                        OPEN_DECK_SIZE = 5;

	public final Random random = new Random(0);

	// the index into this array is used to refer to a player in many places here.
	private PlayerState[] players;

	// keep track of player order
	private int[] player_order;

	public Map<Railway,Integer> owner_by_railway = new HashMap<Railway,Integer>();

	public CardBag deck      = new CardBag();
	public CardBag open_deck = new CardBag();
	public CardBag discarded = new CardBag();

	// deck of destination tickets
	public LinkedList<Mission> missions = new LinkedList<Mission>();

	private boolean game_over = false;
	private int last_player = -1;
	
	// for assigning colors to players
	private static final Color[] colors = new Color[]{ Color.BLUE, Color.RED, Color.GREEN,
	                                                   Color.YELLOW, Color.BLACK };
	private int next_color_index = 0;

	public State clone() {
		State that = new State();
		that.random.setSeed(this.random.getSeed());
		that.players = this.players.clone();
		for (int i = 0; i < players.length; i++)
			that.players[i] = that.players[i].clone();
		that.player_order = this.player_order.clone();
		that.owner_by_railway.putAll(this.owner_by_railway);
		that.deck.addAll(this.deck);
		that.open_deck.addAll(this.open_deck);
		that.discarded.addAll(this.discarded);
		that.missions.addAll(this.missions);
		that.game_over = this.game_over;
		that.last_player = this.last_player;
		that.next_color_index = this.next_color_index;
		return that;
	}

	public String toString() {
		return "State(deck: "+deck+" open_deck: "+open_deck
		     + " missions: "+missions+" last_player: "+last_player+")";
	}

	public State() {}
	
	public State(String... player_names) {
		int ni = player_names.length;
		players = new PlayerState[ni];
		player_order = new int[ni];
		for (int i = 0; i < ni; i++) {
			players[i] = new PlayerState(i, player_names[i], colors[next_color_index++]);
			player_order[i] = i;
		}
	}

	public void setup() {
		for (Color c: Color.values())
			deck.addAll(Collections.nCopies(NCARDS_PER_COLOR, c));
		// two more grey cards than other colors
		deck.add(Color.GREY); deck.add(Color.GREY);

		missions.addAll(Mission.missions);
		Collections.shuffle(missions, new java.util.Random(random.getSeed()));

		for (PlayerState p: players) {
			for (int i = 0; i < INITIAL_HAND_SIZE; i++)
				p.hand.add(deck.draw(random));
			// XXX: officially, players can choose to discard one
			// of the missions dealt.
			for (int i = 0; i < INITIAL_MISSION_COUNT; i++)
				p.missions.add(missions.removeFirst());
		}

		for (int i = 0; i < OPEN_DECK_SIZE; i++)
			open_deck.add(deck.draw(random));
	}

	public void switchTurns() {
		int curr = player_order[0];

		if (currentPlayerState().almostOutOfCars()) {
			if (last_player < 0) {
				last_player = curr;
			} else if (last_player == curr) {
				game_over = true;
				return;
			}
		}
		
		// shifts all elements forward, putting the first element in the back
		int ni = player_order.length;
		for (int i = 1; i < ni; i++)
			player_order[i-1] = player_order[i];
		player_order[ni-1] = curr;
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

	// TODO: this shouldn't be necessary
	public int playerHandleByName(String name) {
		for (PlayerState ps: players) {
			if (ps.name.equals(name))
				return ps.handle;
		}
		throw new RuntimeException("no player with name "+name);
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
	
	public void restoreDecks() {
		if (deck.isEmpty()) {
			deck = discarded;
			discarded = new CardBag();
		}
		while (open_deck.size() < OPEN_DECK_SIZE && !deck.isEmpty())
			open_deck.add(deck.draw(random));
	}
	
	public void applyDecision(Decision d) throws IllegalDecisionException {
		d.requireLegal(this);
		d.apply(this);
	}

	public Set<Decision> allPossibleDecisions() {
		PlayerState ps = currentPlayerState();
		Set<Decision> ds = new LinkedHashSet<Decision>(100);

		if (ps.drawn_missions == null) {
			if (ps.drawn_card == null) {
				// claim
				for (Railway r: Railway.railways) {
					if (!isClaimed(r) && r.length <= ps.ncars && !ps.railways.contains(r.dual)) {
						for (Color c: Color.values()) {
							CardBag cs = ps.hand.cardsToClaim(r, c);
							if (cs != null)
								ds.add(new ClaimRailwayDecision(r, cs));
						}
					}
				}
			}
			
			for (Color c: Color.values())
				if (open_deck.contains(c))
					ds.add(new DrawCardDecision(c));

			if (!deck.isEmpty())
				ds.add(new DrawCardDecision(null));
			
			if (ps.drawn_card == null) {
				ds.add(new DrawMissionsDecision());
			}
		} else {
			// keep
			for (Set<Mission> ms: Util.powerset(ps.drawn_missions)) {
				if (ms.isEmpty())
					continue;
				ds.add(new KeepMissionsDecision(ms));
			}
		}

		return ds;
	}
}
