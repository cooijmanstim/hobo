package hobo;

import hobo.graphics.GamePanel;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class Main {
	public static void main(String[] args) {
		graphical();
	}

	public static void headless() {
		long then = System.currentTimeMillis();
		long totalndecisions = 0;
		for (int i = 0; i < 10; i++) {
			Game g = new Game("",
			                  Player.fromConfiguration("uncertain montecarlo name:carlo strategic:false sample_size:5 decision_time:5000"),
			                  Player.fromConfiguration("uncertain minimax name:joshua sample_size:5 decision_time:5000"));
			g.play();
			totalndecisions += g.ndecisions;
		}
		long now = System.currentTimeMillis();
		System.out.println(now - then);
		System.out.println((totalndecisions*1.0/1000)+" ("+totalndecisions+"/"+1000+") decisions per game on average");
	}

	public static void textual() {
		PlayerInteraction ui = new TextualPlayerInteraction();
		Game g = new Game("", new HumanPlayer("x", ui));
		g.registerObserver(ui);
		g.play();
	}

	public static void graphical() {
		// this is a mess.  don't look at it.
		SwingUtilities.invokeLater(new Runnable() {
			private boolean hasbeenrun = false;
			private JFrame mainFrame, newGameFrame;

			public void run() {
				// just to be sure
				assert(!hasbeenrun);
				hasbeenrun = true;

				newGameFrame = setupNewGameFrame();
				mainFrame = new JFrame("crapshoot");

				JButton b = new JButton("new game...");
				b.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						newGameFrame.setVisible(true);
					}
				});

				mainFrame.add(b, BorderLayout.PAGE_END);
				mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				mainFrame.pack();
				mainFrame.setVisible(true);
			}

			public JFrame setupNewGameFrame() {
				final JFrame f = new JFrame();

				String[] labels = {
					"Game configuration:",
					"Player 1:",
					"Player 2:",
					"Player 3:",
					"Player 4:",
					"Player 5:",
				};
				
				final JTextField[] fields = {
					new JTextField("seed:0"),
					new JTextField("uncertain montecarlo name:carlo sample_size:3 strategic:true decision_time:6000"),
					new JTextField("uncertain minimax name:joshua sample_size:3 decision_time:6000"),
					new JTextField(""),
					new JTextField(""),
					new JTextField(""),
				};
				
				f.setLayout(new GridLayout(labels.length + 1, 2));
				for (int i = 0; i < labels.length; i++) {
					f.add(new JLabel(labels[i]));
					f.add(fields[i]);
				}

				JButton okbutton = new JButton("OK");
				okbutton.addActionListener(new ActionListener() {
					@Override public void actionPerformed(ActionEvent e) {
						String[] configurations = new String[fields.length];
						int i = 0;
						for (JTextField field: fields)
							configurations[i++] = field.getText();
						newGame(configurations);
						f.setVisible(false);
					}
				});
				f.add(okbutton);

				f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
				f.pack();
				return f;
			}
			
			// stuff specific to the currently running game
			private Thread gameThread = null;
			private GamePanel gamePanel = null;
			private Game game = null;

			public void newGame(String... configurations) {
				List<Player> players = new ArrayList<Player>();
				for (int i = 1; i < configurations.length; i++) {
					String s = configurations[i].trim();
					if (s.isEmpty())
						continue;

					players.add(Player.fromConfiguration(s));
				}
				
				newGame(configurations[0], players.toArray(new Player[0]));
			}
			
			public void newGame(String configuration, Player[] players) {
				final GamePanel gp = new GamePanel();
				
				for (Player p: players)
					if (p instanceof HumanPlayer)
						((HumanPlayer)p).setUI(gp.getUserInterface());
				
				final Game g = new Game(configuration, players);

				g.registerObserver(new GameObserver() {
					@Override public void observe(final Event e) {
						try {
							javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
								public void run() {
									gp.reflect(e.state);
									gp.repaint();
								}
							});
						} catch (InterruptedException ex) {
							g.abort();
						} catch (InvocationTargetException ex) {
							ex.printStackTrace();
							System.exit(1);
						}
					}
				});

				if (gameThread != null) {
					game.abort();
					gameThread.interrupt();
				}
				game = g;
				if (gamePanel != null)
					mainFrame.remove(gamePanel);
				gamePanel = gp;
				
				mainFrame.add(gamePanel, BorderLayout.CENTER);
				mainFrame.validate();
				mainFrame.pack();
				
				gameThread = new Thread(new Runnable() {
					public void run() { g.play(); }
				});
				gameThread.start();
			}
		});
	}	
}
