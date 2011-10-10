package hobo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Mission {
	public final City source, destination;
	public final int value;
	public String str;

	public Mission(City source, City destination, int value, String str){
		this.source = source;
		this.destination = destination;
		this.value = value;
		this.str = str;
	}

	public boolean connects(City c) {
		return this.source == c || this.destination == c;
	}

	public boolean connects(City c, City d) {
		return this.source == c && this.destination == d ||
		       this.source == d && this.destination == c;
	}
	
	public static ArrayList<Mission> missions = new ArrayList<Mission>();
	static {
		missions.add(new Mission(City.LOS_ANGELES, City.NEW_YORK, 21, "LosAngeles_NewYork.png"));
		missions.add(new Mission(City.SEATTLE, City.NEW_YORK, 22, "Seattle_NewYork.png"));
		missions.add(new Mission(City.VANCOUVER, City.SANTA_FE, 13, "Vancouver_SantaFe.png"));
		missions.add(new Mission(City.LOS_ANGELES, City.MIAMI, 20, "LosAngeles_Miami.png"));
		missions.add(new Mission(City.LOS_ANGELES, City.CHIGACO, 16, "LosAngeles_Chicago.png"));
		missions.add(new Mission(City.MONTREAL, City.ATLANTA, 9, "Montreal_Atlanta.png"));
		missions.add(new Mission(City.DALLAS, City.NEW_YORK, 11, "Dallas_NewYork.png"));
		missions.add(new Mission(City.CALGARY, City.PHOENIX, 13, "Calgary_Phoenix.png"));
		missions.add(new Mission(City.DENVER, City.EL_PASO, 4, "Denver_ElPaso.png"));
		missions.add(new Mission(City.WINNIPEG, City.LITTLE_ROCK, 11, "Winnipeg_LittleRock.png"));
		missions.add(new Mission(City.NEW_YORK, City.ATLANTA, 6, "NewYork_Atlanta.png"));
		missions.add(new Mission(City.CHIGACO, City.SANTA_FE, 9, "Chicago_SantaFe.png"));
		missions.add(new Mission(City.DULUTH, City.EL_PASO, 10, "Duluth_ElPaso.png"));
		missions.add(new Mission(City.BOSTON, City.MIAMI, 12, "Boston_Miami.png"));
		missions.add(new Mission(City.CALGARY, City.SALT_LAKE_CITY, 7, "Calgary_SaltLakeCity.png"));
		missions.add(new Mission(City.PORTLAND, City.NASHVILLE, 17, "Portland_Nashville.png"));
		missions.add(new Mission(City.PORTLAND, City.PHOENIX, 11, "Portland_Phoenix.png"));
		missions.add(new Mission(City.DENVER, City.PITTSBURGH, 11, "Denver_Pittsburgh.png"));
		missions.add(new Mission(City.MONTREAL, City.NEW_ORLEANS, 13, "Montreal_NewOrleans.png"));
		missions.add(new Mission(City.VANCOUVER, City.MONTREAL, 20, "Vancouver_Montreal.png"));
		missions.add(new Mission(City.HELENA, City.LOS_ANGELES, 8, "Helena_LosAngeles.png"));
		missions.add(new Mission(City.SEATTLE, City.LOS_ANGELES, 9, "Seattle_LosAngeles.png"));
		missions.add(new Mission(City.SAULT_ST_MARIE, City.OKLAHOMA_CITY, 9, "SaultStMarie_Oklahoma.png"));
		missions.add(new Mission(City.SAULT_ST_MARIE, City.NASHVILLE, 8, "SaultStMarie_Nashville.png"));
		missions.add(new Mission(City.CHIGACO, City.NEW_ORLEANS, 7, "Chicago_NewOrleans.png"));
		missions.add(new Mission(City.TORONTO, City.MIAMI, 10, "Toronto_Miami.png"));
		missions.add(new Mission(City.SAN_FRANCISCO, City.ATLANTA, 17, "SanFrancisco_Atlanta.png"));
		missions.add(new Mission(City.WINNIPEG, City.HOUSTON, 12, "Winnipeg_Houston.png"));
		missions.add(new Mission(City.DULUTH, City.HOUSTON, 8, "Duluth_Houston.png"));
		missions.add(new Mission(City.KANSAS_CITY, City.HOUSTON, 5, "KansasCity_Houston.png"));	
	}
}
