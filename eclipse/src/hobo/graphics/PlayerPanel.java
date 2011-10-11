package hobo.graphics;

import java.awt.Color;
import java.awt.GridLayout;

import hobo.HumanPlayer;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class PlayerPanel extends JPanel {
	public HumanPlayer player;
	public JLabel name;
	public static JLabel points;
	public static JLabel turn;
	
	public PlayerPanel(HumanPlayer player, boolean turnBol, Color color) {
		this.player = player;
		name = new JLabel(player.name());
		points = new JLabel("0 Pts.");
		if(turnBol) {
			turn = new JLabel("Currently his/her move");
		} else {
			turn = new JLabel();
		}
		
		
		setLayout(new GridLayout(3, 1));
		add(name);
		add(points);
		add(turn);
		
		setBackground(color);
	}

	
}
