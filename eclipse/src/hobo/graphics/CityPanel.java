package hobo.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;

import hobo.City;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class CityPanel extends JPanel{
	private City city;
	
	public CityPanel(City city) {
		this.city = city;
		addMouseListener(new listener());
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		Ellipse2D.Double cirlce = new Ellipse2D.Double(city.x-5, city.y-5, 10, 10);
		g2.setColor(Color.RED);
		g2.fill(cirlce);
		g2.setColor(Color.BLACK);
//		g2.drawString(city.name, (int)city.x-10, (int)city.y-10);
	}
	
	private class listener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			JOptionPane.showMessageDialog(null, "Test");
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
