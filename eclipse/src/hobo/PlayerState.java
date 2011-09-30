package hobo;

import java.util.Set;
import java.util.HashSet;

public class PlayerState implements Cloneable {
	public final String name;
	public int ncars = 45, score = 0;
	public CardBag hand = new CardBag();
	public Set<Mission> missions = new HashSet<Mission>();
	public Set<Railway> railways = new HashSet<Railway>();

	// when ncars drops below this at the end of a player's turn, the game
	// goes one for one last round.
	public static final int MIN_NCARS = 3;

	// choosing to draw a card or to draw missions are stateful actions that require
	// an additional decision.  these variables keep track of the state.
	public Color drawn_card = null;
	public Set<Mission> drawn_missions = null;

	public PlayerState(String name) {
		this.name = name;
	}
   
	public PlayerState clone() {
		PlayerState that = new PlayerState(name);
		that.ncars = this.ncars;
		that.score = this.score;
		that.hand.addAll(this.hand);
		that.missions.addAll(this.missions);
		that.railways.addAll(this.railways);
		that.drawn_card = this.drawn_card;
		that.drawn_missions = this.drawn_missions;
	}
}
