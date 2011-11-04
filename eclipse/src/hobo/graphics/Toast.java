package hobo.graphics;

import java.awt.Container;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class Toast extends JLabel {
	public Toast(String s) {
		super(s);
		
		setForeground(Color.white);
		setBackground(new Color(0.3f, 0.3f, 0.3f));
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setOpaque(true);
		
		final Component that = this;
		new Timer().schedule(new TimerTask() {
			public void run() {
				Container parent = getParent();
				parent.remove(that);
				parent.repaint();
			}
		}, 3000);
	}
}
