package hobo;

import java.util.Set;

public class KeepMissionsDecision extends Decision {
	public final Set<Mission> missions;

	public KeepMissionsDecision(Set<Mission> missions) {
		this.missions = missions;
	}
	
	@Override public String toString() {
		return "KeepMissionsDecision(missions: "+missions+")";
	}
}
