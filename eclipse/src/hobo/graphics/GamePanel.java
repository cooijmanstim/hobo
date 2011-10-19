package hobo.graphics;

import hobo.CardBag;
import hobo.ClaimRailwayDecision;
import hobo.Color;
import hobo.Decision;
import hobo.Event;
import hobo.Mission;
import hobo.Railway;
import hobo.Player;
import hobo.PlayerInteraction;
import hobo.State;
import hobo.Visualization;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class GamePanel extends JPanel implements Visualization {
	private MapPanel map;
	private PlayersPanel players;
	private DecksPanel decks;
	private HandPanel hand;
	private JPanel panel2, panel3;
	private MissionsPanel missions;
	
	private State state = null;
	
	public GamePanel() {
		setLayout(new BorderLayout());
		
		map = new MapPanel(this);
		missions = new MissionsPanel(this);
		players = new PlayersPanel(this);
		decks = new DecksPanel(this);
		hand = new HandPanel(this);
		
		panel2 = new JPanel();
		panel2.setLayout(new BorderLayout());
		panel2.add(decks, BorderLayout.WEST);
		panel2.add(new JScrollPane(map), BorderLayout.CENTER);

		panel3 = new JPanel();		
		panel3.setLayout(new BorderLayout());
		panel3.add(missions, BorderLayout.WEST);
		panel3.add(hand, BorderLayout.CENTER);
		
		add(players, BorderLayout.NORTH);
		add(panel2, BorderLayout.CENTER);
		add(panel3, BorderLayout.SOUTH);
	}
	
	@Override public void reflect(State s) {
		state = s;
		players.reflect(s);
		decks.reflect(s);
		map.reflect(s);
		missions.reflect(s);
		hand.reflect(s);
	}
	
	public PlayerInteraction getUserInterface() {
		return new PlayerInteraction() {
			@Override public Decision askDecision(Player p, State s) {
				reflect(s);
				return getDecision();
			}

			@Override public void tellIllegal (Player p, State s, Decision d, String reason) { message("illegal move: "+reason); }
			@Override public void tellDraw    (Player p, State s) { message("draw game"   ); }
			@Override public void tellLoss    (Player p, State s) { message(p+", you lose"); }
			@Override public void tellWin     (Player p, State s) { message(p+", you win" ); }
			
			@Override public void observe(Event e) {}
		};
	}
	
	public void claim(Railway r) {
		Color selection = hand.selection();
		CardBag cards = state.currentPlayerState().hand.cardsToClaim(r, selection);
		// if couldn't afford it, just let the move be illegal so the user will be notified
		if (cards == null)
			cards = new CardBag();
		registerDecision(new ClaimRailwayDecision(r, cards));
	}

	// TODO: show this in the UI somewhere, somehow.
	public void message(String s) {
		System.out.println(s);
	}

	// here be the thread-hackery that is required to make
	// PlayerInteraction work for a graphical interface.
	private final Object lastDecisionLock = new Object();
	private Decision lastDecision = null;
	
	public void registerDecision(Decision d) {
		synchronized (lastDecisionLock) {
			lastDecision = d;
			lastDecisionLock.notify();
		}
	}
	
	public Decision getDecision() {
		try {
			Decision d = null;
			while (d == null) {
					synchronized (lastDecisionLock) {
						lastDecisionLock.wait();
						d = lastDecision;
						lastDecision = null;
					}
			}
			return d;
		} catch (InterruptedException e) {
			return null;
		}
	}
}
