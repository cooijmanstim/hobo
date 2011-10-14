package hobo.graphics;

import hobo.Railway;
import hobo.ClaimRailwayDecision;
import hobo.CardBag;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;

public class RailChooserFrame extends JFrame {
	public static ArrayList<JButton> buttonArray;
	public static ArrayList<Railway> rail;
	private MapPanel mapPanel;
	private JFrame frame;
	private final GamePanel gamePanel;
	
	public RailChooserFrame(ArrayList<Railway> railways, GamePanel gamePanel, MapPanel mapPanel) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		frame = this;
		
		this.gamePanel = gamePanel;
		this.mapPanel = mapPanel;

		buttonArray = new ArrayList<JButton>();
		rail = railways;
		
		for(int i = 0; i < railways.size(); i++) {
			buttonArray.add(new JButton(i+1+". "+railways.get(i).source.name+" - "+railways.get(i).destination.name+" ("+railways.get(i).color+")"));
		}
		
		setLayout(new GridLayout(railways.size(), 1));
		
		for(int i = 0; i < railways.size(); i++) {
			buttonArray.get(i).addActionListener(new addRail());
			add(buttonArray.get(i));
		}
		
		pack();
		setVisible(true);
	}
	
	private class addRail implements ActionListener {
		@Override public void actionPerformed(ActionEvent arg0) {
			String s = ((JButton)(arg0.getSource())).getText().substring(0, 1);
			int number = Integer.parseInt(s);
			gamePanel.registerDecision(new ClaimRailwayDecision(rail.get(number-1), new CardBag()));
			
			Railway.railways.remove(rail.get(number-1));
			rail.remove(number-1);
			
			mapPanel.repaint();
			frame.setVisible(false);
			frame.dispose();
		}
		
	}
	
}
