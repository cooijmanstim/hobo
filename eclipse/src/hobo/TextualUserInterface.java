package hobo;

import java.io.*;

public class TextualUserInterface implements UserInterface {
	public void observe(Event e) {
		System.out.println(e.state);
	}
	
	public Decision askDecision(Player p) {
		System.out.print(p+" to move: ");

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

	public void tellIllegal(Player p) { System.out.println("illegal move"); }
	public void tellLoss   (Player p) { System.out.println("you lose, "+p); }
	public void tellDraw   (Player p) { System.out.println("draw game"); }
	public void tellWin    (Player p) { System.out.println("you win, "+p); }
}
