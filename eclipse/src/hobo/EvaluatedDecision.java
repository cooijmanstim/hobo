package hobo;

public class EvaluatedDecision {
	public final Decision decision;
	public final double utility;
	public EvaluatedDecision(Decision d, double u) {
		decision = d; utility = u;
	}
	
	public String toString() {
		return "EvaluatedDecision(utility: "+utility+" decision: "+decision+")";
	}
}
