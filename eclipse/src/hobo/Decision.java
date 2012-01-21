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
	public abstract AppliedDecision apply(State s, Object outcome_designator, boolean undoably);

	// decisions can have multiple outcomes; this method gives the caller a way to ask
	// for specific ones.  the caller can choose an outcome by passing the corresponding
	// outcome designator to apply.
	private static final Object[] justoneplskthx = { null }; 
	public Object[] outcomeDesignators(State s) {
		return justoneplskthx;
	}
	
	public double outcomeLikelihood(State s, Object outcome_designator) {
		return 1;
	}

	public AppliedDecision apply(State s, boolean undoably) {
		return apply(s, null, undoably);
	}
}
