package hobo.graphics;

import hobo.Color;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class HandCardPanel extends JPanel {
	public final Color color;
	private final Image image;
	private int quantity;
	
	public HandCardPanel(Color color) {
		this.color = color;
		image = getToolkit().getImage(imagePath(color));
		setPreferredSize(new Dimension(200, 100));
	}

	private static String imagePath(Color color) {
		return "src/trains/Train_"+color+".png";
	}
	
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	public void markSelected() {
		setBorder(BorderFactory.createLineBorder(java.awt.Color.black));
	}
	
	public void markNotSelected() {
		setBorder(null);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(image, 0, 0, this);
		g2.drawString(quantity+"", 15, 15);
	}
}
