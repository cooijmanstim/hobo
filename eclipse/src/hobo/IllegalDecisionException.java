package hobo;

public class IllegalDecisionException extends RuntimeException {
	public final String reason;
	public IllegalDecisionException(String reason) {
		this.reason = reason;
	}
	public String toString() {
		return super.toString()+": "+reason;
	}
}
