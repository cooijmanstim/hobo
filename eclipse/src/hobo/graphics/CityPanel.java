package hobo.graphics;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import hobo.City;
import hobo.Railway;
import javax.swing.JPanel;

public class CityPanel extends JPanel{
	private City city;
	private GamePanel panel;
	
	public CityPanel(City city, GamePanel panel) {
		this.city = city;
		addMouseListener(new listener());
		setBounds((int)city.x-5, (int)city.y-5, 10, 10);
		this.panel = panel;
	}
	
	@Override
	public void paintComponent(Graphics g) {
	}
	
	private class listener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			if(!city.railways.isEmpty())
				new RailChooserFrame(city.railways, panel);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			Railway.railways.size();
			for(Railway rail : city.railways) {
				GamePanel.connections.add(new RailwayPanel(rail, 0));
			}
			panel.repaint();
		}

		@Override
		public void mouseExited(MouseEvent e) {
			GamePanel.connections.clear();
			panel.repaint();
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}	
	}
}
