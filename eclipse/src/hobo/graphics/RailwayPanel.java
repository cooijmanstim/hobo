package hobo.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.geom.Line2D;

import hobo.City;
import javax.swing.JPanel;

public class RailwayPanel extends JPanel {

	private City city1, city2;
	
	public RailwayPanel(City city1, City city2) {
		this.city1 = city1;
		this.city2 = city2;
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		Stroke stroke = new BasicStroke(10);
		g2.setStroke(stroke);
		int[] xCord = new int[] {123,156,157,125};
		int[] yCord = new int[] {98,93,102,105};
//		Polygon figure = new Polygon(xCord, yCord, xCord.length);
		Line2D.Double line = new Line2D.Double(city1.x, city1.y, city2.x, city2.y);
		g2.setColor(Color.BLUE);
//		g2.fill(figure);
		g2.draw(line);
	}
}
