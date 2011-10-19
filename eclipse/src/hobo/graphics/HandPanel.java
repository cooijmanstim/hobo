package hobo.graphics;

import hobo.CardBag;
import hobo.Visualization;
import hobo.State;
import hobo.Color;

import java.util.ArrayList;
import java.util.List;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

public class HandPanel extends JPanel implements Visualization {
	private List<HandCardPanel> children;
	private final GamePanel gamePanel;
	private Color selection = null;

	public HandPanel(GamePanel gamePanel) {
		this.gamePanel = gamePanel;
		setLayout(new GridLayout(1, Color.values().length));
		setPreferredSize(new Dimension(200*Color.values().length, 100));
	}
	
	@Override public void reflect(State s) {
		if (children == null) {
			children = new ArrayList<HandCardPanel>();
			for (final Color c: Color.values()) {
				final HandCardPanel hcp = new HandCardPanel(c);
				hcp.addMouseListener(new MouseAdapter() {
					@Override public void mouseClicked(MouseEvent e) {
						selection = c;
						for (HandCardPanel hcp: children)
							hcp.markNotSelected();
						hcp.markSelected();
					}
				});
				children.add(hcp);
				add(hcp);

				// make sure there is a selection
				if (selection == null)
					hcp.dispatchEvent(new MouseEvent(hcp, MouseEvent.MOUSE_CLICKED,
					                                 System.currentTimeMillis(), 0,
					         /* fuck you, java */    10, 10, 1, false));
			}
		}

		CardBag cards = s.currentPlayerState().hand;
		for (HandCardPanel hcp: children)
			hcp.setQuantity(cards.count(hcp.color));
	}
	
	public Color selection() { return selection; }
}
