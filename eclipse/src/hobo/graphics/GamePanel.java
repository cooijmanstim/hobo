package hobo.graphics;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.JPanel;

public class GamePanel extends JPanel {
	private Image map;
	
	public GamePanel() {
		map = getToolkit().getImage("map.png");
		setPreferredSize(new Dimension(1324, 858));
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(map, 0, 0, this);
	}
}
