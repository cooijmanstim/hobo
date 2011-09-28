package hobo;

import java.util.*;

public class Main {
	public static void main(String[] args) {
		List<Player> ps = new ArrayList<Player>();
		UserInterface ui = new TextualUserInterface();
		ps.add(new HumanPlayer("x", ui));
		ps.add(new NegamaxPlayer("o"));
		Game g = new Game(ps);
		g.registerObserver(ui);
		g.play();
	}
}
