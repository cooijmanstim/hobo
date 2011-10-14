package hobo.graphics;

import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import hobo.City;
import hobo.Railway;
import javax.swing.JPanel;

public class CityPanel extends JPanel{
	private final City city;
	private final MapPanel mapPanel;
	private final GameVisualization visualization;
	
	public CityPanel(final City city, final GameVisualization gv, final MapPanel mapPanel) {
		this.city = city;
		this.mapPanel = mapPanel;
		this.visualization = gv;
		setBounds((int)city.x-5, (int)city.y-5, 10, 10);
		addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				// this is decision-making stuff
//				if(!city.railways.isEmpty())
//					new RailChooserFrame(city.railways, visualization, mapPanel);
			}

			@Override public void mouseEntered(MouseEvent e) {
				mapPanel.makeVisible(city.railways);
				mapPanel.repaint();
			}

			@Override public void mouseExited(MouseEvent e) {
				mapPanel.clearVisible();
				mapPanel.repaint();
			}
		});
	}
	
	@Override public void paintComponent(Graphics g) {}
}
