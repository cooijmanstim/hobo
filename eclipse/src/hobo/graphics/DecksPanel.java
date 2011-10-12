package hobo.graphics;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JPanel;

public class DecksPanel extends JPanel {
	private ArrayList<TrainCardPanel> cards;
	
	public DecksPanel() {
		cards = new ArrayList<TrainCardPanel>();
		cards.add(new TrainCardPanel("Train_Black.png"));
		cards.add(new TrainCardPanel("Train_Blue.png"));
		cards.add(new TrainCardPanel("Train_Green.png"));
		cards.add(new TrainCardPanel("Train_Multicolor.png"));
		cards.add(new TrainCardPanel("Train_Red.png"));
		cards.add(new TrainCardPanel("Train_Empty.png"));

		setLayout(new GridLayout(cards.size(), 1));
		setPreferredSize(new Dimension(200,100*cards.size()));
		for(int i = 0; i < cards.size(); i++) {
			add(cards.get(i));
		}
	}
}