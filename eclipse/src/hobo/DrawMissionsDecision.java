package hobo;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class DrawMissionsDecision extends Decision {
	public DrawMissionsDecision(int player) {
		this.player = player;
	}
	
	@Override public String toString() {
		return "DrawMissionsDecision(player: "+player+")";
	}
	
	@Override public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof DrawMissionsDecision))
			return false;
		DrawMissionsDecision that = (DrawMissionsDecision)o;
		return that.player == this.player;
	}
	
	private static final int classHashCode = "DrawMissionsDecision".hashCode();
	@Override public int hashCode() {
		return player ^ classHashCode;
	}
	
	@Override public String reasonForIllegality(State s) {
		PlayerState p = s.playerState(player);
		if (s.currentPlayer() != player) return "it's not your turn";
		if (p.drawn_card != null) return "you drew a card and now must decide which other card to draw";
		if (p.drawn_missions != null) return "you drew mission cards and now must decide which to keep";
		if (s.missions.isEmpty()) return "no missions to draw";
		return null;
	}

	@Override public Object[] outcomeDesignators(State s) {
		// assume drawing 3 missions

		Mission[] ms = s.missions.toArray(new Mission[s.missions.size()]);
		int n = ms.length, m = Math.min(3, ms.length);
		Object[] mss = new Object[(int)Util.binomial_coefficient(m, n)];

		// I'm getting lazy
		int imss = 0;
		for (int i = 0; i < n; i++) {
			if (n == 1) {
				mss[imss++] = EnumSet.of(ms[i]);
			} else {
				for (int j = i + 1; j < n; j++) {
					if (n == 2) {
						mss[imss++] = EnumSet.of(ms[i], ms[j]);
					} else {
						for (int k = j + 1; k < n; k++) {
							mss[imss++] = EnumSet.of(ms[i], ms[j], ms[k]);
						}
					}
				}
			}
		}

		return mss;
	}
	
	@Override public double outcomeLikelihood(State s, Object mission_set) {
		@SuppressWarnings("unchecked")
		Set<Mission> ms = (Set<Mission>)mission_set;
		int n = s.missions.size();
		int k = ms.size();
		assert(0 < k && k <= 3 && k <= n);
		return 1.0 / Util.binomial_coefficient(1, k);
	}
	
	@Override public AppliedDecision apply(State s, Object forced_mission_set, boolean undoably) {
		AppliedDecision a = undoably ? new AppliedDecision(this, s) : null;

		s.switchToPlayer(player);
		PlayerState p = s.playerState(player);

		if (forced_mission_set == null) {
			p.drawn_missions = Util.remove_sample(s.missions, 3, s.random, EnumSet.noneOf(Mission.class));
		} else {
			@SuppressWarnings("unchecked")
			Set<Mission> ms = (Set<Mission>)forced_mission_set;
			s.missions.removeAll(ms);
			p.drawn_missions = ms;
		}
		
		if (undoably) a.drawn_missions = p.drawn_missions;
		return a;
	}

	public class AppliedDecision extends hobo.AppliedDecision {
		public Set<Mission> drawn_missions;
		
		public AppliedDecision(Decision d, State s) { super(d, s); }

		@Override public void undo() {
			PlayerState p = state.playerState(player);

			state.missions.addAll(p.drawn_missions);
			p.drawn_missions = null;

			super.undo();
		}
		
		@Override public String toString() {
			return "DrawMissionsDecision.AppliedDecision()";
		}
	}

	public static Set<Decision> availableTo(State s, PlayerState ps, Set<Decision> ds) {
		if (ps.drawn_card != null || ps.drawn_missions != null || s.missions.isEmpty())
			return ds;
		ds.add(new DrawMissionsDecision(ps.handle));
		return ds;
	}
}
