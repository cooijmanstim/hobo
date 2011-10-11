package hobo.graphics;

import hobo.City;
import hobo.HumanPlayer;
import hobo.Mission;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;


public class MainFrame extends JFrame {

	private GamePanel gamePanel;
	private ArrayList<PlayerPanel> players;
	private JPanel panel, panel2, panel3, panel4, panel5;
	private MissionPanel mission;
	private ArrayList<TrainCardPanel> cards;
	private ArrayList<HandCardPanel> hands;
	
	public MainFrame() {
		gamePanel = new GamePanel();
		players = new ArrayList<PlayerPanel>();
		players.add(new PlayerPanel(new HumanPlayer("Chris", null), true, Color.BLUE));
		players.add(new PlayerPanel(new HumanPlayer("Tim", null), false, Color.RED));
		players.add(new PlayerPanel(new HumanPlayer("Jonathan", null), false, Color.GREEN));
		players.add(new PlayerPanel(new HumanPlayer("Levi", null), false, Color.YELLOW));
		players.add(new PlayerPanel(new HumanPlayer("Verkey", null), false, Color.GRAY));
		panel = new JPanel();
		panel.setLayout(new GridLayout(1, 4));
		ArrayList<Mission> mis = new ArrayList<Mission>();
		mis.add(Mission.missions.get(0));
		mis.add(Mission.missions.get(1));
		mis.add(Mission.missions.get(2));
		this.mission = new MissionPanel(mis);
		cards = new ArrayList<TrainCardPanel>();
		
		cards.add(new TrainCardPanel("Train_Black.png"));
		cards.add(new TrainCardPanel("Train_Blue.png"));
		cards.add(new TrainCardPanel("Train_Green.png"));
		cards.add(new TrainCardPanel("Train_Multicolor.png"));
		cards.add(new TrainCardPanel("Train_Red.png"));
		cards.add(new TrainCardPanel("Train_Empty.png"));
		panel4 = new JPanel();
		
		panel2 = new JPanel();
		panel3 = new JPanel();
		hands = new ArrayList<HandCardPanel>();
		hands.add(new HandCardPanel("Train_Black.png", 3));
		hands.add(new HandCardPanel("Train_Blue.png", 5));
		hands.add(new HandCardPanel("Train_Multicolor.png", 2));
		hands.add(new HandCardPanel("Train_Yellow.png", 1));
		panel5 = new JPanel();
	}
	
	public void setUpFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		for(int i = 0; i < players.size(); i++) {
			players.get(i).setBorder(new TitledBorder(new EtchedBorder(), "Player "+(i+1)));
			panel.add(players.get(i));
		}
		panel4.setLayout(new GridLayout(cards.size(), 1));
		panel4.setPreferredSize(new Dimension(200,100*cards.size()));
		for(int i = 0; i < cards.size(); i++) {
			panel4.add(cards.get(i));
		}
		panel5.setLayout(new GridLayout(1, hands.size()));
		panel5.setPreferredSize(new Dimension(200*hands.size(), 100));
		for(int i = 0; i < hands.size(); i++) {
			panel5.add(hands.get(i));
		}
		
		
		panel2.setLayout(new BorderLayout());
		panel2.add(panel4, BorderLayout.WEST);
		panel2.add(gamePanel, BorderLayout.CENTER);
		panel3.setLayout(new BorderLayout());
		panel3.add(mission, BorderLayout.WEST);
		panel3.add(panel5, BorderLayout.CENTER);
		
		add(panel,BorderLayout.NORTH);
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
