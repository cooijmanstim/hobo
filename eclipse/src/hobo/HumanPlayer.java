package hobo;

import java.util.Map;
import java.util.Set;

public class HumanPlayer extends Player {
	private PlayerInteraction ui;
	
	public HumanPlayer(String name) {
		this.name = name;
	}

	public HumanPlayer(String name, PlayerInteraction ui) {
		this.name = name;
		this.ui = ui;
	}

	public void setUI(PlayerInteraction ui) {
		this.ui = ui;
	}
	
	public static HumanPlayer fromConfiguration(String configuration) {
		String name = "human";

		for (Map.Entry<String,String> entry: Util.parseConfiguration(configuration).entrySet()) {
			String k = entry.getKey(), v = entry.getValue();
			if (k.equals("name")) name = v;
		}

		return new HumanPlayer(name);
	}

	public Decision decide(State s) {
		System.out.println("----------------------------------------------------");
		System.out.println(name+" deciding...");
		return ui.askDecision(this, s);
	}

	public void illegal(State s, Decision d, String reason) { ui.tellIllegal(this, s, d, reason); }
	public void loss   (State s)             { ui.tellLoss   (this, s); }
	public void win    (State s)             { ui.tellWin    (this, s); }
	public void draw   (State s)             { ui.tellDraw   (this, s); }

	// pfft
	@Override public void setDecisionTime(int t) {}
	@Override public void setVerbose(boolean v) {}
}
