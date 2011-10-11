package hobo;

public interface PlayerInteraction extends GameObserver {
	public Decision askDecision(Player p, State s);
	public void tellIllegal(Player p, State s, Decision d, String reason);
	public void tellLoss(Player p, State s);
	public void tellDraw(Player p, State s);
	public void tellWin(Player p, State s);
}
