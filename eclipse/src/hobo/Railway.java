package hobo;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

public class Railway {
	public final City source, destination;
	public final int tracks;
	public final int color;
	/* color =
	 * 0=black
	 * 1=grey
	 * 2=white
	 * 3=yellow
	 * 4=green
	 * 5=red
	 * 6=blue
	 * 7=brown
	 * 8=pink
	**/
	public Railway(City source, City destination, int tracks, int color){
		this.source = source;
		this.destination = destination;
		this.tracks = tracks;
		this.color = color;
		init();
	}	
	
	public City source(){
		return source;
	}
	
	public City destination(){
		return destination;
	}
	
	public int tracks(){
		return tracks;
	}
	
	//remember to check if the tracks are double_sided before asking for the colors!!!
	// because i haven't build in an exception, there can be one sided tracks while a color2 is returned.
	public Color getColor(){
		switch(color){
		case 0:
			return Color.black;
		case 1:
			return Color.gray;
		case 2:
			return Color.white;
		case 3: 
			return Color.yellow;
		case 4:
			return Color.green;
		case 5:
			return Color.red;
		case 6:
			return Color.blue;
		case 7:
			return Color.orange;
		case 8:
			return Color.pink;
		default:
			return Color.gray;
		}
	}
	
	public static Set<Railway> railways = new HashSet<Railway>();
	public void init(){
		railways.add(new Railway(City.VANCOUVER, City.CALGARY, 3, 1));
		railways.add(new Railway(City.VANCOUVER, City.SEATTLE, 1, 1));
		railways.add(new Railway(City.VANCOUVER, City.SEATTLE, 1, 1));
		
		railways.add(new Railway(City.CALGARY, City.WINNIPEG, 6, 2));
		railways.add(new Railway(City.CALGARY, City.SEATTLE, 4, 1));
		railways.add(new Railway(City.CALGARY, City.HELENA, 4, 1));
		
		railways.add(new Railway(City.WINNIPEG, City.SAULT_ST_MARIE, 6, 1));
		railways.add(new Railway(City.WINNIPEG, City.HELENA, 4, 6));
		railways.add(new Railway(City.WINNIPEG, City.DULUTH, 4, 0));
		
		railways.add(new Railway(City.SAULT_ST_MARIE, City.MONTREAL, 5, 0));
		railways.add(new Railway(City.SAULT_ST_MARIE, City.DULUTH, 3, 1));
		railways.add(new Railway(City.SAULT_ST_MARIE, City.TORONTO, 2, 1));
		
		railways.add(new Railway(City.MONTREAL, City.BOSTON, 2, 1));
		railways.add(new Railway(City.MONTREAL, City.BOSTON, 2, 1));
		railways.add(new Railway(City.MONTREAL, City.TORONTO, 3, 1));
		railways.add(new Railway(City.MONTREAL, City.NEW_YORK, 3, 6));
		
		railways.add(new Railway(City.SEATTLE, City.PORTLAND, 1, 1));
		railways.add(new Railway(City.SEATTLE, City.PORTLAND, 1, 1));
		railways.add(new Railway(City.SEATTLE, City.HELENA, 6, 3));
		
		railways.add(new Railway(City.HELENA, City.SALT_LAKE_CITY, 3, 8));
		railways.add(new Railway(City.HELENA, City.DENVER, 4, 4));
		railways.add(new Railway(City.HELENA, City.OMAHA, 5, 5));
		
		railways.add(new Railway(City.DULUTH, City.HELENA, 6, 7));
		railways.add(new Railway(City.DULUTH, City.OMAHA, 2, 1));
		railways.add(new Railway(City.DULUTH, City.OMAHA, 2, 1));
		railways.add(new Railway(City.DULUTH, City.CHIGACO, 3, 5));
		railways.add(new Railway(City.DULUTH, City.TORONTO, 6, 8));
		
		railways.add(new Railway(City.TORONTO, City.CHIGACO, 4, 2));
		railways.add(new Railway(City.TORONTO, City.PITTSBURGH, 2, 1));
		
		railways.add(new Railway(City.NEW_YORK, City.PITTSBURGH, 2, 2));
		railways.add(new Railway(City.NEW_YORK, City.PITTSBURGH, 2, 4));
		railways.add(new Railway(City.NEW_YORK, City.WASHINGTON, 2, 7));
		railways.add(new Railway(City.NEW_YORK, City.WASHINGTON, 2, 0));
		railways.add(new Railway(City.NEW_YORK, City.BOSTON, 2, 3));
		railways.add(new Railway(City.NEW_YORK, City.BOSTON, 2, 5));
		
		railways.add(new Railway(City.PORTLAND, City.SAN_FRANCISCO, 5, 8));
		railways.add(new Railway(City.PORTLAND, City.SAN_FRANCISCO, 5, 4));
		railways.add(new Railway(City.PORTLAND, City.SALT_LAKE_CITY, 6, 6));
		
		railways.add(new Railway(City.SALT_LAKE_CITY, City.SAN_FRANCISCO, 5, 7));
		railways.add(new Railway(City.SALT_LAKE_CITY, City.SAN_FRANCISCO, 5, 2));
		railways.add(new Railway(City.SALT_LAKE_CITY, City.LAS_VEGAS, 3, 7));
		railways.add(new Railway(City.SALT_LAKE_CITY, City.DENVER, 3, 3));
		railways.add(new Railway(City.SALT_LAKE_CITY, City.DENVER, 3 ,5));
		
		railways.add(new Railway(City.DENVER, City.PHOENIX, 5, 2));
		railways.add(new Railway(City.DENVER, City.SANTA_FE, 2, 1));
		railways.add(new Railway(City.DENVER, City.OKLAHOMA_CITY, 3, 5));
		railways.add(new Railway(City.DENVER, City.KANSAS_CITY, 4, 7));
		railways.add(new Railway(City.DENVER, City.KANSAS_CITY, 4, 0));
		railways.add(new Railway(City.DENVER, City.OMAHA, 4, 8));
		
		railways.add(new Railway(City.OMAHA, City.KANSAS_CITY, 1, 1));
		railways.add(new Railway(City.OMAHA, City.KANSAS_CITY, 1, 1));
		railways.add(new Railway(City.OMAHA, City.CHIGACO, 4, 6));
		
		railways.add(new Railway(City.CHIGACO, City.SAINT_LOUIS, 2, 4));
		railways.add(new Railway(City.CHIGACO, City.SAINT_LOUIS, 2, 2));
		railways.add(new Railway(City.CHIGACO, City.PITTSBURGH, 3, 0));
		railways.add(new Railway(City.CHIGACO, City.PITTSBURGH, 3, 7));
		
		railways.add(new Railway(City.PITTSBURGH, City.SAINT_LOUIS, 5, 4));
		railways.add(new Railway(City.PITTSBURGH, City.NASHVILLE, 4, 3));
		railways.add(new Railway(City.PITTSBURGH, City.RALEIGH, 2, 1));
		railways.add(new Railway(City.PITTSBURGH, City.WASHINGTON, 2, 1));
		
		railways.add(new Railway(City.KANSAS_CITY, City.OKLAHOMA_CITY, 2, 1));
		railways.add(new Railway(City.KANSAS_CITY, City.OKLAHOMA_CITY, 2, 1));
		railways.add(new Railway(City.KANSAS_CITY, City.SAINT_LOUIS, 2, 8));
		railways.add(new Railway(City.KANSAS_CITY, City.SAINT_LOUIS, 2, 6));
		
		railways.add(new Railway(City.SAINT_LOUIS, City.LITTLE_ROCK, 2, 1));
		railways.add(new Railway(City.SAINT_LOUIS, City.NASHVILLE, 2, 1));
		
		railways.add(new Railway(City.SAN_FRANCISCO, City.LOS_ANGELES, 3, 8));
		railways.add(new Railway(City.SAN_FRANCISCO, City.LOS_ANGELES, 3, 3));
		
		railways.add(new Railway(City.LAS_VEGAS, City.LOS_ANGELES, 2, 1));
		
		railways.add(new Railway(City.SANTA_FE, City.PHOENIX, 3, 1));
		railways.add(new Railway(City.SANTA_FE, City.EL_PASO, 2, 1));
		railways.add(new Railway(City.SANTA_FE, City.OKLAHOMA_CITY, 3, 6));
		
		railways.add(new Railway(City.OKLAHOMA_CITY, City.EL_PASO, 5, 3));
		railways.add(new Railway(City.OKLAHOMA_CITY, City.DALLAS, 2, 1));
		railways.add(new Railway(City.OKLAHOMA_CITY, City.DALLAS, 2, 1));
		railways.add(new Railway(City.OKLAHOMA_CITY, City.LITTLE_ROCK, 2, 1));
		
		railways.add(new Railway(City.LITTLE_ROCK, City.DALLAS, 2, 1));
		railways.add(new Railway(City.LITTLE_ROCK, City.NEW_ORLEANS, 3, 4));
		railways.add(new Railway(City.LITTLE_ROCK, City.NASHVILLE, 3, 2));
		
		railways.add(new Railway(City.NASHVILLE, City.ATLANTA, 1, 1));
		railways.add(new Railway(City.NASHVILLE, City.RALEIGH, 2, 0));
		railways.add(new Railway(City.NASHVILLE, City.RALEIGH, 2, 0));
		
		railways.add(new Railway(City.RALEIGH, City.ATLANTA, 2, 1));
		railways.add(new Railway(City.RALEIGH, City.ATLANTA, 2, 1));
		railways.add(new Railway(City.RALEIGH, City.CHARLESTON, 2, 1));
		
		railways.add(new Railway(City.ATLANTA, City.NEW_ORLEANS, 4, 7));
		railways.add(new Railway(City.ATLANTA, City.NEW_ORLEANS, 4, 3));
		railways.add(new Railway(City.ATLANTA, City.MIAMI, 5, 6));
		
		railways.add(new Railway(City.CHARLESTON, City.MIAMI, 4, 8));
		railways.add(new Railway(City.LOS_ANGELES, City.PHOENIX, 3, 1));
		railways.add(new Railway(City.LOS_ANGELES, City.EL_PASO, 6, 0));
		
		railways.add(new Railway(City.PHOENIX, City.EL_PASO, 3, 1));
		railways.add(new Railway(City.EL_PASO, City.DALLAS, 4, 5));
		railways.add(new Railway(City.EL_PASO, City.HOUSTON, 6, 4));
		
		railways.add(new Railway(City.DALLAS, City.HOUSTON, 2, 1));
		railways.add(new Railway(City.HOUSTON, City.NEW_ORLEANS, 2, 1));
		railways.add(new Railway(City.NEW_ORLEANS, City.MIAMI, 6, 5));
	}	
	
}
