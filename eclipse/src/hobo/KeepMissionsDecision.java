package hobo;

import java.util.List;

public class KeepMissionsDecision extends Decision {
	public final List<Mission> missions;

	public KeepMissionsDecision(List<Mission> missions) {
		this.missions = missions;
	}
}
