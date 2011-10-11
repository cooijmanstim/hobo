package hobo.graphics;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;

import javax.swing.JPanel;

public class HandCardPanel extends JPanel {
	public String str;
	public int amount;
	
	public HandCardPanel(String string, int amount) {
		this.amount = amount;
		str = "src/trains/"+string;
		setPreferredSize(new Dimension(200, 100));
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
			Image img = getToolkit().getImage(str);
			g2.drawImage(img, 0, 0, this);
			if(amount != 1)
				g2.drawString(amount+"", 180, 20);
	}
}
