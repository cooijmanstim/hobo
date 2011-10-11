package hobo.graphics;

import hobo.Mission;
import hobo.Railway;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JPanel;

public class MissionPanel extends JPanel {
	public static ArrayList<Image> missionsImage;
	private JComboBox missionsCombo;
	private MissionPanel mp;
	
	public MissionPanel(ArrayList<Mission> missions) {
		missionsImage = new ArrayList<Image>();
		setLayout(new GridLayout(missions.size(), 1));
		String[] str = new String[missions.size()];
		for(int i = 0; i < missions.size(); i++) {
			missionsImage.add(getToolkit().getImage("src/missions/"+missions.get(i).str));
			str[i] = missions.get(i).source.name+" - "+missions.get(i).destination.name;
			
		}
		mp = this;
		missionsCombo = new JComboBox(str);
		missionsCombo.addActionListener(new DropDown());
		setPreferredSize(new Dimension(200, 150));
		add(missionsCombo);
		setLayout(new FlowLayout());
	}
	
	@Override
	public void paintComponent(Graphics arg0) {
		super.paintComponent(arg0);
		Graphics2D g2 = (Graphics2D) arg0;
		int i = missionsCombo.getSelectedIndex();
		g2.drawImage(missionsImage.get(i), 0, 50, this);
	}
	
	private class DropDown implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			mp.repaint();
			
		}
		
	}
}
