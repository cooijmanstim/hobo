package hobo;

import java.util.*;

public class RandomPlayer implements Player {
	private final String name;
	private final Random random = new Random();

	public RandomPlayer(String name) {
		this.name = name;
	}
	
	public String name() { return name; }
	public String toString() { return name; }

	public void perceive(Event e) {}
	public Decision decide(State s) {
		List<Decision> ds = allDecisions(s);
		return ds.get(random.nextInt(ds.size()));
	}

	public void illegal(State s, Decision d, String reason) {}
	public void loss   (State s) {}
	public void win    (State s) {}
	public void draw   (State s) {}

	public List<Decision> allDecisions(State s) {
		PlayerState ps = s.currentPlayerState();
		List<Decision> ds = new ArrayList<Decision>();

		if (ps.drawn_missions == null) {
			// NOTE: If there are multiple cards of any one color in the
			// open deck, one decision will be generated for each of them.
			// This is not a bug.  It ensures there are always equally many
			// DrawCardDecisions to consider.  Otherwise the probability
			// of choosing some other decision would be influenced by the
			// distribution of colors in the open deck, which is clearly
			// wrong.
			// NOTE: For the tree search AI, such duplicate decisions SHOULD
			// be eliminated.
			for (Color c: s.open_deck)
				ds.add(new DrawCardDecision(c));
			ds.add(new DrawCardDecision(null));

			if (ps.drawn_card == null) {
				ds.add(new DrawMissionsDecision());

				// claim
				for (Railway r: Railway.railways) {
					if (!s.isClaimed(r) && r.length <= ps.ncars) {
						for (Color c: Color.values()) {
							CardBag cs = ps.hand.cardsToClaim(r, c);
							if (cs != null)
								ds.add(new ClaimRailwayDecision(r, cs));
						}
					}
				}
			}
		} else {
			// keep
			for (int[] is: new int[][]{ { 0 }, { 1 }, { 2 },
			                            { 0, 1 }, { 1, 2 }, { 2, 0 },
			                            { 0, 1, 2 } }) {
				List<Mission> ms = new ArrayList<Mission>();
				for (int i: is)
					ms.add(ps.drawn_missions.get(i));
				ds.add(new KeepMissionsDecision(ms));
			}
		}

		return ds;
	}
}
