package hobo;

import java.util.*;

public class Game {
	private final State state;
	private final Map<String,Player> players_by_name;
	private final List<GameObserver> observers = new ArrayList<GameObserver>();

	public Game(List<Player> ps) {
		Map<String,Player> players_by_name = new HashMap<String,Player>();
		for (Player p: ps)
			players_by_name.put(p.name(), p);
		this.players_by_name = Collections.unmodifiableMap(players_by_name);

		List<String> names = new ArrayList<String>();
		for (Player p: ps)
			names.add(p.name());
		state = new State(names);
	}

	public void play() {
		while (true) {
			advance();
		
			if (aborted)
				return;

			if (state.gameOver()) {
				if (state.isDraw()) {
					for (Player p: players_by_name.values())
						p.draw(state);
				} else {
					Player w = players_by_name.get(state.winner());
					for (Player l: players_by_name.values()) {
						if (l != w)
							l.loss(state);
					}
					w.win(state);
				}
				break;
			}
		}
	}

	public void advance() {
		Player p = players_by_name.get(state.currentPlayer());
	
		Decision d;
		while (true) {
			d = p.decide(state);
			if (state == null) {
				abort();
				return;
			}
			try {
				state.applyDecision(d);
				break;
			} catch (IllegalDecisionException e) {
				p.illegal(state, d);
			}
		}
	
		Event e = new Event(state, p, d);
		notifyPlayers(e);
		notifyObservers(e);
	}

	private boolean aborted = false;
	public void abort() {
		aborted = true;
	}

	public void notifyPlayers(Event e) {
		for (Player p: players_by_name.values())
			p.perceive(e);
	}

	public void registerObserver(GameObserver go) {
		observers.add(go);
		go.observe(new Event(state, null, null));
	}

	public void notifyObservers(Event e) {
		for (GameObserver go: observers)
			go.observe(e);
	}
}
