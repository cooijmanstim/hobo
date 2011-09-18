package hobo;

import java.util.*;

public class Main {
	public static void main(String[] args) {
		List<Player> ps = new ArrayList<Player>();
		ps.add(new HumanCLIPlayer("x"));
		ps.add(new HumanCLIPlayer("o"));
		Game g = new Game(ps);
		g.play();
	}
}
