package hobo;

public class DrawCardDecision extends Decision {
	public final Color color;

	// if color is not null, draw from open deck
	public DrawCardDecision(Color color) {
		this.color = color;
	}
}
