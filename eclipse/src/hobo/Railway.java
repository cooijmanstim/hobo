package hobo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Railway {
	// NOTE: despite these being called source/destination, railways are not directed
	public final City source, destination;
	public final int length;
	public final Color color;
	public /* pretend final */ Railway dual;
	public String imagePath;

	public Railway(City source, City destination, int length, Color color, String picString){
		this.source = source;
		this.destination = destination;
		this.length = length;
		this.color = color;
		this.imagePath = picString;
		source.registerRailway(this);
		destination.registerRailway(this);
	}	

	public boolean connects(City c, City d) {
		return this.source == c && this.destination == d ||
		       this.source == d && this.destination == c;
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
	public int score() {
		return score_by_length[length];
	}

	public String toString() {
		return "Railway(source: "+source+", destination: "+destination+", color: "+color+")";
	}

	public static final ArrayList<Railway> railways = new ArrayList<Railway>();
	static {
		railways.add(new Railway(City.VANCOUVER,      City.CALGARY,            3, Color.GREY,	"1_Vancouver_Calgary_"));
		railways.add(new Railway(City.VANCOUVER,      City.SEATTLE,            1, Color.GREY,	"1_Vancouver_Seattle_"));
		railways.add(new Railway(City.VANCOUVER,      City.SEATTLE,            1, Color.GREY,	"2_Vancouver_Seattle_"));
		
		railways.add(new Railway(City.CALGARY,        City.WINNIPEG,           6, Color.WHITE,	"1_Calgary_Winnipeg_"));
		railways.add(new Railway(City.CALGARY,        City.SEATTLE,            4, Color.GREY,	"1_Seattle_Calgary_"));
		railways.add(new Railway(City.CALGARY,        City.HELENA,             4, Color.GREY,	"1_Calgary_Helena_"));
		
		railways.add(new Railway(City.WINNIPEG,       City.SAULT_ST_MARIE,     6, Color.GREY,	"1_Winnipeg_SaultStMarie_"));
		railways.add(new Railway(City.WINNIPEG,       City.HELENA,             4, Color.BLUE,	"1_Helena_Winnipeg_"));
		railways.add(new Railway(City.WINNIPEG,       City.DULUTH,             4, Color.BLACK,	"1_Winnipeg_Duluth_"));
		
		railways.add(new Railway(City.SAULT_ST_MARIE, City.MONTREAL,           5, Color.BLACK,	"1_SaultStMarie_Montreal_"));
		railways.add(new Railway(City.SAULT_ST_MARIE, City.DULUTH,             3, Color.GREY,	"1_Duluth_SaultStMarie_"));
		railways.add(new Railway(City.SAULT_ST_MARIE, City.TORONTO,            2, Color.GREY,	"1_SaultStMarie_Toronto_"));
		
		railways.add(new Railway(City.MONTREAL,       City.BOSTON,             2, Color.GREY,	"1_Montreal_Boston_"));
		railways.add(new Railway(City.MONTREAL,       City.BOSTON,             2, Color.GREY,	"2_Montreal_Boston_"));
		railways.add(new Railway(City.MONTREAL,       City.TORONTO,            3, Color.GREY,	"1_Toronto_Montreal_"));
		railways.add(new Railway(City.MONTREAL,       City.NEW_YORK,           3, Color.BLUE,	"1_Montreal_NewYork_"));
		
		railways.add(new Railway(City.SEATTLE,        City.PORTLAND,           1, Color.GREY,	"1_Portland_Seattle_"));
		railways.add(new Railway(City.SEATTLE,        City.PORTLAND,           1, Color.GREY,	"2_Portland_Seattle_"));
		railways.add(new Railway(City.SEATTLE,        City.HELENA,             6, Color.YELLOW,	"1_Seattle_Helena_"));
		
		railways.add(new Railway(City.HELENA,         City.SALT_LAKE_CITY,     3, Color.PINK,	"1_SaltLakeCity_Helena_"));
		railways.add(new Railway(City.HELENA,         City.DENVER,             4, Color.GREEN,	"1_Helena_Denver_"));
		railways.add(new Railway(City.HELENA,         City.OMAHA,              5, Color.RED,	"1_Helena_Omaha_"));
		
		railways.add(new Railway(City.DULUTH,         City.HELENA,             6, Color.BROWN,	"1_Helena_Duluth_"));
		railways.add(new Railway(City.DULUTH,         City.OMAHA,              2, Color.GREY,	"1_Omaha_Duluth_"));
		railways.add(new Railway(City.DULUTH,         City.OMAHA,              2, Color.GREY,	"2_Omaha_Duluth_"));
		railways.add(new Railway(City.DULUTH,         City.CHICAGO,            3, Color.RED,	"1_Duluth_Chicago_"));
		railways.add(new Railway(City.DULUTH,         City.TORONTO,            6, Color.PINK,	"1_Duluth_Toronto_"));
		
		railways.add(new Railway(City.TORONTO,        City.CHICAGO,            4, Color.WHITE,	"1_Chicago_Toronto_"));
		railways.add(new Railway(City.TORONTO,        City.PITTSBURGH,         2, Color.GREY,	"1_Toronto_Pittsburgh_"));
		
		railways.add(new Railway(City.NEW_YORK,       City.PITTSBURGH,         2, Color.WHITE,	"1_Pittsburgh_NewYork_"));
		railways.add(new Railway(City.NEW_YORK,       City.PITTSBURGH,         2, Color.GREEN,	"2_Pittsburgh_NewYork_"));
		railways.add(new Railway(City.NEW_YORK,       City.WASHINGTON,         2, Color.BROWN,	"1_NewYork_Washington_"));
		railways.add(new Railway(City.NEW_YORK,       City.WASHINGTON,         2, Color.BLACK,	"2_NewYork_Washington_"));
		railways.add(new Railway(City.NEW_YORK,       City.BOSTON,             2, Color.YELLOW,	"1_NewYork_Boston_"));
		railways.add(new Railway(City.NEW_YORK,       City.BOSTON,             2, Color.RED,	"2_NewYork_Boston_"));
		
		railways.add(new Railway(City.PORTLAND,       City.SAN_FRANCISCO,      5, Color.PINK,	"2_SanFrancisco_Portland_"));
		railways.add(new Railway(City.PORTLAND,       City.SAN_FRANCISCO,      5, Color.GREEN,	"1_SanFrancisco_Portland_"));
		railways.add(new Railway(City.PORTLAND,       City.SALT_LAKE_CITY,     6, Color.BLUE,	"1_Portland_SaltLakeCity_"));
		
		railways.add(new Railway(City.SALT_LAKE_CITY, City.SAN_FRANCISCO,      5, Color.BROWN,	"1_SanFrancisco_SaltLakeCity_"));
		railways.add(new Railway(City.SALT_LAKE_CITY, City.SAN_FRANCISCO,      5, Color.WHITE,	"2_SanFrancisco_SaltLakeCity_"));
		railways.add(new Railway(City.SALT_LAKE_CITY, City.LAS_VEGAS,          3, Color.BROWN,	"1_LasVegas_SaltLakeCity_"));
		railways.add(new Railway(City.SALT_LAKE_CITY, City.DENVER,             3, Color.YELLOW,	"2_SaltLakeCity_Denver_"));
		railways.add(new Railway(City.SALT_LAKE_CITY, City.DENVER,             3 ,Color.RED,	"1_SaltLakeCity_Denver_"));
										      
		railways.add(new Railway(City.DENVER,         City.PHOENIX,            5, Color.WHITE,	"1_Phoenix_Denver_"));
		railways.add(new Railway(City.DENVER,         City.SANTA_FE,           2, Color.GREY,	"1_SantaFe_Denver_"));
		railways.add(new Railway(City.DENVER,         City.OKLAHOMA_CITY,      3, Color.RED,	"1_Denver_OklahomaCity_"));
		railways.add(new Railway(City.DENVER,         City.KANSAS_CITY,        4, Color.BROWN,	"2_Denver_KansasCity_"));
		railways.add(new Railway(City.DENVER,         City.KANSAS_CITY,        4, Color.BLACK,	"1_Denver_KansasCity_"));
		railways.add(new Railway(City.DENVER,         City.OMAHA,              4, Color.PINK,	"1_Denver_Omaha_"));
		
		railways.add(new Railway(City.OMAHA,          City.KANSAS_CITY,        1, Color.GREY,	"1_Omaha_KansasCity_"));
		railways.add(new Railway(City.OMAHA,          City.KANSAS_CITY,        1, Color.GREY,	"2_Omaha_KansasCity_"));
		railways.add(new Railway(City.OMAHA,          City.CHICAGO,            4, Color.BLUE,	"1_Omaha_Chicago_"));
		
		railways.add(new Railway(City.CHICAGO,        City.SAINT_LOUIS,        2, Color.GREEN,	"1_StLouis_Chicago_"));
		railways.add(new Railway(City.CHICAGO,        City.SAINT_LOUIS,        2, Color.WHITE,	"2_StLouis_Chicago_"));
		railways.add(new Railway(City.CHICAGO,        City.PITTSBURGH,         3, Color.BLACK,	"2_Chicago_Pittsburgh_"));
		railways.add(new Railway(City.CHICAGO,        City.PITTSBURGH,         3, Color.BROWN,	"1_Chicago_Pittsburgh_"));
		
		railways.add(new Railway(City.PITTSBURGH,     City.SAINT_LOUIS,        5, Color.GREEN,	"1_StLouis_Pittsburgh_"));
		railways.add(new Railway(City.PITTSBURGH,     City.NASHVILLE,          4, Color.YELLOW,	"1_Nashville_Pittsburgh_"));
		railways.add(new Railway(City.PITTSBURGH,     City.RALEIGH,            2, Color.GREY,	"1_Pittsburgh_Raleigh_"));
		railways.add(new Railway(City.PITTSBURGH,     City.WASHINGTON,         2, Color.GREY,	"1_Pittsburgh_Washington_"));
		
		railways.add(new Railway(City.KANSAS_CITY,    City.OKLAHOMA_CITY,      2, Color.GREY,	"1_OklahomaCity_KansasCity_"));
		railways.add(new Railway(City.KANSAS_CITY,    City.OKLAHOMA_CITY,      2, Color.GREY,	"2_OklahomaCity_KansasCity_"));
		railways.add(new Railway(City.KANSAS_CITY,    City.SAINT_LOUIS,        2, Color.PINK,	"2_KansasCity_StLouis_"));
		railways.add(new Railway(City.KANSAS_CITY,    City.SAINT_LOUIS,        2, Color.BLUE,	"1_KansasCity_StLouis_"));
		
		railways.add(new Railway(City.SAINT_LOUIS,    City.LITTLE_ROCK,        2, Color.GREY,	"1_LittleRock_StLouis_"));
		railways.add(new Railway(City.SAINT_LOUIS,    City.NASHVILLE,          2, Color.GREY,	"1_StLouis_Nashville_"));
		
		railways.add(new Railway(City.SAN_FRANCISCO,  City.LOS_ANGELES,        3, Color.PINK,	"2_SanFrancisco_LosAngeles_"));
		railways.add(new Railway(City.SAN_FRANCISCO,  City.LOS_ANGELES,        3, Color.YELLOW,	"1_SanFrancisco_LosAngeles_"));
		
		railways.add(new Railway(City.LAS_VEGAS,      City.LOS_ANGELES,        2, Color.GREY,	"1_LosAngeles_LasVegas_"));
		
		railways.add(new Railway(City.SANTA_FE,       City.PHOENIX,            3, Color.GREY,	"1_Phoenix_SantaFe_"));
		railways.add(new Railway(City.SANTA_FE,       City.EL_PASO,            2, Color.GREY,	"1_ElPaso_SantaFe_"));
		railways.add(new Railway(City.SANTA_FE,       City.OKLAHOMA_CITY,      3, Color.BLUE,	"1_SantaFe_OklahomaCity_"));
		
		railways.add(new Railway(City.OKLAHOMA_CITY,  City.EL_PASO,            5, Color.YELLOW,	"1_ElPaso_OklahomaCity_"));
		railways.add(new Railway(City.OKLAHOMA_CITY,  City.DALLAS,             2, Color.GREY,	"1_OklahomaCity_Dallas_"));
		railways.add(new Railway(City.OKLAHOMA_CITY,  City.DALLAS,             2, Color.GREY,	"2_OklahomaCity_Dallas_"));
		railways.add(new Railway(City.OKLAHOMA_CITY,  City.LITTLE_ROCK,        2, Color.GREY,	"1_OklahomaCity_LittleRock_"));
		
		railways.add(new Railway(City.LITTLE_ROCK,    City.DALLAS,             2, Color.GREY,	"1_Dallas_LittleRock_"));
		railways.add(new Railway(City.LITTLE_ROCK,    City.NEW_ORLEANS,        3, Color.GREEN,	"1_LittleRock_NewOrleans_"));
		railways.add(new Railway(City.LITTLE_ROCK,    City.NASHVILLE,          3, Color.WHITE,	"1_LittleRock_Nashville_"));
		
		railways.add(new Railway(City.NASHVILLE,      City.ATLANTA,            1, Color.GREY,	"1_Nashville_Atlanta_"));
		railways.add(new Railway(City.NASHVILLE,      City.RALEIGH,            3, Color.BLACK,	"1_Nashville_Raleigh_"));
		
		railways.add(new Railway(City.RALEIGH,        City.ATLANTA,            2, Color.GREY,	"1_Atlanta_Raleigh_"));
		railways.add(new Railway(City.RALEIGH,        City.ATLANTA,            2, Color.GREY,	"2_Atlanta_Raleigh_"));
		railways.add(new Railway(City.RALEIGH,        City.CHARLESTON,         2, Color.GREY,	"1_Raleigh_Charleston_"));
		railways.add(new Railway(City.RALEIGH,        City.WASHINGTON,         2, Color.GREY,	"1_Raleigh_Washington_"));
		railways.add(new Railway(City.RALEIGH,        City.WASHINGTON,         2, Color.GREY,	"2_Raleigh_Washington_"));
		
		railways.add(new Railway(City.ATLANTA,        City.NEW_ORLEANS,        4, Color.BROWN,	"2_NewOrleans_Atlanta_"));
		railways.add(new Railway(City.ATLANTA,        City.NEW_ORLEANS,        4, Color.YELLOW,	"1_NewOrleans_Atlanta_"));
		railways.add(new Railway(City.ATLANTA,        City.MIAMI,              5, Color.BLUE,	"1_Atlanta_Miami_"));
		railways.add(new Railway(City.ATLANTA,        City.CHARLESTON,         2, Color.GREY,	"1_Atlanta_Charlston_"));
		
		railways.add(new Railway(City.CHARLESTON,     City.MIAMI,              4, Color.PINK,	"1_Charleston_Miami_"));
		railways.add(new Railway(City.LOS_ANGELES,    City.PHOENIX,            3, Color.GREY,	"1_LosAngeles_Phoenix_"));
		railways.add(new Railway(City.LOS_ANGELES,    City.EL_PASO,            6, Color.BLACK,	"1_LosAngeles_ElPaso_"));
		
		railways.add(new Railway(City.PHOENIX,        City.EL_PASO,            3, Color.GREY,	"1_Phoenix_ElPaso_"));
		railways.add(new Railway(City.EL_PASO,        City.DALLAS,             4, Color.RED,	"1_ElPaso_Dallas_"));
		railways.add(new Railway(City.EL_PASO,        City.HOUSTON,            6, Color.GREEN,	"1_ElPaso_Houston_"));
		
		railways.add(new Railway(City.DALLAS,         City.HOUSTON,            2, Color.GREY,	"1_Dallas_Houston_"));
		railways.add(new Railway(City.DALLAS,         City.HOUSTON,            2, Color.GREY,	"2_Dallas_Houston_"));
		railways.add(new Railway(City.HOUSTON,        City.NEW_ORLEANS,        2, Color.GREY,	"1_Houston_NewOrleans_"));
		railways.add(new Railway(City.NEW_ORLEANS,    City.MIAMI,              6, Color.RED,	"1_Miami_NewOrleans_"));

		// find and couple double railways
		for (Railway r: railways) {
			for (Railway s: railways) {
				if (s.connects(r.source, r.destination)) {
					r.dual = s;
					s.dual = r;
				}
			}
		}
	}	
}
