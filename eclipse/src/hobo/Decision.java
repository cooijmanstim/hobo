package hobo;

public abstract class Decision {
	public /* pretend final */ int player;
	
	public boolean isLegal(State s) {
		return reasonForIllegality(s) == null;
	}

	public boolean isLegalForPlayer(State s) {
		int oldplayer = s.currentPlayer();
		s.switchToPlayer(player);
		boolean isLegal = isLegal(s);
		s.switchToPlayer(oldplayer);
		return isLegal;
	}
	
	public void requireLegal(State s) {
		String r = reasonForIllegality(s);
		if (r != null)
			throw new IllegalDecisionException(r);
	}

	public abstract String reasonForIllegality(State s);
	public abstract AppliedDecision apply(State s);
}
