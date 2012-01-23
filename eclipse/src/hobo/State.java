package hobo;

import java.util.*;

public class State implements Cloneable {
	public static final int NCARDS_PER_COLOR = 12,
	                        INITIAL_HAND_SIZE = 4,
	                        INITIAL_MISSION_COUNT = 3,
	                        OPEN_DECK_SIZE = 5;

	public static final CardBag INITIAL_DECK = new CardBag() {{
		for (Color c: Color.values())
			add(c, NCARDS_PER_COLOR);
		// two more grey cards than other colors
		add(Color.GREY);
		add(Color.GREY);
	}};
	
	public MersenneTwisterFast random = new MersenneTwisterFast(0);

	// the index into this array is used to refer to a player in many places here.
	private PlayerState[] players;
	private int current_player;

	public Map<Railway,Integer> owner_by_railway = new EnumMap<Railway,Integer>(Railway.class);

	public CardBag deck      = new CardBag();
	public CardBag open_deck = new CardBag();
	public CardBag discarded = new CardBag();

	// deck of destination tickets
	public Set<Mission> missions = EnumSet.noneOf(Mission.class);
	
	private boolean game_over = false;
	private int last_player = -1;
	
	// for assigning colors to players
	private static final Color[] colors = new Color[]{ Color.BLUE, Color.RED, Color.GREEN,
	                                                   Color.YELLOW, Color.BLACK };
	private int next_color_index = 0;

	public State(State that) {
		this.random = that.random.clone();
		this.players = that.players.clone();
		for (int i = 0; i < players.length; i++)
			this.players[i] = that.players[i].clone();
		this.current_player = that.current_player;
		this.owner_by_railway.putAll(that.owner_by_railway);
		this.deck.addAll(that.deck);
		this.open_deck.addAll(that.open_deck);
		this.discarded.addAll(that.discarded);
		this.missions.addAll(that.missions);
		this.deck_restorations.addAll(that.deck_restorations);
		this.discardeds.addAll(that.discardeds);
		this.game_over = that.game_over;
		this.last_player = that.last_player;
		this.next_color_index = that.next_color_index;
	}
	
	public State(String[] player_names, long seed) {
		int ni = player_names.length;
		players = new PlayerState[ni];
		for (int i = 0; i < ni; i++)
			players[i] = new PlayerState(i, player_names[i], colors[next_color_index++]);
		current_player = 0;
		
		random = new MersenneTwisterFast(seed);
	}
	
	public static State fromConfiguration(String configuration, String... player_names) {
		long seed = System.currentTimeMillis();

		for (Map.Entry<String,String> entry: Util.parseConfiguration(configuration).entrySet()) {
			String k = entry.getKey(), v = entry.getValue();
			if (k.equals("seed")) seed = Long.parseLong(v);
		}

		return new State(player_names, seed);
	}

	@Override public State clone() {
		return new State(this);
	}
	
