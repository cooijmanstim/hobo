package hobo;

import java.util.ArrayList;
import java.util.Deque;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class PlayerState implements Cloneable {
	public static int next_color_index = 0;

	public static final int INITIAL_NCARS = 45;
	// when ncars drops below this at the end of a player's turn, the game
	// goes on for one last round.
	public static final int MIN_NCARS = 3;

	public final int handle;
	public final String name;
	public final Color color;
	public int ncars = INITIAL_NCARS, score = 0;
	public CardBag hand = new CardBag();
	public Set<Mission> missions = EnumSet.noneOf(Mission.class);	
	public Set<Railway> railways = EnumSet.noneOf(Railway.class);

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
		return Util.shortestPath(m.source, m.destination, railways, railways) != null;
	}
	
	public boolean almostOutOfCars() {
		return ncars < PlayerState.MIN_NCARS;
	}
   
	public int finalScore() {
		int score = this.score;
		for (Mission m: missions)
			score += (missionCompleted(m) ? 1 : -1) * m.value;
		return score;
	}

	public void claim(Railway r) {
		ncars -= r.length;
		score += r.score();

		railways.add(r);
	}

	public void unclaim(Railway r) {
		score -= r.score();
		ncars += r.length;

		railways.remove(r);
	}
	
	public void receiveMissions(Set<Mission> ms) {
		missions.addAll(ms);
	}

	public void unreceiveMissions(Set<Mission> ms) {
		missions.removeAll(ms);
	}
	
	/** Finds the currently incomplete missions that would be completed by
	* acquiring the railway r.
	*
	* For this to work correctly, r should not be contained by this.railways.
	* The algorithm first builds two sets of cities, one containing all cities
	* reachable from the source of r, and the other containing all cities
	* reachable from the destination of r. These two sets are disjoint.
	*
	* Whichever of these two sets is built last may be empty; this indicates
	* that the cities connected by r were already connected, and hence this
	* railway will not complete any missions.
	*
	* Otherwise, the algorithm tries to find missions with one city in each
	* of the two sets. Missions with both cities in one set were already
	* completed. Missions with one city in neither set will not be completed.
	*/
	public Set<Mission> missionsCompletedBy(Railway r, Set<Mission> missions) {
		// cities already visited
		Set<City> explored = EnumSet.noneOf(City.class);

		// all cities on the source side
		Set<City> cities1 = citiesConnectedTo(r.source, explored);
		// all cities on the destination side
		Set<City> cities2 = citiesConnectedTo(r.destination, explored);

		Set<Mission> newly_completed_missions = EnumSet.noneOf(Mission.class);
		if (cities2.isEmpty())
			return newly_completed_missions;

		findMissions: for (Mission m: missions) {
			for (City c1: cities1) {
				if (!m.connects(c1))
					continue;
				for (City c2: cities2) {
					if (m.connects(c1, c2)) {
						newly_completed_missions.add(m);
						continue findMissions;
					}
				}
			}
		}

		return newly_completed_missions;
	}

	// NOTE: explored is modified
	public Set<City> citiesConnectedTo(City c, Set<City> explored) {
		Set<City> cities = EnumSet.noneOf(City.class);
		cities.add(c);
		explored.add(c);
		for (Railway r: c.railways) {
			if (railways.contains(r)) {
				c = null;
				if (!explored.contains(r.source)) c = r.source;
				if (!explored.contains(r.destination)) c = r.destination;
				if (c != null)
					cities.addAll(citiesConnectedTo(c, explored));
			}
		}
		return cities;
	}

	public int getMissionPoints() {
		int i = 0;
		for(Mission m : missions)
			i += m.value;
		return i;
	}
}
