package hobo;

public class Railway {
	public final City source, destination;
	public final int tracks;
	public final boolean double_sided;
	public final int color1, color2;
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
	public Railway(City source, City destination, int tracks, boolean double_sided, int color1){
		this.source = source;
		this.destination = destination;
		this.tracks = tracks;
		this.double_sided = double_sided;
		this.color1 = color1;
		color2 = -1;
	}
	
	public Railway(City source, City destination, int tracks, boolean double_sided, int color1, int color2){
		this.source = source;
		this.destination = destination;
		this.tracks = tracks;
		this.double_sided = double_sided;
		this.color1 = color1;
		this.color2 = color2;
	}
	
	public static Railway VC = new Railway(City.VANCOUVER, City.CALGARY, 3, false, 1);
	public static Railway VS = new Railway(City.VANCOUVER, City.SEATTLE, 1, true, 1);
	
	public static Railway CW = new Railway(City.CALGARY, City.WINNIPEG, 6, false, 2);
	public static Railway CS = new Railway(City.CALGARY, City.SEATTLE, 4, false, 1);
	public static Railway CH = new Railway(City.CALGARY, City.HELENA, 4, false, 1);
	
	public static Railway WS = new Railway(City.WINNIPEG, City.SAULT_ST_MARIE, 6, false, 1);
	public static Railway WH = new Railway(City.WINNIPEG, City.HELENA, 4, false, 6);
	public static Railway WD = new Railway(City.WINNIPEG, City.DULUTH, 4, false, 0);
	
	public static Railway SM = new Railway(City.SAULT_ST_MARIE, City.MONTREAL, 5, false, 0);
	public static Railway SD = new Railway(City.SAULT_ST_MARIE, City.DULUTH, 3, false, 1);
	public static Railway ST = new Railway(City.SAULT_ST_MARIE, City.TORONTO, 2, false, 1);
	
	public static Railway MB = new Railway(City.MONTREAL, City.BOSTON, 2, true, 1);
	public static Railway MT = new Railway(City.MONTREAL, City.TORONTO, 3, false, 1);
	public static Railway MN = new Railway(City.MONTREAL, City.NEW_YORK, 3, false, 6);
	
	public static Railway SP = new Railway(City.SEATTLE, City.PORTLAND, 1, true, 1);
	public static Railway SH = new Railway(City.SEATTLE, City.HELENA, 6, false, 3);
	
	public static Railway HS = new Railway(City.HELENA, City.SALT_LAKE_CITY, 3, false, 8);
	public static Railway HD = new Railway(City.HELENA, City.DENVER, 4, false, 4);
	public static Railway HO = new Railway(City.HELENA, City.OMAHA, 5, false, 5);
	
	public static Railway DH = new Railway(City.DULUTH, City.HELENA, 6, false, 7);
	public static Railway DO = new Railway(City.DULUTH, City.OMAHA, 2, true, 1);
	public static Railway DC = new Railway(City.DULUTH, City.CHIGACO, 3, false, 5);
	public static Railway DT = new Railway(City.DULUTH, City.TORONTO, 6, false, 8);
	
	public static Railway TC = new Railway(City.TORONTO, City.CHIGACO, 4, false, 2);
	public static Railway TP = new Railway(City.TORONTO, City.PITTSBURGH, 2, false, 1);
	
	public static Railway NP = new Railway(City.NEW_YORK, City.PITTSBURGH, 2, true, 2, 4);
	public static Railway NW = new Railway(City.NEW_YORK, City.WASHINGTON, 2, true, 7, 0);
	public static Railway NB = new Railway(City.NEW_YORK, City.BOSTON, 2, true, 3, 5);
	
	public static Railway PS = new Railway(City.PORTLAND, City.SAN_FRANCISCO, 5, true, 4, 8);
	public static Railway PSAL = new Railway(City.PORTLAND, City.SALT_LAKE_CITY, 6, false, 6);
	
	public static Railway SS = new Railway(City.SALT_LAKE_CITY, City.SAN_FRANCISCO, 5, true, 7, 2);
	public static Railway SL = new Railway(City.SALT_LAKE_CITY, City.LAS_VEGAS, 3, false, 7);
	public static Railway SALD = new Railway(City.SALT_LAKE_CITY, City.DENVER, 3, true, 5, 3);
	
	public static Railway DP = new Railway(City.DENVER, City.PHOENIX, 5, false, 2);
	public static Railway DS = new Railway(City.DENVER, City.SANTA_FE, 2, false, 1);
	public static Railway DEO = new Railway(City.DENVER, City.OKLAHAMA_CITY, 3, false, 5);
	public static Railway DK = new Railway(City.DENVER, City.KANSAS_CITY, 4, true, 0, 7);
	public static Railway DEOM = new Railway(City.DENVER, City.OMAHA, 4, false, 8);
	
