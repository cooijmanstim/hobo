package hobo.graphics;

import hobo.CardBag;
import hobo.Visualization;
import hobo.State;
import hobo.Color;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JPanel;

public class HandPanel extends JPanel implements Visualization {
	private ArrayList<HandCardPanel> children;
	private final GamePanel gamePanel;

	public HandPanel(GamePanel gamePanel) {
		this.gamePanel = gamePanel;
		setLayout(new GridLayout(1, Color.values().length));
		setPreferredSize(new Dimension(200*Color.values().length, 100));
	}
	
	@Override public void reflect(State s) {
		if (children == null) {
			children = new ArrayList<HandCardPanel>();
			for (Color c: Color.values()) {
				HandCardPanel hcp = new HandCardPanel(c);
				children.add(hcp);
				add(hcp);
			}
		}

		CardBag cards = s.currentPlayerState().hand;
		for (HandCardPanel hcp: children)
			hcp.setQuantity(cards.count(hcp.color));
	}
}
