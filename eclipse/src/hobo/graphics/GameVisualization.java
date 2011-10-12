package hobo.graphics;

import hobo.Mission;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameVisualization extends JPanel {
	private MapPanel map;
	private PlayersPanel players;
	private DecksPanel decks;
	private HandPanel hand;
	private JPanel panel2, panel3;
	private MissionsPanel missions;
	
	public GameVisualization() {
		map = new MapPanel();
		missions = new MissionsPanel();
		players = new PlayersPanel();
		decks = new DecksPanel();
		hand = new HandPanel();
		
		panel2 = new JPanel();
		panel2.setLayout(new BorderLayout());
		panel2.add(decks, BorderLayout.WEST);
		panel2.add(map, BorderLayout.CENTER);

		panel3 = new JPanel();		
		panel3.setLayout(new BorderLayout());
		panel3.add(missions, BorderLayout.WEST);
		panel3.add(hand, BorderLayout.CENTER);
		
		add(players,  BorderLayout.NORTH);
		add(panel2, BorderLayout.CENTER);
		add(panel3, BorderLayout.SOUTH);
	}
}
