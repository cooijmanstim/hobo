package hobo;

public class HumanPlayer implements Player {
	private final String name;
	private final PlayerInteraction ui;
	
	public HumanPlayer(String name, PlayerInteraction ui) {
		this.name = name;
		this.ui = ui;
	}
	
	public String name() { return name; }
	public String toString() { return name; }

	public void perceive(Event e) {}
	public Decision decide(State s) { return ui.askDecision(this); }

	public void illegal(State s, Decision d) { ui.tellIllegal(this); }
	public void loss   (State s)             { ui.tellLoss   (this); }
	public void win    (State s)             { ui.tellWin    (this); }
	public void draw   (State s)             { ui.tellDraw   (this); }
}
