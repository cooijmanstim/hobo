package hobo.graphics;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class TrainCardPanel extends JPanel {
	public String str;
	
	public TrainCardPanel(String string) {
		str = "src/trains/"+string;
		setPreferredSize(new Dimension(200, 100));
		addMouseListener(new TakeCard());
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		Image img = getToolkit().getImage(str);
		g2.drawImage(img, 0, 0, this);
	}
	
	private class TakeCard implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			JOptionPane.showMessageDialog(null, "Perfom Action: Draw "+str+" Card");
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
