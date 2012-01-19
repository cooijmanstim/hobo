package hobo.graphics;

import java.awt.Color;
import java.awt.GridLayout;

import hobo.State;
import hobo.PlayerState;
import hobo.Visualization;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class PlayerPanel extends JPanel implements Visualization {
	public final int handle;
	public JLabel name = new JLabel(), points = new JLabel(), turn = new JLabel();
	
	public PlayerPanel(int handle) {
		this.handle = handle;

		setLayout(new GridLayout(3, 1));
		setBorder(new TitledBorder(new EtchedBorder(), "Player "+handle));
		add(name);
		add(points);
		add(turn);
	}

	@Override public void reflect(State s) {
		PlayerState ps = s.playerState(handle);

		// this really only needs to be done once, but who cares
		name.setText(ps.name);
		setBackground(ps.color.awtColor);
		
		points.setText(ps.score+" points, "+ps.ncars+" cars");
		turn.setText(s.currentPlayerState() == ps ? "Currently his/her move" : "");
	}
}
