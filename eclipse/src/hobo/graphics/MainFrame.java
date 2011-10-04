package hobo.graphics;

import hobo.City;

import java.util.ArrayList;

import javax.swing.JFrame;


public class MainFrame extends JFrame {

	private GamePanel gamePanel;
	
	public MainFrame() {
		gamePanel = new GamePanel();
	}
	
	public void setUpFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		add(gamePanel);
		
		pack();
	}

	public static void main(String[] args) {
		MainFrame frame = new MainFrame();
		frame.setUpFrame();
		frame.setVisible(true);
	}

}
