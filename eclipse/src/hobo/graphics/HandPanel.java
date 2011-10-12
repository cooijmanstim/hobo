package hobo.graphics;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JPanel;

public class HandPanel extends JPanel {
	private ArrayList<HandCardPanel> hands;

	public HandPanel() {
		hands = new ArrayList<HandCardPanel>();
		hands.add(new HandCardPanel("Train_Black.png", 3));
		hands.add(new HandCardPanel("Train_Blue.png", 5));
		hands.add(new HandCardPanel("Train_Multicolor.png", 2));
		hands.add(new HandCardPanel("Train_Yellow.png", 1));
		
		setLayout(new GridLayout(1, hands.size()));
		setPreferredSize(new Dimension(200*hands.size(), 100));
		for(int i = 0; i < hands.size(); i++) {
			add(hands.get(i));
		}
	}
}
