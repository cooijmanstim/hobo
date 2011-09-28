package hobo;

public interface UserInterface extends GameObserver {
	public Decision askDecision(Player p);
	public void tellIllegal(Player p);
	public void tellLoss(Player p);
	public void tellDraw(Player p);
	public void tellWin(Player p);
}
