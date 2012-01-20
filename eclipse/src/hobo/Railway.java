package hobo;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

public enum Railway {
	Vancouver_Calgary_1(3, Color.GREY),
	Vancouver_Seattle_1(1, Color.GREY),
	Vancouver_Seattle_2(1, Color.GREY),
	Calgary_Winnipeg_1(6, Color.WHITE),
	Seattle_Calgary_1(4, Color.GREY),
	Calgary_Helena_1(4, Color.GREY),
	Winnipeg_SaultStMarie_1(6, Color.GREY),
	Helena_Winnipeg_1(4, Color.BLUE),
	Winnipeg_Duluth_1(4, Color.BLACK),
	SaultStMarie_Montreal_1(5, Color.BLACK),
	Duluth_SaultStMarie_1(3, Color.GREY),
	SaultStMarie_Toronto_1(2, Color.GREY),
	Montreal_Boston_1(2, Color.GREY),
	Montreal_Boston_2(2, Color.GREY),
	Toronto_Montreal_1(3, Color.GREY),
	Montreal_NewYork_1(3, Color.BLUE),
	Portland_Seattle_1(1, Color.GREY),
	Portland_Seattle_2(1, Color.GREY),
	Seattle_Helena_1(6, Color.YELLOW),
	SaltLakeCity_Helena_1(3, Color.PURPLE),
	Helena_Denver_1(4, Color.GREEN),
	Helena_Omaha_1(5, Color.RED),
	Helena_Duluth_1(6, Color.ORANGE),
	Omaha_Duluth_1(2, Color.GREY),
	Omaha_Duluth_2(2, Color.GREY),
	Duluth_Chicago_1(3, Color.RED),
	Duluth_Toronto_1(6, Color.PURPLE),
	Chicago_Toronto_1(4, Color.WHITE),
	Toronto_Pittsburgh_1(2, Color.GREY),
	Pittsburgh_NewYork_1(2, Color.WHITE),
	Pittsburgh_NewYork_2(2, Color.GREEN),
	NewYork_Washington_1(2, Color.ORANGE),
	NewYork_Washington_2(2, Color.BLACK),
	NewYork_Boston_1(2, Color.YELLOW),
	NewYork_Boston_2(2, Color.RED),
	SanFrancisco_Portland_2(5, Color.PURPLE),
	SanFrancisco_Portland_1(5, Color.GREEN),
	Portland_SaltLakeCity_1(6, Color.BLUE),
	SanFrancisco_SaltLakeCity_1(5, Color.ORANGE),
	SanFrancisco_SaltLakeCity_2(5, Color.WHITE),
	LasVegas_SaltLakeCity_1(3, Color.ORANGE),
	SaltLakeCity_Denver_2(3, Color.YELLOW),
	SaltLakeCity_Denver_1(3 ,Color.RED),
	Phoenix_Denver_1(5, Color.WHITE),
	SantaFe_Denver_1(2, Color.GREY),
	Denver_OklahomaCity_1(4, Color.RED),
	Denver_KansasCity_2(4, Color.ORANGE),
	Denver_KansasCity_1(4, Color.BLACK),
	Denver_Omaha_1(4, Color.PURPLE),
	Omaha_KansasCity_1(1, Color.GREY),
	Omaha_KansasCity_2(1, Color.GREY),
	Omaha_Chicago_1(4, Color.BLUE),
	StLouis_Chicago_1(2, Color.GREEN),
	StLouis_Chicago_2(2, Color.WHITE),
	Chicago_Pittsburgh_2(3, Color.BLACK),
	Chicago_Pittsburgh_1(3, Color.ORANGE),
	StLouis_Pittsburgh_1(5, Color.GREEN),
	Nashville_Pittsburgh_1(4, Color.YELLOW),
	Pittsburgh_Raleigh_1(2, Color.GREY),
	Pittsburgh_Washington_1(2, Color.GREY),
	OklahomaCity_KansasCity_1(2, Color.GREY),
	OklahomaCity_KansasCity_2(2, Color.GREY),
	KansasCity_StLouis_2(2, Color.PURPLE),
	KansasCity_StLouis_1(2, Color.BLUE),
	LittleRock_StLouis_1(2, Color.GREY),
	StLouis_Nashville_1(2, Color.GREY),
	SanFrancisco_LosAngeles_2(3, Color.PURPLE),
	SanFrancisco_LosAngeles_1(3, Color.YELLOW),
	LosAngeles_LasVegas_1(2, Color.GREY),
	Phoenix_SantaFe_1(3, Color.GREY),
	ElPaso_SantaFe_1(2, Color.GREY),
	SantaFe_OklahomaCity_1(3, Color.BLUE),
	ElPaso_OklahomaCity_1(5, Color.YELLOW),
	OklahomaCity_Dallas_1(2, Color.GREY),
	OklahomaCity_Dallas_2(2, Color.GREY),
	OklahomaCity_LittleRock_1(2, Color.GREY),
	Dallas_LittleRock_1(2, Color.GREY),
	LittleRock_NewOrleans_1(3, Color.GREEN),
	LittleRock_Nashville_1(3, Color.WHITE),
	Nashville_Atlanta_1(1, Color.GREY),
	Nashville_Raleigh_1(3, Color.BLACK),
	Atlanta_Raleigh_1(2, Color.GREY),
	Atlanta_Raleigh_2(2, Color.GREY),
	Raleigh_Charleston_1(2, Color.GREY),
	Raleigh_Washington_1(2, Color.GREY),
	Raleigh_Washington_2(2, Color.GREY),
	NewOrleans_Atlanta_2(4, Color.ORANGE),
	NewOrleans_Atlanta_1(4, Color.YELLOW),
	Atlanta_Miami_1(5, Color.BLUE),
	Atlanta_Charleston_1(2, Color.GREY),
	Charleston_Miami_1(4, Color.PURPLE),
	LosAngeles_Phoenix_1(3, Color.GREY),
	LosAngeles_ElPaso_1(6, Color.BLACK),
	Phoenix_ElPaso_1(3, Color.GREY),
	ElPaso_Dallas_1(4, Color.RED),
	ElPaso_Houston_1(6, Color.GREEN),
	Dallas_Houston_1(1, Color.GREY),
	Dallas_Houston_2(1, Color.GREY),
	Houston_NewOrleans_1(2, Color.GREY),
	Miami_NewOrleans_1(6, Color.RED);

