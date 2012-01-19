package hobo;

import java.util.Set;
import java.util.EnumSet;

public enum City {
	Vancouver    (110, 105),
	Calgary      (240,  87),
	Winnipeg     (465,  98),
	SaultStMarie (705, 147),
	Montreal     (897,  82),
	Seattle      (106, 159),
	Helena       (341, 219),
	Duluth       (578, 213),
	Toronto      (814, 170),
	Boston       (968, 140),
	Portland     ( 85, 210),
	NewYork      (916, 215),
	Omaha        (547, 306),
	Chicago      (699, 275),
	Pittsburgh   (830, 260),
	SaltLakeCity (269, 343),
	Denver       (399, 374),
	KansasCity   (567, 357),
	StLouis      (654, 358),
	Nashville    (748, 397),
	Raleigh      (864, 373),
	Washington   (923, 306),
	SanFrancisco ( 70, 407),
	LasVegas     (213, 453),
	SantaFe      (392, 465),
	OklahomaCity (548, 443),
	LittleRock   (639, 447),
	Atlanta      (799, 431),
	Charleston   (892, 439),
	LosAngeles   (147, 512),
	Phoenix      (268, 519),
	ElPaso       (386, 556),
	Dallas       (567, 531),
	Houston      (609, 571),
	NewOrleans   (702, 559),
	Miami        (925, 596);
	
	public static final City[] all = values();
	
	static {
		for (City city: all)
			city.cacheDistances();	
	}

	public final double x, y;
	public double[] distances;

	// initialized in registerRailway to avoid circularity
	public /* pretend final */ Set<Railway> railways = null;

	private City(double x, double y) {
		this.x = x;
		this.y = y;
	}

	// fuzzy match, not fast
	public static City designated_by(String name) {
		for (City c: values())
			if (c.name().toLowerCase().replaceAll(" ", "_").equals(name.toLowerCase().replaceAll(" ", "_")))
				return c;
		return null;
	}
	
	/**
	 * Initiates a list of euclidian distances from this city to all other cities
	 */
	private void cacheDistances(){
		City[] cities = City.values();
		distances = new double[cities.length];
		for (int i = 0; i < cities.length; i++)
			distances[i] = Math.sqrt(Math.pow(cities[i].x - this.x, 2)
			                       + Math.pow(cities[i].y - this.y, 2));
	}
	
	public void registerRailway(Railway r) {
		if (railways == null)
			railways = EnumSet.of(r);
		else
			railways.add(r);
	}

	// get a railway that connects this city to that one, possibly constrained by color
	public Railway railwayTo(City that, Color c) {
		for (Railway r: railways) {
			if (r.connects(this, that) && (c == null || r.color == c))
				return r;
		}
		return null;
	}

	public Railway railwayTo(City that) {
		return railwayTo(that, null);
	}
}
