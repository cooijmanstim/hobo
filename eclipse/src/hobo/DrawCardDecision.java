package hobo;

public class DrawCardDecision extends Decision {
	// if color is not null, draw from open deck
	public final Color color;
	public DrawCardDecision()            { this.color = null; }
	public DrawCardDecision(Color color) { this.color = color; }
		
	@Override public String toString() {
		return "DrawCardDecision(color: "+color+")";
	}
}