	@Override public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof State)) return false;
		State that = (State)o;
		if (!this.random.stateEquals(that.random)) return false;
		if (!Arrays.deepEquals(this.players, that.players)) return false;
		if (this.current_player != that.current_player) return false;
		if (!this.owner_by_railway.equals(that.owner_by_railway)) return false;
		if (!this.deck.equals(that.deck)) return false;
		if (!this.open_deck.equals(that.open_deck)) return false;
		if (!this.discarded.equals(that.discarded)) return false;
		if (!this.missions.equals(that.missions)) return false;
		if (!this.deck_restorations.equals(that.deck_restorations)) return false;
		if (!this.discardeds.equals(that.discardeds)) return false;
		if (this.game_over != that.game_over) return false;
		if (this.last_player != that.last_player) return false;
		if (this.next_color_index != that.next_color_index) return false;
		return true;
	}

	public String toString() {
		return "State(game_over: "+game_over+" deck: "+deck+" open_deck: "+open_deck
		     + " missions: "+missions+" last_player: "+last_player+")";
	}

	public void setup() {
		deck = INITIAL_DECK.clone();
		open_deck.addAll(deck.remove_sample(OPEN_DECK_SIZE, random));
		missions.addAll(Arrays.asList(Mission.values()));

		for (PlayerState p: players) {
			p.hand.addAll(deck.remove_sample(INITIAL_HAND_SIZE, random));
			p.receiveMissions(Util.remove_sample(missions, INITIAL_MISSION_COUNT, random, EnumSet.noneOf(Mission.class)));
		}
	}

	public void switchTurns() {
		if (currentPlayerState().almostOutOfCars()) {
			if (last_player < 0) {
				last_player = current_player;
			} else if (last_player == current_player) {
				game_over = true;
				return;
			}
		}

		current_player++;
		current_player %= players.length;
	}

	public void unswitchTurns() {
		if (game_over) {
			game_over = false;
			return;
		}

		current_player--;
		if (current_player < 0)
			current_player += players.length;
		
		if (currentPlayerState().almostOutOfCars() && last_player == current_player)
			last_player = -1;
	}
	
	// this results in invalid states (used in best-reply search)
	public void switchToPlayer(int handle) {
		current_player = handle;
	}

	public int[] players() {
		int[] handles = new int[players.length];
		for (int i = 0; i < handles.length; i++)
			handles[i] = i;
		return handles;
	}
	
	public List<PlayerState> playerStates() {
		List<PlayerState> playerStates = new ArrayList<PlayerState>();
		for (int i = 0; i < players.length; i++)
			playerStates.add(players[i]);
		return playerStates;
	}
	
	public int currentPlayer() {
		return current_player;
	}

	public PlayerState currentPlayerState() {
		return playerState(current_player);
	}

	public PlayerState playerState(int handle) {
		return players[handle];
	}

	public boolean gameOver() {
		return game_over;
	}

	public boolean isDraw() {
		int winner = winner();
		for (int handle: players()) {
			if (handle != winner && players[winner].finalScore() == players[handle].finalScore())
				return true;
		}
		return false;
	}

	public int winner() {
		int xmax = Integer.MIN_VALUE;
		int winner = -1;
		for (int i = 0; i < players.length; i++) {
			int x = players[i].finalScore();
			if (x > xmax) {
				xmax = x;
				winner = i;
			}
		}
		return winner;
	}

	// how far is the player ahead of the best other player?
	public int aheadness(int i) {
		int xmax = Integer.MIN_VALUE;
		for (int j = 0; j < players.length; j++) {
			int x = players[j].finalScore();
			if (i != j && x > xmax)
				xmax = x;
		}
		return players[i].finalScore() - xmax;
	}

	public boolean isClaimed(Railway r) {
		return owner_by_railway.containsKey(r);
	}

	public Set<Railway> usableRailwaysFor(int player) {
		Set<Railway> result = EnumSet.copyOf(players[player].railways);
		for (Railway r: Railway.all) {
			if (!isClaimed(r))
				result.add(r);
		}
		return result;
	}
	
	public Set<Railway> freeRailways() {
		Set<Railway> railways = EnumSet.allOf(Railway.class);
		railways.removeAll(owner_by_railway.keySet()); // XXX: cost of keySet()?
		return railways;
	}

	public CardBag openCards() {
		return open_deck;
	}

	// restoreDecks undo info
	public final Deque<CardBag> deck_restorations = new LinkedList<CardBag>();
	public final Deque<CardBag> discardeds = new LinkedList<CardBag>();

	public void restoreDecks() {
		int k = OPEN_DECK_SIZE - open_deck.size();
		if (deck.size() < k) {
			deck.addAll(discarded);
			discardeds.push(discarded);
			discarded = new CardBag();
		} else {
			discardeds.push(null);
		}
		CardBag cs = deck.draw(k, random);
		deck_restorations.push(cs);
		open_deck.addAll(cs);
	}
	
	public void unrestoreDecks() {
		CardBag cs = deck_restorations.pop();
		open_deck.removeAll(cs);
		deck.addAll(cs);
		CardBag ds = discardeds.pop();
		if (ds != null) {
			discarded.addAll(ds);
			deck.removeAll(ds);
		}
	}
	
	public AppliedDecision applyDecision(Decision d) throws IllegalDecisionException {
		d.requireLegal(this);
		return d.apply(this, true);
	}
	
	public Set<Decision> allPossibleDecisions() {
		return allPossibleDecisionsFor(current_player);
	}

	public Set<Decision> allPossibleDecisionsFor(int player) {
		Set<Decision> ds = new LinkedHashSet<Decision>(100);
		PlayerState ps = players[player];
		ds = ClaimRailwayDecision.availableTo(this, ps, ds);
		ds = DrawCardDecision.availableTo(this, ps, ds);
		ds = DrawMissionsDecision.availableTo(this, ps, ds);
		ds = KeepMissionsDecision.availableTo(this, ps, ds);
		return ds;
	}

	public boolean farFromOver() {
		for (PlayerState ps: players) {
			if (ps.ncars < (PlayerState.INITIAL_NCARS / 2))
				return false;
		}
		return true;
	}
}
