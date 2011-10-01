package hobo.graphics;

import hobo.City;

import java.util.ArrayList;

import javax.swing.JFrame;


public class MainFrame extends JFrame {

	private GamePanel gamePanel;
	private ArrayList<CityPanel> cityPanels;
	
	public MainFrame() {
		gamePanel = new GamePanel();
		cityPanels = new ArrayList<CityPanel>();
		City[] arrayCity = (City[]) City.cities.toArray();
		for(int i = 0; i < City.cities.size(); i++) {
			cityPanels.add(new CityPanel(arrayCity[i]));
		}
	}
	
	public void setUpFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		add(gamePanel);
		for(int i = 0; i < cityPanels.size(); i++) {
			add(cityPanels.get(i));
		}
		pack();
	}

	public static void main(String[] args) {
		MainFrame frame = new MainFrame();
		frame.setUpFrame();
		frame.setVisible(true);
	}

}
