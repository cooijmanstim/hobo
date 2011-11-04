package hobo.graphics;

import hobo.City;
import hobo.Railway;
import hobo.State;
import hobo.PlayerState;
import hobo.Visualization;

import java.util.Set;
import java.util.HashSet;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JPanel;

public class CityPanel extends JPanel implements Visualization {
	private final City city;
	private Set<Railway> railways;
	
	public CityPanel(final City city, final GamePanel gamePanel, final MapPanel mapPanel) {
		this.city = city;
		this.railways = new HashSet<Railway>(city.railways);
		setBounds((int)city.x-5, (int)city.y-5, 10, 10);
		addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				// this is decision-making stuff
				if(!railways.isEmpty())
					new RailChooserFrame(city, railways, gamePanel, mapPanel);
			}

			@Override public void mouseEntered(MouseEvent e) {
				mapPanel.makeVisible(railways);
				mapPanel.repaint();
			}

			@Override public void mouseExited(MouseEvent e) {
				mapPanel.clearVisible();
				mapPanel.repaint();
			}
		});
	}

	@Override public void reflect(State s) {
		// railways to show on hover depends on railway ownerships and current player's hand
		PlayerState ps = s.currentPlayerState();
		railways = new HashSet<Railway>(city.railways);
		// loop through city.railways so we don't modify the collection we're looping through
		for (Railway r: city.railways) {
			if (!ps.hand.canAfford(r) || s.isClaimed(r))
				railways.remove(r);
		}
	}
	
	@Override public void paintComponent(Graphics g) {}
}
