package hobo;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class PlayerState implements Cloneable {
	public static int next_color_index = 0;

	public final int handle;
	public final String name;
	public final Color color;
	public int ncars = 45, score = 0;
	public CardBag hand = new CardBag();
	public Set<Mission> missions = EnumSet.noneOf(Mission.class);
	public Set<Railway> railways = EnumSet.noneOf(Railway.class);

	// when ncars drops below this at the end of a player's turn, the game
	// goes on for one last round.
	public static final int MIN_NCARS = 3;

	// choosing to draw a card or to draw missions are stateful actions that require
	// an additional decision.  these variables keep track of the state.
	public Color drawn_card = null;
	public Set<Mission> drawn_missions = null;

	public PlayerState clone() {
		PlayerState that = new PlayerState(handle, name, color);
		that.ncars = this.ncars;
		that.score = this.score;
		that.hand.addAll(this.hand);
		that.missions.addAll(this.missions);
		that.railways.addAll(this.railways);
		that.drawn_card = this.drawn_card;
		that.drawn_missions = this.drawn_missions;
		return that;
	}
	
	@Override public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PlayerState)) return false;
		PlayerState that = (PlayerState)o;
		if (this.ncars != that.ncars) return false;
		if (this.score != that.score) return false;
		if (!this.hand.equals(that.hand)) return false;
		if (!this.missions.equals(that.missions)) return false;
		if (!this.railways.equals(that.railways)) return false;
		if (this.drawn_card != that.drawn_card) return false;
		if (this.drawn_missions == null && that.drawn_missions != null) return false;
		if (this.drawn_missions != null && !this.drawn_missions.equals(that.drawn_missions)) return false;
		return true;
	}

	public PlayerState(int handle, String name, Color color) {
		this.color = color;
		this.handle = handle;
		this.name = name;
	}
	
	public boolean missionCompleted(Mission m) {
		return Util.shortestPath(m.source, m.destination, railways) != null;
	}
	
	public boolean almostOutOfCars() {
		return ncars < PlayerState.MIN_NCARS;
	}
   
	public int finalScore() {
		int score = this.score;
		for (Mission m: missions)
			score += m.value * (missionCompleted(m) ? 1 : -1);
		return score;
	}

	public void claim(Railway r) {
		ncars -= r.length;
		score += r.score();
		railways.add(r);
	}

	public void unclaim(Railway r) {
		railways.remove(r);
		score -= r.score();
		ncars += r.length;
	}

	public double utility(State s) {
		if (s.gameOver()) return finalScore();

		Set<Railway> usable_railways = s.usableRailwaysFor(handle);
		double u = 0.0;
		for (Mission m: missions) {
			int length = 0;
			int LENGTH = 0;
			for (Railway r: Util.shortestPath(m.source, m.destination, usable_railways)) {
				LENGTH += r.length;
				if (railways.contains(r))
					length += r.length;
			}
			u += m.value * (length * 2.0 / LENGTH - 1);
		}

		int mPoints = 0;
		if (missions.size() > 3) {
			mPoints += 10*(missions.size()-3);
		}

//		System.out.println("score="+score+" - u="+u+" - e="+e);
		return score + u + hand.utilityAsHand() - mPoints;
	}
}
