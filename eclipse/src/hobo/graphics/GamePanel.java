package hobo.graphics;

import hobo.CardBag;
import hobo.ClaimRailwayDecision;
import hobo.Color;
import hobo.Decision;
import hobo.DrawCardDecision;
import hobo.DrawMissionsDecision;
import hobo.Event;
import hobo.KeepMissionsDecision;
import hobo.Mission;
import hobo.PlayerState;
import hobo.Railway;
import hobo.Player;
import hobo.PlayerInteraction;
import hobo.State;
import hobo.Visualization;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.OverlayLayout;
import javax.swing.SwingUtilities;

public class GamePanel extends JLayeredPane implements Visualization {
	private MapPanel map;
	private PlayersPanel players;
	private DecksPanel decks;
	private HandPanel hand;
	private MissionsPanel missions;
	
	private State state = null;
	
	public GamePanel() {
		map = new MapPanel(this);
		missions = new MissionsPanel(this);
		players = new PlayersPanel(this);
		decks = new DecksPanel(this);
		hand = new HandPanel(this);

		JPanel panel2 = new JPanel();
		panel2.setLayout(new BorderLayout());
		panel2.add(decks, BorderLayout.WEST);
		panel2.add(new JScrollPane(map), BorderLayout.CENTER);

		JPanel panel3 = new JPanel();
		panel3.setLayout(new BorderLayout());
		panel3.add(missions, BorderLayout.WEST);
		panel3.add(hand, BorderLayout.CENTER);
		
		JPanel panel1 = new JPanel();
		panel1.setLayout(new BorderLayout());
		panel1.add(players, BorderLayout.NORTH);
		panel1.add(panel2, BorderLayout.CENTER);
		panel1.add(panel3, BorderLayout.SOUTH);

		setLayout(new OverlayLayout(this));
		add(panel1, JLayeredPane.DEFAULT_LAYER);
	}
	
	@Override public void reflect(State s) {
		state = s;
		
		if (s.gameOver()) {
			// show end state, overlaid with scores and shit
			JFrame f = new JFrame();
			int[] players = s.players();
			f.setLayout(new GridLayout(0, players.length));
			// sigh. gridlayout insists on ltr, ttb child insertion
			for (int y = 0; y < 3; y++) {
				for (int handle: players) {
					final PlayerState ps = s.playerState(handle);
					Component c = null;
					switch (y) {
					case 0:
						c = new JLabel(ps.name);
						break;
					case 1:
						c = new JLabel(""+ps.finalScore());
						break;
					case 2:
						JList l = new JList(ps.missions.toArray());
						l.setCellRenderer(new ListCellRenderer() {
							@Override public Component getListCellRendererComponent(JList l, Object o, int i,
							                                                        boolean isSelected,
							                                                        boolean hasFocus) {
								Mission m = (Mission)o;
								JLabel c = new JLabel(m.value+" "+m.source+" - "+m.destination);
								c.setBackground(ps.missionCompleted(m)
								                  ? new java.awt.Color(0.5f, 0.5f, 1f)
								                  : new java.awt.Color(1f, 0.5f, 0.5f));
								c.setOpaque(true);
								return c;
							}
						});
						c = l;
						break;
					}
					f.add(c);
				}
			}
			f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			f.pack();
			f.setVisible(true);
		} else {
			players.reflect(s);
			decks.reflect(s);
			map.reflect(s);
			missions.reflect(s);
			hand.reflect(s);
		}
	}
	
	public PlayerInteraction getUserInterface() {
		return new PlayerInteraction() {
			@Override public Decision askDecision(Player p, State s) {
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
		PlayerState ps = state.currentPlayerState();
		Color selection = hand.selection();
		CardBag cards = ps.hand.cardsToClaim(r, selection);
		// if couldn't afford it, just let the move be illegal so the user will be notified
		if (cards == null)
			cards = new CardBag();
		registerDecision(new ClaimRailwayDecision(ps.handle, r, cards));
	}

	public void drawMissions() {
		registerDecision(new DrawMissionsDecision(state.currentPlayer()));
	}

	public void keepMissions(Set<Mission> ms) {
		registerDecision(new KeepMissionsDecision(state.currentPlayer(), ms));
	}

	public void drawCard(Color c) {
		registerDecision(new DrawCardDecision(state.currentPlayer(), c));
	}
	
	
	public void message(String s) {
		System.out.println(s);
		add(new Toast(s), JLayeredPane.POPUP_LAYER);
	}

	// here be the thread-hackery that is required to make
	// PlayerInteraction work for a graphical interface.
	private final Object lastDecisionLock = new Object();
	private boolean awaitingDecision = false;
	private Decision lastDecision = null;
	
	public void registerDecision(Decision d) {
		synchronized (lastDecisionLock) {
			lastDecision = d;
			lastDecisionLock.notify();
		}
	}
	
	public Decision getDecision() {
		awaitingDecision = true;
		Decision d = null;
		try {
			while (d == null) {
					synchronized (lastDecisionLock) {
						lastDecisionLock.wait();
						d = lastDecision;
						lastDecision = null;
					}
			}
		} catch (InterruptedException e) {
			d = null;
		}
		awaitingDecision = false;
		return d;
	}
	
	public boolean awaitingDecision() {
		return awaitingDecision;
	}
}
