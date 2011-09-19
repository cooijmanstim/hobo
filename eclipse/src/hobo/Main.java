package hobo;

import java.util.*;

public class Main {
	public static void main(String[] args) {
		List<Player> ps = new ArrayList<Player>();
		ps.add(new NegamaxPlayer("x"));
		ps.add(new NegamaxPlayer("o"));
		Game g = new Game(ps);
		g.registerObserver(new GameObserver() {
			public void observe(Event e) {
				System.out.println(e.state);
			}
		});
		g.play();
	}
}
