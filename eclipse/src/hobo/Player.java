package hobo;

public abstract class Player {
	protected String name;
	protected int handle = -1;
	
	public String name() { return name; }
	public String toString() { return name; }
	public void setHandle(int handle) { this.handle = handle; }
	
	public void perceive(Event e) {};
	public abstract Decision decide(State s);

	public void loss(State s) {}
	public void win(State s) {}
	public void draw(State s) {}
	public void illegal(State s, Decision d, String reason) {};
}
