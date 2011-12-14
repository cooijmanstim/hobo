package hobo;

import java.util.ArrayList;
import java.util.Set;
import java.util.List;
import java.util.HashSet;

public class PlayerState implements Cloneable {
	public static int next_color_index = 0;

	public final int handle;
	public final String name;
	public final Color color;
	public int ncars = 45, score = 0;
	public CardBag hand = new CardBag();
	public Set<Mission> missions = new HashSet<Mission>();
	public Set<Railway> railways = new HashSet<Railway>();
	public Set<Mission> completed_missions = new HashSet<Mission>();

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
		that.completed_missions.addAll(this.completed_missions);
		that.railways.addAll(this.railways);
		that.drawn_card = this.drawn_card;
		that.drawn_missions = this.drawn_missions;
		return that;
	}

	public PlayerState(int handle, String name, Color color) {
		this.color = color;
		this.handle = handle;
		this.name = name;
	}
	
	public boolean missionCompleted(Mission m) {
		return completed_missions.contains(m);
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
		completed_missions.addAll(missionsCompletedBy(r));
		railways.add(r);
	}

	/** Finds the currently incomplete missions that would be completed by
	 * acquiring the railway r.
	 * 
	 * For this to work correctly, r should not be contained by this.railways.
	 * The algorithm first builds two sets of cities, one containing all cities
	 * reachable from the source of r, and the other containing all cities
	 * reachable from the destination of r.  These two sets are disjoint.
	 *
	 * Whichever of these two sets is built last may be empty; this indicates
	 * that the cities connected by r were already connected, and hence this
	 * railway will not complete any missions.
	 * 
	 * Otherwise, the algorithm tries to find missions with one city in each
	 * of the two sets.  Missions with both cities in one set were already
	 * completed.  Missions with one city in neither set will not be completed.
	 */
	public Set<Mission> missionsCompletedBy(Railway r) {
		// cities already visited
		Set<City> explored = new HashSet<City>();

		// all cities on the source side
		Set<City> cities1 = citiesConnectedTo(r.source,      explored);
		// all cities on the destination side
		Set<City> cities2 = citiesConnectedTo(r.destination, explored);

		Set<Mission> newly_completed_missions = new HashSet<Mission>();
		if (cities2.isEmpty())
			return newly_completed_missions;

		findMissions: for (Mission m: missions) {
			if (missionCompleted(m))
				continue;
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
		Set<City> cities = new HashSet<City>();
		cities.add(c);
		explored.add(c);
		for (Railway r: c.railways) {
			if (railways.contains(r)) {
				c = null;
				if (!explored.contains(r.source))      c = r.source;
				if (!explored.contains(r.destination)) c = r.destination;
				if (c != null)
					cities.addAll(citiesConnectedTo(c, explored));
			}
		}
		return cities;
	}
	
	public double utility(State s) {
		// advantage over the other players combined
		if (s.gameOver()) return finalScore();
		double u = 0.0;
		for(Mission m : missions) {
			int length = 0;
			int LENGTH = 0;
			for(Railway r : Util.getShortestWay(m.source, m.destination, new ArrayList<Railway>(), s)) {
				LENGTH += r.length;
				if(railways.contains(r))
					length += r.length;
			}
			u += m.value*(2 * ( length * 1.0 / LENGTH * 1.0 ) - 1);
			
		}
		double e=0.0;
		if(hand.size() == 0)
			e = 1;
		else {
			for(int k:hand.multiplicities()) {
				if(k == 0)
					continue;
				double p = (k*1.0)/hand.size();
				e += (-p)*Util.log2(p);
			}			
		}
//		System.out.println("score="+score+" - u="+u+" - e="+e);
		return score + u + (1-e)*5;
	}
}
