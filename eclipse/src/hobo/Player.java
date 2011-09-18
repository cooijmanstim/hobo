package hobo;

public interface Player {
	public String name();
	
	public void perceive(Event e);
	public Decision decide(State s);
	
	public void loss(State s);
	public void win(State s);
	public void draw(State s);
	public void illegal(State s, Decision d);
}
