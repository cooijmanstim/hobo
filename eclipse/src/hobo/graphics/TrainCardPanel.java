package hobo.graphics;

import hobo.Color;
import hobo.DrawCardDecision;

import java.util.ArrayList;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class TrainCardPanel extends JPanel {
	private final Color color;
	private final GameVisualization visualization;
	private final Image image;

	public TrainCardPanel(final GameVisualization gv, final Color color) {
		this.color = color;
		this.visualization = gv;
		this.image = getToolkit().getImage(imagePath(color));
		setPreferredSize(new Dimension(200, 100));
		addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				gv.registerDecision(new DrawCardDecision(color));
			}
		});
	}
	
	public static String imagePath(Color c) {
		return "src/trains/Train_"+c+".png";
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(image, 0, 0, this);
	}
}
