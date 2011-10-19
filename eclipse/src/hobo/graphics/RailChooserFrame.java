package hobo.graphics;

import hobo.Railway;
import hobo.ClaimRailwayDecision;
import hobo.CardBag;
import hobo.Color;
import hobo.PlayerState;

import java.util.Collection;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;

public class RailChooserFrame extends JFrame {
	public RailChooserFrame(Collection<Railway> railways, final GamePanel gamePanel, final MapPanel mapPanel) {
		for (final Railway r: railways) {
			JButton b = new JButton(r.toString());
			b.addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent e) {
					gamePanel.claim(r);
					setVisible(false);
					dispose();
				}
			});
			add(b);
		}
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);		
		setLayout(new GridLayout(railways.size(), 1));		
		pack();
		setVisible(true);
	}
}
