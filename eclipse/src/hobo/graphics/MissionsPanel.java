package hobo.graphics;

import hobo.Mission;
import hobo.Visualization;
import hobo.State;
import hobo.PlayerState;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class MissionsPanel extends JPanel implements Visualization {
	private final JList missionsList;
	private final GamePanel gamePanel;
	
	public MissionsPanel(final GamePanel gamePanel) {
		this.gamePanel = gamePanel;

		setPreferredSize(new Dimension(200, 100));
		setLayout(new BorderLayout());

		missionsList = new JList();
		add(new JScrollPane(missionsList), BorderLayout.CENTER);

		JButton missionsButton = new JButton("Draw Mission Cards");
		missionsButton.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				gamePanel.drawMissions();
			}
		});
		add(missionsButton, BorderLayout.SOUTH);

	}
	
	@Override public void reflect(State s) {
		PlayerState ps = s.currentPlayerState();
		String[] items = new String[ps.missions.size()];
		int i = 0;
		for (Mission m: ps.missions)
			items[i++] = m.value+" "+m.source+" - "+m.destination;
		missionsList.setListData(items);
		if (ps.drawn_missions != null && gamePanel.awaitingDecision())
			new MissionChooserFrame(ps.drawn_missions, gamePanel);
	}
}
