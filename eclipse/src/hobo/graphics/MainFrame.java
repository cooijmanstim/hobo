package hobo.graphics;

import hobo.Mission;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class MainFrame extends JFrame {
	private GamePanel gamePanel;
	private PlayersPanel players;
	private DecksPanel decks;
	private HandPanel hand;
	private JPanel panel2, panel3;
	private MissionPanel mission;
	
	public MainFrame() {
		gamePanel = new GamePanel();
		mission = new MissionPanel();
		players = new PlayersPanel();
		decks = new DecksPanel();
		hand = new HandPanel();
		
		panel2 = new JPanel();
		panel3 = new JPanel();
	}
	
	public void setUpFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		panel2.setLayout(new BorderLayout());
		panel2.add(decks, BorderLayout.WEST);
		panel2.add(gamePanel, BorderLayout.CENTER);
		
		panel3.setLayout(new BorderLayout());
		panel3.add(mission, BorderLayout.WEST);
		panel3.add(hand, BorderLayout.CENTER);
		
		add(players,  BorderLayout.NORTH);
		add(panel2, BorderLayout.CENTER);
		add(panel3, BorderLayout.SOUTH);
		pack();
	}

	public static void main(String[] args) {
		MainFrame frame = new MainFrame();
		frame.setUpFrame();
		frame.setVisible(true);
	}
}
