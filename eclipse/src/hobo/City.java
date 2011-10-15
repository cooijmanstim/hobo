package hobo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class City {
	public final String name;
	public final double x, y;
	public final ArrayList<Railway> railways = new ArrayList<Railway>();

	public City(String name, double x, double y) {
		this.name = name;
		this.x = x;
		this.y = y;
	}

	public static City designated_by(String name) {
		for (City c: cities)
			if (c.name.toLowerCase().replaceAll(" ", "_").equals(name.toLowerCase().replaceAll(" ", "_")))
				return c;
		return null;
	}

	public void registerRailway(Railway r) {
		railways.add(r);
	}

	public String toString() {
		return name;
	}

	// get a railway that connects this city to that one, possible constrained by color
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

	// the locations are pulled from the map, 
	//so they are like ratio's to each other.
	//where x is distance from left, and y is distance from top.
	public static City VANCOUVER      = new City("Vancouver",       110, 105);
	public static City CALGARY        = new City("Calgary",         240, 87);
	public static City WINNIPEG       = new City("Winnipeg",        465, 98);
	public static City SAULT_ST_MARIE = new City("Sault St. Marie", 705, 147);
	public static City MONTREAL       = new City("Montreal",        897, 82);
	public static City SEATTLE        = new City("Seattle",         106, 159);
	public static City HELENA         = new City("Helena",          341, 219);
	public static City DULUTH         = new City("Duluth",          578, 213);
	public static City TORONTO        = new City("Toronto",         814, 170);
	public static City BOSTON         = new City("Boston",          968, 140);
	public static City PORTLAND       = new City("Portland",        85, 210);
	public static City NEW_YORK       = new City("New York",        916, 215);
	public static City OMAHA          = new City("Omaha",           547, 306);
	public static City CHICAGO        = new City("Chicago",         699, 275);
	public static City PITTSBURGH     = new City("Pittsburgh",      830, 260);
	public static City SALT_LAKE_CITY = new City("Salt Lake City",  269, 343);
	public static City DENVER         = new City("Denver",          399, 374);
	public static City KANSAS_CITY    = new City("Kansas City",     567, 357);
	public static City SAINT_LOUIS    = new City("Saint Louis",     654, 358);
	public static City NASHVILLE      = new City("Nashville",       748, 397);
	public static City RALEIGH        = new City("Raleigh",         864, 373);
	public static City WASHINGTON     = new City("Washington",      923, 306);
	public static City SAN_FRANCISCO  = new City("San Francisco",   70, 407);
	public static City LAS_VEGAS      = new City("Las Vegas",       213, 453);
	public static City SANTA_FE       = new City("Santa Fe",        392, 465);
	public static City OKLAHOMA_CITY  = new City("Oklahoma City",   548, 443);
	public static City LITTLE_ROCK    = new City("Little Rock",     639, 447);
	public static City ATLANTA        = new City("Atlanta",         799, 431);
	public static City CHARLESTON     = new City("Charleston",      892, 439);
	public static City LOS_ANGELES    = new City("Los Angeles",     147, 512);
	public static City PHOENIX        = new City("Phoenix",         268, 519);
	public static City EL_PASO        = new City("El Paso",         386, 556);
	public static City DALLAS         = new City("Dallas",          567, 531);
	public static City HOUSTON        = new City("Houston",         609, 571);
	public static City NEW_ORLEANS    = new City("New Orleans",     702, 559);
	public static City MIAMI          = new City("Miami",           925, 596);

	public static Set<City> cities = new HashSet<City>();
	static {
		cities.add(VANCOUVER);
		cities.add(CALGARY);
		cities.add(WINNIPEG);
		cities.add(SAULT_ST_MARIE);
		cities.add(MONTREAL);
		cities.add(SEATTLE);
		cities.add(HELENA);
		cities.add(DULUTH);
		cities.add(TORONTO);
		cities.add(BOSTON);
		cities.add(PORTLAND);
		cities.add(NEW_YORK);
		cities.add(OMAHA);
		cities.add(CHICAGO);
		cities.add(PITTSBURGH);
		cities.add(SALT_LAKE_CITY);
		cities.add(DENVER);
		cities.add(KANSAS_CITY);
		cities.add(SAINT_LOUIS);
		cities.add(NASHVILLE);
		cities.add(RALEIGH);
		cities.add(WASHINGTON);
		cities.add(SAN_FRANCISCO);
		cities.add(LAS_VEGAS);
		cities.add(SANTA_FE);
		cities.add(OKLAHOMA_CITY);
		cities.add(LITTLE_ROCK);
		cities.add(ATLANTA);
		cities.add(CHARLESTON);
		cities.add(LOS_ANGELES);
		cities.add(PHOENIX);
		cities.add(EL_PASO);
		cities.add(DALLAS);
		cities.add(HOUSTON);
		cities.add(NEW_ORLEANS);
		cities.add(MIAMI);
	}
	
	static {
		// make sure railways are initialized also
		Railway.railways.size();
	}
}
