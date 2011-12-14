package hobo;

import hobo.graphics.GamePanel;

import java.util.*;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class Main {
	public static void main(String[] args) {
		graphical();
	}
	
	public static void textual() {
		PlayerInteraction ui = new TextualPlayerInteraction();
		Game g = new Game(new HumanPlayer("x", ui));
		g.registerObserver(ui);
		g.play();
	}

	public static void graphical() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				final JFrame f = new JFrame("crapshoot");
				
				JButton b = new JButton("new game...");
				b.addActionListener(new ActionListener() {
					private Thread gameThread = null;
					private GamePanel gamePanel = null; // hate hate hate

					public void actionPerformed(ActionEvent e) {
						final GamePanel gp = new GamePanel();
						final Game g = new Game(//new HumanPlayer("tim", gp.getUserInterface()),
		                                        new CoalitionalMinimaxPlayer("paranoid", 1, false, 30),
		                                        new CoalitionalMinimaxPlayer("joshua", 0.5, true, 30),
		                                        new CoalitionalMinimaxPlayer("maarten", 1, true, 30),
						                        new CoalitionalMinimaxPlayer("steven", 0.5, false, 30));

						g.registerObserver(new GameObserver() {
							@Override public void observe(final Event e) {
								javax.swing.SwingUtilities.invokeLater(new Runnable() {
									public void run() {
										gp.reflect(e.state);
										gp.repaint();
									}
								});
							}
						});
						
						if (gamePanel != null)
							f.remove(gamePanel);
						gamePanel = gp;
						f.add(gamePanel, BorderLayout.CENTER);
						
						f.validate();

						if (gameThread != null)
							gameThread.interrupt();
						gameThread = new Thread(new Runnable() {
							public void run() { g.play(); }
						});
						gameThread.start();
					}
				});
				
				b.doClick();

				f.add(b, BorderLayout.PAGE_END);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.pack();
				f.setVisible(true);
			}
		});
	}	
}
