package hobo;

import java.io.*;

public class HumanCLIPlayer implements Player {
	private final String name;
	
	public HumanCLIPlayer(String name) {
		this.name = name;
	}
	
	public String name() { return name; }
	public String toString() { return name; }

	public void perceive(Event e) {
		System.out.println(e.player+" takes ("+e.decision.x+", "+e.decision.y+")");
	}

	public Decision decide(State s) {
		System.out.println(s);
		System.out.print(this+" to move: ");
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String line = null;
		try {
			line = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		String[] words = line.trim().split("\\s+");
		int[] xy = new int[2];
		for (int i = 0; i < 2; i++)
			xy[i] = Integer.parseInt(words[i]);
		return new Decision(xy[0], xy[1]);
	}

	public void illegal(State s, Decision d) {
		System.out.println("illegal move");
	}

	public void loss(State s) {
		System.out.println(this+" loses");
	}

	public void win(State s) {
		System.out.println(this+" wins");
	}
	
	public void draw(State s) {
		System.out.println("draw game");
	}
}