	public static Railway OK = new Railway(City.OMAHA, City.KANSAS_CITY, 1, true, 1);
	public static Railway OC = new Railway(City.OMAHA, City.CHIGACO, 4, false, 6);
	
	public static Railway CHS = new Railway(City.CHIGACO, City.SAINT_LOUIS, 2, true, 4, 2);
	public static Railway CP = new Railway(City.CHIGACO, City.PITTSBURGH, 3, true, 7, 0);
	
	public static Railway PIS = new Railway(City.PITTSBURGH, City.SAINT_LOUIS, 5, false, 4);
	public static Railway PN = new Railway(City.PITTSBURGH, City.NASHVILLE, 4, false, 3);
	public static Railway PR = new Railway(City.PITTSBURGH, City.RALEIGH, 2, false, 1);
	public static Railway PW = new Railway(City.PITTSBURGH, City.WASHINGTON, 2, false, 1);
	
	public static Railway KO = new Railway(City.KANSAS_CITY, City.OKLAHAMA_CITY, 2, true, 1);
	public static Railway KS = new Railway(City.KANSAS_CITY, City.SAINT_LOUIS, 2, true, 6, 8);
	
	public static Railway SAL = new Railway(City.SAINT_LOUIS, City.LITTLE_ROCK, 2, false, 1);
	public static Railway SN = new Railway(City.SAINT_LOUIS, City.NASHVILLE, 2, false, 1);
	
	public static Railway SLO = new Railway(City.SAN_FRANCISCO, City.LOS_ANGELES, 3, true, 3, 8);
	
	public static Railway LL = new Railway(City.LAS_VEGAS, City.LOS_ANGELES, 2, false, 1);
	
	public static Railway SAP = new Railway(City.SANTA_FE, City.PHOENIX, 3, false, 1);
	public static Railway SE = new Railway(City.SANTA_FE, City.EL_PASO, 2, false, 1);
	public static Railway SO = new Railway(City.SANTA_FE, City.OKLAHAMA_CITY, 3, false, 6);
	
	public static Railway OE = new Railway(City.OKLAHAMA_CITY, City.EL_PASO, 5, false, 3);
	public static Railway OD = new Railway(City.OKLAHAMA_CITY, City.DALLAS, 2, true, 1);
	public static Railway OL = new Railway(City.OKLAHAMA_CITY, City.LITTLE_ROCK, 2, false, 1);
	
	public static Railway LD = new Railway(City.LITTLE_ROCK, City.DALLAS, 2, false, 1);
	public static Railway LN = new Railway(City.LITTLE_ROCK, City.NEW_ORLEANS, 3, false, 4);
	public static Railway LNA = new Railway(City.LITTLE_ROCK, City.NASHVILLE, 3, false, 2);
	
	public static Railway NA = new Railway(City.NASHVILLE, City.ATLANTA, 1, false, 1);
	public static Railway NR = new Railway(City.NASHVILLE, City.RALEIGH, 2, true, 0);
	
	public static Railway RA = new Railway(City.RALEIGH, City.ATLANTA, 2, true, 1);
	public static Railway RC = new Railway(City.RALEIGH, City.CHARLESTON, 2, false, 1);
	
	public static Railway AN = new Railway(City.ATLANTA, City.NEW_ORLEANS, 4, true, 3, 7);
	public static Railway AM = new Railway(City.ATLANTA, City.MIAMI, 5, false, 6);
	
	public static Railway CM = new Railway(City.CHARLESTON, City.MIAMI, 4, false, 8);
	
	public static Railway LP = new Railway(City.LOS_ANGELES, City.PHOENIX, 3, false, 1);
	public static Railway LE = new Railway(City.LOS_ANGELES, City.EL_PASO, 6, false, 0);
	
	public static Railway PE = new Railway(City.PHOENIX, City.EL_PASO, 3, false, 1);
	
	public static Railway ED = new Railway(City.EL_PASO, City.DALLAS, 4, false, 5);
	public static Railway EH = new Railway(City.EL_PASO, City.HOUSTON, 6, false, 4);
	
	public static Railway DAH = new Railway(City.DALLAS, City.HOUSTON, 2, false, 1);
	
	public static Railway HN = new Railway(City.HOUSTON, City.NEW_ORLEANS, 2, false, 1);
	
	public static Railway NM = new Railway(City.NEW_ORLEANS, City.MIAMI, 6, false, 5);

}
