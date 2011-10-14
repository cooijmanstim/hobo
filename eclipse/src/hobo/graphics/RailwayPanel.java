package hobo.graphics;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import hobo.City;
import hobo.Railway;
import javax.swing.JPanel;

public class RailwayPanel extends JPanel {
	public final City city1, city2;
	public final Image railway_img;
	
	public RailwayPanel(Railway railway, int color) {
		this.city1 = railway.source;
		this.city2 = railway.destination;
		String str = "src/railways/"+railway.imagePath+color+".png";
		railway_img = getToolkit().getImage(str);
	}
}
