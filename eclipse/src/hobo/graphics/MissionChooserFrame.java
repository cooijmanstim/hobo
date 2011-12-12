package hobo.graphics;

import hobo.KeepMissionsDecision;
import hobo.Mission;
import hobo.ClaimRailwayDecision;
import hobo.CardBag;
import hobo.Color;
import hobo.PlayerState;
import hobo.City;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;

public class MissionChooserFrame extends JFrame {
	public MissionChooserFrame(Collection<Mission> missions, final GamePanel gamePanel) {
		final JList list = new JList(missions.toArray());
		add(list);
		JButton button = new JButton("Confirm");
		button.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent e) {
				Set<Mission> ms = new HashSet<Mission>();
				Object[] os = list.getSelectedValues();
				for (int i = 0; i < os.length; i++) {
					ms.add((Mission)os[i]);
				}
				gamePanel.registerDecision(new KeepMissionsDecision(ms));
				
				setVisible(false);
				dispose();
			}
		});
		add(button);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new FlowLayout());
		pack();
		setVisible(true);
	}
}
