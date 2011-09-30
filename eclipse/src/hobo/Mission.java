package hobo;

import java.util.HashSet;
import java.util.Set;

public class Mission { //aka routekaart
	private final City source, destination;
	private final int value;
	
	public Mission(City source, City destination, int value){
		this.source = source;
		this.destination = destination;
		this.value = value;
	}
	
	public static Set<Mission> missions = new HashSet<Mission>();
	
	public void init(){
		missions.add(new Mission(City.LOS_ANGELES, City.NEW_YORK, 21));
		missions.add(new Mission(City.SEATTLE, City.NEW_YORK, 22));
		missions.add(new Mission(City.VANCOUVER, City.SANTA_FE, 13));
		missions.add(new Mission(City.LOS_ANGELES, City.MIAMI, 20));
		missions.add(new Mission(City.LOS_ANGELES, City.CHIGACO, 16));
		missions.add(new Mission(City.MONTREAL, City.ATLANTA, 9));
		missions.add(new Mission(City.DALLAS, City.NEW_YORK, 11));
		missions.add(new Mission(City.CALGARY, City.PHOENIX, 13));
		missions.add(new Mission(City.DENVER, City.EL_PASO, 4));
		missions.add(new Mission(City.WINNIPEG, City.LITTLE_ROCK, 11));
		missions.add(new Mission(City.NEW_YORK, City.ATLANTA, 6));
		missions.add(new Mission(City.CHIGACO, City.SANTA_FE, 9));
		missions.add(new Mission(City.DULUTH, City.EL_PASO, 10));
		missions.add(new Mission(City.BOSTON, City.MIAMI, 12));
		missions.add(new Mission(City.CALGARY, City.SALT_LAKE_CITY, 7));
		missions.add(new Mission(City.PORTLAND, City.NASHVILLE, 17));
		missions.add(new Mission(City.PORTLAND, City.PHOENIX, 11));
		missions.add(new Mission(City.DENVER, City.PITTSBURGH, 11));
		missions.add(new Mission(City.MONTREAL, City.NEW_ORLEANS, 13));
		missions.add(new Mission(City.VANCOUVER, City.MONTREAL, 20));
		missions.add(new Mission(City.HELENA, City.LOS_ANGELES, 8));
		missions.add(new Mission(City.SEATTLE, City.LOS_ANGELES, 9));
		missions.add(new Mission(City.SAULT_ST_MARIE, City.OKLAHOMA_CITY, 9));
		missions.add(new Mission(City.SAULT_ST_MARIE, City.NASHVILLE, 8));
		missions.add(new Mission(City.CHIGACO, City.NEW_ORLEANS, 7));
		missions.add(new Mission(City.TORONTO, City.MIAMI, 10));
		missions.add(new Mission(City.SAN_FRANCISCO, City.ATLANTA, 17));
		missions.add(new Mission(City.WINNIPEG, City.HOUSTON, 12));
		missions.add(new Mission(City.DULUTH, City.HOUSTON, 8));
		missions.add(new Mission(City.KANSAS_CITY, City.HOUSTON, 5));	
	}
}
