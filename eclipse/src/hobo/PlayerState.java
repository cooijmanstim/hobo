package hobo;

public class PlayerState implements Cloneable {
	public final String name;
	
	public PlayerState(String name) {
		this.name = name;
	}
	
	public PlayerState clone() {
		return new PlayerState(name);
	}
}
