package hobo;

import java.util.Set;
import java.util.HashSet;

public class PlayerState implements Cloneable {
	// for assigning colors to players
	private static final Color[] colors = new Color[]{ Color.BLUE, Color.RED, Color.GREEN,
	                                                   Color.YELLOW, Color.BLACK };
	public static int next_color_index = 0;

	public final int handle;
	public final String name;
	public final Color color = colors[next_color_index++]; // will throw when out of colors
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
		PlayerState that = new PlayerState(handle, name);
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

	public PlayerState(int handle, String name) {
		this.handle = handle;
		this.name = name;
	}
   
	public int finalScore() {
		int score = this.score;
		for (Mission m: missions)
			score += m.value * (completed_missions.contains(m) ? 1 : -1);
		return score;
	}

	public void claim(Railway r) {
		ncars -= r.length;
		score += r.score();

		railways.add(r);

		detectMissionCompletion(r);
	}

	public void detectMissionCompletion(Railway r) {
		// if this railway completes a mission, add that mission to
		// completed_missions
		// NOTE: can complete multiple missions at once
		
		Set<City> explored = new HashSet<City>();
		explored.add(r.destination);
		explored.add(r.source);

		// all cities on the source side
		Set<City> cities1 = cities_connected_to(r.source,      explored);
		// all cities on the destination side
		Set<City> cities2 = cities_connected_to(r.destination, explored);

		// for each uncompleted mission, try to find a pair of cities, one in
		// cities1, the other in cities2, that match the mission.
		// (note that cities1 and cities2 are disjoint: a mission m that has
		// one city c1 in the intersection of what's reachable from r.source
		// and r.destination, and the other city c2 in the union, would already
		// have been connected by some other railway.)
		findMissions: for (Mission m: missions) {
			if (completed_missions.contains(m))
				continue;
			for (City c1: cities1) {
				if (!m.connects(c1))
					continue;
				for (City c2: cities2) {
					if (m.connects(c1, c2)) {
						completed_missions.add(m);
						continue findMissions;
					}
				}
			}
		}
	}

	// NOTE: explored is modified
	public Set<City> cities_connected_to(City c, Set<City> explored) {
		Set<City> cities = new HashSet<City>();
		for (Railway r: c.railways) {
			if (railways.contains(r)) {
				c = null;
				if (!explored.contains(r.source))      c = r.source;
				if (!explored.contains(r.destination)) c = r.destination;
				if (c != null) {
					explored.add(c);
					cities.add(c);
					cities.addAll(cities_connected_to(c, explored));
				}
			}
		}
		return cities;
	}
}
