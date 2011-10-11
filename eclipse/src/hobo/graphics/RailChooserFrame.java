package hobo.graphics;

import hobo.Railway;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;

public class RailChooserFrame extends JFrame {
	public static ArrayList<JButton> buttonArray;
	public static ArrayList<Railway> rail;
	private GamePanel panel;
	private JFrame f;
	
	public RailChooserFrame(ArrayList<Railway> railways, GamePanel panel) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		f = this;
		
		buttonArray = new ArrayList<JButton>();
		rail = railways;
		this.panel = panel;
		
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

		@Override
		public void actionPerformed(ActionEvent arg0) {
			String s = ((JButton)(arg0.getSource())).getText().substring(0, 1);
			int number = Integer.parseInt(s);
			GamePanel.railsways.add(new RailwayPanel(rail.get(number-1),1));
			
			Railway.railways.remove(rail.get(number-1));
			rail.remove(number-1);
			
			panel.repaint();
			f.setVisible(false);
			f.dispose();
		}
		
	}
	
}
