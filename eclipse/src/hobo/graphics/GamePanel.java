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
	private ArrayList<RailwayPanel> rails;
	
	
	public GamePanel() {
		map = getToolkit().getImage("background.jpg");
		setPreferredSize(new Dimension(1024, 683));
		addMouseListener(new clickListener());

		cityPanels = new ArrayList<CityPanel>();
		for(City city : City.cities) {
			cityPanels.add(new CityPanel(city));
		}
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(map, 0, 0, this);
		for(int i = 0; i < cityPanels.size(); i++) {
			cityPanels.get(i).paint(g);
		}
		RailwayPanel railways = new RailwayPanel(Railway.railways.get(0));
		railways.paint(g2);
	}
	
	private class clickListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent arg0) {
			System.out.println(arg0.getX());
			System.out.println(arg0.getY());
			
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
