package hobo;

import java.util.HashSet;
import java.util.Set;

public class City {
	public final String name;
	public final double x, y;
	public final Set<Railway> railways = new HashSet<Railway>();

	public City(String name, double x, double y) {
		this.name = name;
		this.x = x;
		this.y = y;
	}

	public void registerRailway(Railway r) {
		railways.add(r);
	}

	// the locations are pulled from the map, 
	//so they are like ratio's to each other.
	//where x is distance from left, and y is distance from top.
	public static City VANCOUVER      = new City("Vancouver",       1.9, 1.7);
	public static City CALGARY        = new City("Calgary",         5.3, 1.3);
	public static City WINNIPEG       = new City("Winnipeg",        11.2, 1.5);
	public static City SAULT_ST_MARIE = new City("Sault st. Marie", 17.5, 3);
	public static City MONTREAL       = new City("Montreal",        22.6, 1.2);
	public static City SEATTLE        = new City("Seattle",         1.8, 3.2);
	public static City HELENA         = new City("Helena",          7.9, 4.6);
	public static City DULUTH         = new City("Duluth",          14.3, 4.4);
	public static City TORONTO        = new City("Toronto",         20.5, 4.5);
	public static City BOSTON         = new City("Boston",          24.6, 2.7);
	public static City PORTLAND       = new City("Portland",        1.2, 4.6);
	public static City NEW_YORK       = new City("New York",        23, 5.7);
	public static City OMAHA          = new City("Omaha",           13.5, 8);
	public static City CHIGACO        = new City("Chigaco",         17.2, 19.2);
	public static City PITTSBURGH     = new City("Pittsburgh",      21, 19);
	public static City SALT_LAKE_CITY = new City("Salt Lake City",  6.2, 9.1);
	public static City DENVER         = new City("Denver",          8.7, 9.9);
	public static City KANSAS_CITY    = new City("Kansas City",     14, 9.5);
	public static City SAINT_LOUIS    = new City("Saint Louis",     16.4, 9.4);
	public static City NASHVILLE      = new City("Nashville",       18.7, 10.6);
	public static City RALEIGH        = new City("Raleigh",         22, 9.9);
	public static City WASHINGTON     = new City("Washington",      23.3, 8);
	public static City SAN_FRANCISCO  = new City("San Francisco",   1, 10.8);
	public static City LAS_VEGAS      = new City("Las Vegas",       4.8, 12);
	public static City SANTA_FE       = new City("Santa Fe",        8.4, 12.3);
	public static City OKLAHOMA_CITY  = new City("Oklahama City",   13.7, 11.5);
	public static City LITTLE_ROCK    = new City("Little Rock",     16.1, 11.6);
	public static City ATLANTA        = new City("Atlanta",         20.3, 11.3);
	public static City CHARLESTON     = new City("Charleston",      22.1, 11.5);
	public static City LOS_ANGELES    = new City("Los Angeles",     2.9, 13.4);
	public static City PHOENIX        = new City("Phoenix",         6.3, 13.5);
	public static City EL_PASO        = new City("El Paso",         9.2, 14.6);
	public static City DALLAS         = new City("Dallas",          14, 13.7);
	public static City HOUSTON        = new City("Houston",         15.1, 15);
	public static City NEW_ORLEANS    = new City("New Orleans",     16.7, 14.9);
	public static City MIAMI          = new City("Miami",           23.2, 15.9);

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
		cities.add(CHIGACO);
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
}
