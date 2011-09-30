package hobo;

public abstract class Decision {
	public void illegalUnless(boolean condition) throws IllegalDecisionException {
		if (!condition)
			throw new IllegalDecisionException();
	}
}
