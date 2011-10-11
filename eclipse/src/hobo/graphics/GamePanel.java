package hobo.graphics;

import hobo.City;
import hobo.Railway;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JPanel;

public class GamePanel extends JPanel {
	private Image map;
	private ArrayList<CityPanel> cityPanels;
	public static ArrayList<RailwayPanel> connections;
	public static ArrayList<RailwayPanel> railsways;
	
	public GamePanel() {
		map = getToolkit().getImage("src/railways/background.jpg");
		setPreferredSize(new Dimension(1024, 683));
		connections = new ArrayList<RailwayPanel>();
		railsways = new ArrayList<RailwayPanel>();
		cityPanels = new ArrayList<CityPanel>();
		for(City city : City.cities) {
			CityPanel cp = new CityPanel(city, this);
			cityPanels.add(cp);
			add(cp);
		}
		setLayout(null);
		
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(map, 0, 0, this);
		
		for (int i = 0; i < connections.size(); i++) {
			g2.drawImage(connections.get(i).railway_img, 0, 0, this);
		}
		for (int i = 0; i < railsways.size(); i++) {
			g2.drawImage(railsways.get(i).railway_img, 0, 0, this);
		}
	}
}
