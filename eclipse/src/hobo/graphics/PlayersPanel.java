package hobo.graphics;

import hobo.PlayerState;
import hobo.Visualization;
import hobo.State;

import java.util.Set;
import java.util.HashSet;

import java.awt.GridLayout;
import javax.swing.JPanel;

public class PlayersPanel extends JPanel implements Visualization {
	private Set<PlayerPanel> children = null;
	
	public PlayersPanel(GameVisualization gv) {
		setLayout(new GridLayout(1, 4));		
	}
	
	@Override public void reflect(State s) {
		if (children == null) {
			children = new HashSet<PlayerPanel>();
			for (int handle: s.players()) {
				PlayerPanel pp = new PlayerPanel(handle);
				children.add(pp);
				add(pp);
			}
		}
		
		for (PlayerPanel pp: children)
			pp.reflect(s);
	}
}
