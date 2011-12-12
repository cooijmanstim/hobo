package hobo;

public class HumanPlayer extends Player {
	private final PlayerInteraction ui;
	
	public HumanPlayer(String name, PlayerInteraction ui) {
		this.name = name;
		this.ui = ui;
	}
	
	public Decision decide(State s) { return ui.askDecision(this, s); }

	public void illegal(State s, Decision d, String reason) { ui.tellIllegal(this, s, d, reason); }
	public void loss   (State s)             { ui.tellLoss   (this, s); }
	public void win    (State s)             { ui.tellWin    (this, s); }
	public void draw   (State s)             { ui.tellDraw   (this, s); }
}
