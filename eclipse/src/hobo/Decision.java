package hobo;

import java.util.*;

public abstract class Decision {
	public /* pretend final */ int player;

	public boolean isLegal(State s) {
		return reasonForIllegality(s) == null;
	}
	
	public void requireLegal(State s) {
		String r = reasonForIllegality(s);
		if (r != null)
			throw new IllegalDecisionException(r);
	}

	public abstract String reasonForIllegality(State s);
	public abstract void apply(State s);
}
