package hobo.graphics;

import hobo.HumanPlayer;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class PlayersPanel extends JPanel {
	private ArrayList<PlayerPanel> players;
	
	public PlayersPanel() {
		setLayout(new GridLayout(1, 4));
		
		players = new ArrayList<PlayerPanel>();
		players.add(new PlayerPanel(new HumanPlayer("Chris", null), true, Color.BLUE));
		players.add(new PlayerPanel(new HumanPlayer("Tim", null), false, Color.RED));
		players.add(new PlayerPanel(new HumanPlayer("Jonathan", null), false, Color.GREEN));
		players.add(new PlayerPanel(new HumanPlayer("Levi", null), false, Color.YELLOW));
		players.add(new PlayerPanel(new HumanPlayer("Verkey", null), false, Color.GRAY));
		
		for(int i = 0; i < players.size(); i++) {
			players.get(i).setBorder(new TitledBorder(new EtchedBorder(), "Player "+(i+1)));
			add(players.get(i));
		}
	}
}
