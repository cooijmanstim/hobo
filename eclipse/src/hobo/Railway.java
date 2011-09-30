package hobo;

import java.util.HashSet;
import java.util.Set;

public class Railway {
	// NOTE: despite these being called source/destination, railways are not directed
	public final City source, destination;
	public final int length;
	public final Color color;

	// TODO: for double railways, we need the individual railways to have
	// references to eachother, in order to be able to enforce some rules.

	public Railway(City source, City destination, int length, Color color){
		this.source = source;
		this.destination = destination;
		this.length = length;
		this.color = color;
	}	
	
	// is the given collection of cards enough to claim this route?
	public boolean costs(CardBag cards) {
		return cards.size() == length && cards.count(color) + cards.count(Color.GREY) == length;
	}

	private static final int[] score_by_length = new int[]{ 0, 1, 2, 4, 7, 10, 15 };
	public int score() {
		return score_by_length[length];
	}

	public static final Set<Railway> railways = new HashSet<Railway>();
	static {
		railways.add(new Railway(City.VANCOUVER,		City.CALGARY,			3, Color.GREY));
		railways.add(new Railway(City.VANCOUVER,		City.SEATTLE,			1, Color.GREY));
		railways.add(new Railway(City.VANCOUVER,		City.SEATTLE,			1, Color.GREY));
		
		railways.add(new Railway(City.CALGARY,			City.WINNIPEG,			6, Color.WHITE));
		railways.add(new Railway(City.CALGARY,			City.SEATTLE,			4, Color.GREY));
		railways.add(new Railway(City.CALGARY,			City.HELENA,			4, Color.GREY));
		
		railways.add(new Railway(City.WINNIPEG,			City.SAULT_ST_MARIE,	6, Color.GREY));
		railways.add(new Railway(City.WINNIPEG,			City.HELENA,			4, Color.BLUE));
		railways.add(new Railway(City.WINNIPEG,			City.DULUTH,			4, Color.BLACK));
		
		railways.add(new Railway(City.SAULT_ST_MARIE,	City.MONTREAL,			5, Color.BLACK));
		railways.add(new Railway(City.SAULT_ST_MARIE,	City.DULUTH,			3, Color.GREY));
		railways.add(new Railway(City.SAULT_ST_MARIE,	City.TORONTO,			2, Color.GREY));
		
		railways.add(new Railway(City.MONTREAL,			City.BOSTON,			2, Color.GREY));
		railways.add(new Railway(City.MONTREAL,			City.BOSTON,			2, Color.GREY));
		railways.add(new Railway(City.MONTREAL,			City.TORONTO,			3, Color.GREY));
		railways.add(new Railway(City.MONTREAL,			City.NEW_YORK,			3, Color.BLUE));
		
		railways.add(new Railway(City.SEATTLE,			City.PORTLAND,			1, Color.GREY));
		railways.add(new Railway(City.SEATTLE,			City.PORTLAND,			1, Color.GREY));
		railways.add(new Railway(City.SEATTLE,			City.HELENA,			6, Color.YELLOW));
		
		railways.add(new Railway(City.HELENA,			City.SALT_LAKE_CITY,	3, Color.PINK));
		railways.add(new Railway(City.HELENA,			City.DENVER,			4, Color.GREEN));
		railways.add(new Railway(City.HELENA,			City.OMAHA,				5, Color.RED));
		
		railways.add(new Railway(City.DULUTH,			City.HELENA,			6, Color.BROWN));
		railways.add(new Railway(City.DULUTH,			City.OMAHA,				2, Color.GREY));
		railways.add(new Railway(City.DULUTH,			City.OMAHA,				2, Color.GREY));
		railways.add(new Railway(City.DULUTH,			City.CHIGACO,			3, Color.RED));
		railways.add(new Railway(City.DULUTH,			City.TORONTO,			6, Color.PINK));
		
		railways.add(new Railway(City.TORONTO,			City.CHIGACO,			4, Color.WHITE));
		railways.add(new Railway(City.TORONTO,			City.PITTSBURGH,		2, Color.GREY));
		
		railways.add(new Railway(City.NEW_YORK,			City.PITTSBURGH,		2, Color.WHITE));
		railways.add(new Railway(City.NEW_YORK,			City.PITTSBURGH,		2, Color.GREEN));
		railways.add(new Railway(City.NEW_YORK,			City.WASHINGTON,		2, Color.BROWN));
		railways.add(new Railway(City.NEW_YORK,			City.WASHINGTON,		2, Color.BLACK));
		railways.add(new Railway(City.NEW_YORK,			City.BOSTON,			2, Color.YELLOW));
		railways.add(new Railway(City.NEW_YORK,			City.BOSTON,			2, Color.RED));
		
		railways.add(new Railway(City.PORTLAND,			City.SAN_FRANCISCO,		5, Color.PINK));
		railways.add(new Railway(City.PORTLAND,			City.SAN_FRANCISCO,		5, Color.GREEN));
		railways.add(new Railway(City.PORTLAND,			City.SALT_LAKE_CITY,	6, Color.BLUE));
		
		railways.add(new Railway(City.SALT_LAKE_CITY,	City.SAN_FRANCISCO,		5, Color.BROWN));
		railways.add(new Railway(City.SALT_LAKE_CITY,	City.SAN_FRANCISCO,		5, Color.WHITE));
		railways.add(new Railway(City.SALT_LAKE_CITY,	City.LAS_VEGAS,			3, Color.BROWN));
		railways.add(new Railway(City.SALT_LAKE_CITY,	City.DENVER,			3, Color.YELLOW));
		railways.add(new Railway(City.SALT_LAKE_CITY,	City.DENVER,			3 ,Color.RED));
		
		railways.add(new Railway(City.DENVER,			City.PHOENIX,			5, Color.WHITE));
		railways.add(new Railway(City.DENVER,			City.SANTA_FE,			2, Color.GREY));
		railways.add(new Railway(City.DENVER,			City.OKLAHOMA_CITY,		3, Color.RED));
		railways.add(new Railway(City.DENVER,			City.KANSAS_CITY,		4, Color.BROWN));
		railways.add(new Railway(City.DENVER,			City.KANSAS_CITY,		4, Color.BLACK));
		railways.add(new Railway(City.DENVER,			City.OMAHA,				4, Color.PINK));
		
		railways.add(new Railway(City.OMAHA,			City.KANSAS_CITY,		1, Color.GREY));
		railways.add(new Railway(City.OMAHA,			City.KANSAS_CITY,		1, Color.GREY));
		railways.add(new Railway(City.OMAHA,			City.CHIGACO,			4, Color.BLUE));
		
		railways.add(new Railway(City.CHIGACO,			City.SAINT_LOUIS,		2, Color.GREEN));
		railways.add(new Railway(City.CHIGACO,			City.SAINT_LOUIS,		2, Color.WHITE));
		railways.add(new Railway(City.CHIGACO,			City.PITTSBURGH,		3, Color.BLACK));
		railways.add(new Railway(City.CHIGACO,			City.PITTSBURGH,		3, Color.BROWN));
		
		railways.add(new Railway(City.PITTSBURGH,		City.SAINT_LOUIS,		5, Color.GREEN));
		railways.add(new Railway(City.PITTSBURGH,		City.NASHVILLE,			4, Color.YELLOW));
		railways.add(new Railway(City.PITTSBURGH,		City.RALEIGH,			2, Color.GREY));
		railways.add(new Railway(City.PITTSBURGH,		City.WASHINGTON,		2, Color.GREY));
		
		railways.add(new Railway(City.KANSAS_CITY,		City.OKLAHOMA_CITY,		2, Color.GREY));
		railways.add(new Railway(City.KANSAS_CITY,		City.OKLAHOMA_CITY,		2, Color.GREY));
		railways.add(new Railway(City.KANSAS_CITY,		City.SAINT_LOUIS,		2, Color.PINK));
		railways.add(new Railway(City.KANSAS_CITY,		City.SAINT_LOUIS,		2, Color.BLUE));
		
		railways.add(new Railway(City.SAINT_LOUIS,		City.LITTLE_ROCK,		2, Color.GREY));
		railways.add(new Railway(City.SAINT_LOUIS,		City.NASHVILLE,			2, Color.GREY));
		
		railways.add(new Railway(City.SAN_FRANCISCO,	City.LOS_ANGELES,		3, Color.PINK));
		railways.add(new Railway(City.SAN_FRANCISCO,	City.LOS_ANGELES,		3, Color.YELLOW));
		
		railways.add(new Railway(City.LAS_VEGAS,		City.LOS_ANGELES,		2, Color.GREY));
		
		railways.add(new Railway(City.SANTA_FE,			City.PHOENIX,			3, Color.GREY));
		railways.add(new Railway(City.SANTA_FE,			City.EL_PASO,			2, Color.GREY));
		railways.add(new Railway(City.SANTA_FE,			City.OKLAHOMA_CITY,		3, Color.BLUE));
		
		railways.add(new Railway(City.OKLAHOMA_CITY,	City.EL_PASO,			5, Color.YELLOW));
		railways.add(new Railway(City.OKLAHOMA_CITY,	City.DALLAS,			2, Color.GREY));
		railways.add(new Railway(City.OKLAHOMA_CITY,	City.DALLAS,			2, Color.GREY));
		railways.add(new Railway(City.OKLAHOMA_CITY,	City.LITTLE_ROCK,		2, Color.GREY));
		
		railways.add(new Railway(City.LITTLE_ROCK,		City.DALLAS,			2, Color.GREY));
		railways.add(new Railway(City.LITTLE_ROCK,		City.NEW_ORLEANS,		3, Color.GREEN));
		railways.add(new Railway(City.LITTLE_ROCK,		City.NASHVILLE,			3, Color.WHITE));
		
		railways.add(new Railway(City.NASHVILLE,		City.ATLANTA,			1, Color.GREY));
		railways.add(new Railway(City.NASHVILLE,		City.RALEIGH,			2, Color.BLACK));
		railways.add(new Railway(City.NASHVILLE,		City.RALEIGH,			2, Color.BLACK));
		
		railways.add(new Railway(City.RALEIGH,			City.ATLANTA,			2, Color.GREY));
		railways.add(new Railway(City.RALEIGH,			City.ATLANTA,			2, Color.GREY));
		railways.add(new Railway(City.RALEIGH,			City.CHARLESTON,		2, Color.GREY));
		
		railways.add(new Railway(City.ATLANTA,			City.NEW_ORLEANS,		4, Color.BROWN));
		railways.add(new Railway(City.ATLANTA,			City.NEW_ORLEANS,		4, Color.YELLOW));
		railways.add(new Railway(City.ATLANTA,			City.MIAMI,				5, Color.BLUE));
		
		railways.add(new Railway(City.CHARLESTON,		City.MIAMI,				4, Color.PINK));
		railways.add(new Railway(City.LOS_ANGELES,		City.PHOENIX,			3, Color.GREY));
		railways.add(new Railway(City.LOS_ANGELES,		City.EL_PASO,			6, Color.BLACK));
		
		railways.add(new Railway(City.PHOENIX,			City.EL_PASO,			3, Color.GREY));
		railways.add(new Railway(City.EL_PASO,			City.DALLAS,			4, Color.RED));
		railways.add(new Railway(City.EL_PASO,			City.HOUSTON,			6, Color.GREEN));
		
		railways.add(new Railway(City.DALLAS,			City.HOUSTON,			2, Color.GREY));
		railways.add(new Railway(City.HOUSTON,			City.NEW_ORLEANS,		2, Color.GREY));
		railways.add(new Railway(City.NEW_ORLEANS,		City.MIAMI,				6, Color.RED));
	}	
}
