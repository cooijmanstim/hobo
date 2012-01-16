package hobo;

import java.io.*;
import java.util.*;

public class TextualPlayerInteraction implements PlayerInteraction {
	public void observe(Event e) {
		if (e.player != null && e.decision != null)
			System.out.println("player: "+e.player.name()+" decision: "+e.decision);
	}
	
	public Decision askDecision(Player p, State s) {
		PlayerState ps = s.currentPlayerState();
		
		System.out.println("---");
		System.out.println("player: "+p.name());
		System.out.println("railways: "+ps.railways);
		System.out.println("missions: "+ps.missions);
		System.out.println("hand: "+ps.hand);
		System.out.println("open deck: "+s.openCards());

		if (ps.drawn_card != null)
			System.out.println("you just drew a "+ps.drawn_card+", you must choose from where to draw your second card");
		if (ps.drawn_missions != null)
			System.out.println("you just drew missions "+ps.drawn_missions+", you must decide which to keep");

		Decision d = null;
		do {
			System.out.print("? ");
			String[] words = get_words();
			d = interpret(words, ps.handle);
		} while (d == null);

		return d;
	}

	public Decision interpret(String[] words, int player) {
		/*
		 * claim <cards> <city1> <city2> [color]
		 * draw [color]
		 * draw_missions
		 * keep_missions <<city1> <city2>> <<city1> <city2>>
		 */
		if (words[0].equals("claim")) {
			CardBag cards = cards_designated_by(words[1]);
			if (cards == null) {
				System.out.println("invalid cards designator: "+words[1]);
				return null;
			}

			City a = City.designated_by(words[2]);
			if (a == null) {
				System.out.println("unknown city: "+words[2]);
				return null;
			}

			City b = City.designated_by(words[3]);
			if (b == null) {
				System.out.println("unknown city: "+words[3]);
				return null;
			}

			Color c = null;
			if (words.length > 4) {
				c = Color.designated_by(words[4]);
				if (c == null) {
					System.out.println("invalid color: "+words[4]);
					return null;
				}
			}
			
			Railway r = a.railwayTo(b, c);
			if (r == null) {
				System.out.println("no such railway");
				return null;
			}
			
			return new ClaimRailwayDecision(player, r, cards);
		}

		if (words[0].equals("draw")) {
			Color c = null;
			if (words.length > 1) {
				c = Color.designated_by(words[1]);
				if (c == null) {
					System.out.println("invalid color: "+words[1]);
					return null;
				}
			}
			return new DrawCardDecision(player, c);
		}

		if (words[0].equals("draw_missions")) {
			return new DrawMissionsDecision(player);
		}

		if (words[0].equals("keep_missions")) {
			if (words.length % 2 == 0) {
				System.out.println("mission designators must be pairs of cities");
			} else {
				Set<Mission> missions = new LinkedHashSet<Mission>();
				for (int i = 1; i < words.length - 1; i += 2) {
					City a = City.designated_by(words[i]);
					if (a == null) {
						System.out.println("unknown city: "+words[i]);
						return null;
					}
					City b = City.designated_by(words[i+1]);
					if (b == null) {
						System.out.println("unknown city: "+words[i+1]);
						return null;
					}
					Mission m = Mission.connecting(a, b);
					if (m == null) {
						System.out.println("no mission connecting: "+a+" and "+b);
						return null;
					}
					missions.add(m);
				}
				return new KeepMissionsDecision(player, missions);
			}
		}

		System.out.println("invalid command");
		return null;
	}

	public void tellIllegal(Player p, State s, Decision d, String reason) { System.out.println("illegal move, "+reason); }
	public void tellLoss   (Player p, State s) { System.out.println("you lose, "+p); }
	public void tellDraw   (Player p, State s) { System.out.println("draw game"); }
	public void tellWin    (Player p, State s) { System.out.println("you win, "+p); }

	public String[] get_words() {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String line = null;
		try {
			line = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return line.trim().split("\\s+");
	}

	public CardBag cards_designated_by(String designator) {
		CardBag cards = new CardBag();
		for (char c: designator.toCharArray()) {
			Color d = null;
			switch (c) {
			case 'k': d = Color.BLACK;  break;
			case 'l': d = Color.GREY;   break;
			case 'w': d = Color.WHITE;  break;
			case 'y': d = Color.YELLOW; break;
			case 'g': d = Color.GREEN;  break;
			case 'r': d = Color.RED;    break;
			case 'b': d = Color.BLUE;   break;
			case 'o': d = Color.ORANGE;  break;
			case 'p': d = Color.PURPLE;   break;
			default: return null;
			}
			cards.add(d);
		}
		return cards;
	}
}
