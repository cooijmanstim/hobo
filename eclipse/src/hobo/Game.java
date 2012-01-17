package hobo;

import java.util.*;

public class Game {
	private final State state;
	private final Player[] players;
	private final List<GameObserver> observers = new ArrayList<GameObserver>();

	// testing belief
	private final Belief belief = new Belief(0);

	public Game(String configuration, Player... players) {
		this.players = players;

		int ni = players.length;
		String[] names = new String[ni];
		for (int i = 0; i < ni; i++) {
			names[i] = players[i].name();
			players[i].setHandle(i);
		}
		state = State.fromConfiguration(configuration, names);
	}

	public void play() {
		state.setup();
		belief.initialize(state);

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
			} catch (IllegalDecisionException e) {
				e.printStackTrace();
			}
		}
	}

	public void advance() {
		Player p = players[state.currentPlayer()];
	
		Decision d;
		AppliedDecision ad;
		while (true) {
			d = p.decide(state);
			System.out.println(p.name()+" decided "+d);
			if (d == null) {
				abort();
				return;
			}
			try {
				ad = state.applyDecision(d);
				break;
			} catch (IllegalDecisionException e) {
				System.out.println("illegal decision: "+d.reasonForIllegality(state));
				p.illegal(state, d, e.reason);
			}
		}

		Event e = new Event(state, p, d, ad);
		notifyPlayers(e);
		notifyObservers(e);

		// testing belief system
		belief.update(e);
		System.out.println("belief accuracy: "+belief.likelihoodOf(e.state)+" ("+belief.likelihoodOfCards(e.state)+" * "+belief.likelihoodOfMissions(e.state)+")");
		System.out.println("zero knowledge missions likelihood: "+Belief.zeroKnowledgeLikelihoodOfMissions(e.state));
		State sampled_state = belief.sample(e.state);

		System.out.println("sampled missions deck: "+sampled_state.missions+" actual mission deck: "+e.state.missions);
		for (int i = 0; i < players.length; i++)
			System.err.println("player "+i+" sampled missions: "+sampled_state.playerState(i).missions+" actual missions: "+e.state.playerState(i).missions);

		if (belief.likelihoodOf(e.state) == 1.0 && !e.state.equals(sampled_state)) {
			System.err.println("reality has likelihood 1 but sample differs from reality");
			System.err.println("sampled deck: "+sampled_state.deck+" actual deck: "+e.state.deck);
			for (int i = 0; i < players.length; i++)
				System.err.println("player "+i+" sampled hand: "+sampled_state.playerState(i).hand+" actual hand: "+e.state.playerState(i).hand);
			throw new RuntimeException();
		}
	}
	
	public void printScores() {
		for (Player p: players) {
			PlayerState ps = state.playerState(p.handle);
			System.out.println(ps.name+": "+ps.finalScore());
		}
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
