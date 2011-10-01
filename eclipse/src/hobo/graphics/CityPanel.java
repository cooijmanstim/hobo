package hobo.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import hobo.City;

import javax.swing.JPanel;

public class CityPanel extends JPanel{
	private City city;
	
	public CityPanel(City city) {
		this.city = city;
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		Ellipse2D.Double cirlce = new Ellipse2D.Double(city.x-10, city.y-10, 20, 20);
		g.setColor(Color.RED);
		g2.fill(cirlce);
	}
}
