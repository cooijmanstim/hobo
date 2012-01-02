package hobo.graphics;

import hobo.City;
import hobo.Railway;
import hobo.Visualization;
import hobo.State;
import hobo.PlayerState;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.*;

import javax.swing.JPanel;

public class MapPanel extends JPanel implements Visualization {
	private final Image map;
	private List<Image> claimedRailways = new ArrayList<Image>();
    private List<Image> visibleRailways = new ArrayList<Image>(); // when hovering over a city
    private final Set<CityPanel> cityPanels = new LinkedHashSet<CityPanel>();
	
	public MapPanel(GamePanel gv) {
		map = getToolkit().getImage("src/railways/background.jpg");
		for(City city: City.values()) {
			CityPanel cp = new CityPanel(city, gv, this); 
			add(cp);
			cityPanels.add(cp);
		}
		setPreferredSize(new Dimension(1024, 683));
		setLayout(null);
	}
	
	@Override public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(map, 0, 0, this);
		for (Image i: claimedRailways)
			g2.drawImage(i, 0, 0, this);
		for (Image i: visibleRailways)
			g2.drawImage(i, 0, 0, this);
	}

	public void clearVisible() {
		visibleRailways = new ArrayList<Image>();
	}
	
	public void makeVisible(Collection<Railway> railways) {
		clearVisible();
		for (Railway r: railways)
			visibleRailways.add(getToolkit().getImage("src/railways/"+r.imagePath+"null.png"));
	}
	
	@Override public void reflect(State s) {
		claimedRailways = new ArrayList<Image>();
		for (PlayerState ps: s.playerStates()) {
			for (Railway r: ps.railways)
				claimedRailways.add(getToolkit().getImage("src/railways/"+r.imagePath+ps.color+".png"));
		}
		for (CityPanel cp: cityPanels)
			cp.reflect(s);
	}
}