	public static final Railway[] all = values();
	public static final Set<Railway> emptySet = EnumSet.noneOf(Railway.class);

	static {
		// find and couple double railways
		for (Railway r: values()) {
			for (Railway s: values()) {
				if (s != r && s.connects(r.source, r.destination)) {
					r.dual = s;
					s.dual = r;
				}
			}
		}
		
		// register railways with cities
		// (can only be done after all enum members are initialized)
		for (Railway r: values()) {
			r.source.registerRailway(r);
			r.destination.registerRailway(r);
		}
	}

	// NOTE: despite these being called source/destination, railways are not directed
	public final City source, destination;
	public final int length;
	public final Color color;
	public /* pretend final */ Railway dual;
	public final String imagePath;

	private Railway(int length, Color color) {
		this.length = length;
		this.color = color;

		// grab cities from name
		String[] parts = name().split("_");
		source = City.valueOf(parts[0]);
		destination = City.valueOf(parts[1]);

		imagePath = name() + "_";
	}

	public boolean connects(City c, City d) {
		return this.source == c && this.destination == d ||
		       this.source == d && this.destination == c;
	}
	
	public boolean connects(City c) {
		return this.source == c || this.destination == c;
	}
	
	public City otherCity(City c) {
		return (this.source      == c) ? this.destination
		     : (this.destination == c) ? this.source
		     : null;
	}
	
	// is the given collection of cards enough to claim this route?
	public boolean costs(CardBag cards) {
		if (cards.size() != length)
			return false;

		if (color == Color.GREY)
			return cards.allEquivalent();
		else
			return cards.countEquivalent(color) == length;
	}

	private static final int[] score_by_length = new int[]{ 0, 1, 2, 4, 7, 10, 15 };
	public static final int MAX_SCORE = score_by_length[score_by_length.length - 1];
	public int score() {
		return score_by_length[length];
	}

	public String toString() {
		return "Railway(source: "+source+", destination: "+destination+", color: "+color+")";
	}

	public double relevanceFor(Mission m) {
		double[] a = { m.source.x, m.source.y },
		         b = { m.destination.x, m.destination.y },
		         c = { this.source.x, this.source.y },
		         d = { this.destination.x, this.destination.y };
		       // don't let alongness count for too much -- add a constant
		return (3 + Util.segmentAlongness(a, b, c, d))
		       // punish if the railway strays too far
		       // (but sqrt for diminishing punishment over distance)
		     / (1 + Math.sqrt(Math.max(Util.distanceOfPointToSegment(c, a, b),
		                               Util.distanceOfPointToSegment(d, a, b))));
	}
	
	public static void main(String[] args) {
		// have a look at the relevance landscape for some mission
		Mission m = Mission.Seattle_LosAngeles;
		for (Railway r: all) {
			double[] a = { m.source.x, m.source.y },
			         b = { m.destination.x, m.destination.y },
			         c = { r.source.x, r.source.y },
			         d = { r.destination.x, r.destination.y };
			double alongness = Util.segmentAlongness(a, b, c, d);
			double distance = Math.max(Util.distanceOfPointToSegment(c, a, b),
			                           Util.distanceOfPointToSegment(d, a, b));
			System.out.println(r.relevanceFor(m)+"\t"+distance+"\t"+alongness+"\t"+r);
		}
	}
}
