package hobo.graphics;

import hobo.Visualization;
import hobo.State;
import hobo.Color;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JPanel;

public class DecksPanel extends JPanel implements Visualization {
	private final GamePanel gamePanel;
	
	public DecksPanel(GamePanel gamePanel) {
		this.gamePanel = gamePanel;
		int size = State.OPEN_DECK_SIZE + 1;
		setLayout(new GridLayout(size, 1));
		setPreferredSize(new Dimension(200,100*size));
	}

	@Override public void reflect(State s) {
		// just remove all and construct new panels
		// (performance doesn't matter much in the GUI)
		removeAll();

		// open deck
		for (Color c: s.openCards())
			add(new TrainCardPanel(gamePanel, c));
		
		// closed deck
		add(new TrainCardPanel(gamePanel, null));
				
		revalidate(); // kill me now
	}
}
