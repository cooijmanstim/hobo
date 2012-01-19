package hobo;

import java.util.*;

public class Game {
	public final State state;
	public final Player[] players;
	private final List<GameObserver> observers = new ArrayList<GameObserver>();
	public int ndecisions = 0;
	private boolean verbose = true;

	public Game(String configuration, Player... players) {
		this.players = players;

		int ni = players.length;
		String[] names = new String[ni];
		for (int i = 0; i < ni; i++) {
			names[i] = players[i].name();
			players[i].setHandle(i);
		}

		for (Map.Entry<String,String> entry: Util.parseConfiguration(configuration).entrySet()) {
			String k = entry.getKey(), v = entry.getValue();
			if (k.equals("verbose")) verbose = Boolean.parseBoolean(v);
		}
		
		state = State.fromConfiguration(configuration, names);
	}

	public State getState() {
		return state;
	}
	
	public void play() {
		state.setup();
		Event e = new Event(state, null, null, null);
		notifyPlayers(e);
		notifyObservers(e);

		while (true) {
			try {
				advance();
				
				if (aborted)
					return;
				
				if (state.gameOver()) {
					if (state.isDraw()) {
						for (Player p: players)
							p.draw(state);
					} else {
						Player w = players[state.winner()];
						for (Player l: players) {
							if (l != w)
								l.loss(state);
						}
						w.win(state);
					}
					break;
				}
			} catch (IllegalDecisionException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void advance() {
		Player p = players[state.currentPlayer()];
	
		Decision d;
		AppliedDecision ad;
		while (true) {
			d = p.decide(state);
			if (verbose) System.out.println(p.name()+" decided "+d);
			if (d == null) {
				abort();
				return;
			}
			ndecisions++;
			try {
				ad = state.applyDecision(d);
				break;
			} catch (IllegalDecisionException e) {
				if (verbose) System.out.println("illegal decision: "+d.reasonForIllegality(state));
				p.illegal(state, d, e.reason);
			}
		}

		Event e = new Event(state, p, d, ad);
		notifyPlayers(e);
		notifyObservers(e);
	}
	
	public void printScores() {
		for (Player p: players) {
			PlayerState ps = state.playerState(p.handle);
			System.out.println(ps.name+": "+ps.finalScore());
		}
	}
	
	public String returnScores() {
		String result = "";
		for (Player p: players) {
			PlayerState ps = state.playerState(p.handle);
			result += ps.name+": "+ps.finalScore();
		}
		return result;
	}
	
	public PlayerState whoWon() {
		PlayerState[] players = new PlayerState[state.players().length];
		players[0] = state.playerState(0);
		players[1] = state.playerState(1);
		PlayerState winningPlayer = null;
		int h = Integer.MIN_VALUE;
		for(int i = 0; i < players.length; i++) {
			if(players[i].finalScore() > h) {
				winningPlayer = players[i];
				h = players[i].finalScore();
			}
		}
		return winningPlayer;
	}

	public int[] scores() {
		int[] scores = new int[players.length];
		for (int i = 0; i < players.length; i++)
			scores[i] = state.playerState(i).finalScore();
		return scores;
	}

	private boolean aborted = false;
	public void abort() {
		aborted = true;
	}

	public void notifyPlayers(Event e) {
		for (Player p: players)
			p.perceive(e);
	}

	public void registerObserver(GameObserver go) {
		observers.add(go);
	}

	public void notifyObservers(Event e) {
		for (GameObserver go: observers)
			go.observe(e);
	}
}
