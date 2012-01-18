package hobo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public enum Mission {
	LosAngeles_NewYork(21), Seattle_NewYork(22), Vancouver_SantaFe(13),
	LosAngeles_Miami(20), LosAngeles_Chicago(16), Montreal_Atlanta(9),
	Dallas_NewYork(11), Calgary_Phoenix(13), Denver_ElPaso(4),
	Winnipeg_LittleRock(11), NewYork_Atlanta(6), Chicago_SantaFe(9),
	Duluth_ElPaso(10), Boston_Miami(12), Calgary_SaltLakeCity(7),
	Portland_Nashville(17), Portland_Phoenix(11), Denver_Pittsburgh(11),
	Montreal_NewOrleans(13), Vancouver_Montreal(20), Helena_LosAngeles(8),
	Seattle_LosAngeles(9), SaultStMarie_OklahomaCity(9), SaultStMarie_Nashville(8),
	Chicago_NewOrleans(7), Toronto_Miami(10), SanFrancisco_Atlanta(17),
	Winnipeg_Houston(12), Duluth_Houston(8), KansasCity_Houston(5);
	
	public static final Mission[] all = values();

	public final City source, destination;
	public final int value;
	public final String imagePath;

	private Mission(int value) {
		this.value = value;

		this.imagePath = name() + ".png";

		String[] parts = name().split("_");
		source = City.valueOf(parts[0]);
		destination = City.valueOf(parts[1]);
	}

	public static Mission connecting(City c, City d) {
		for (Mission m: all)
			if (m.connects(c, d))
				return m;
		return null;
	}

	public boolean connects(City c) {
		return this.source == c || this.destination == c;
	}

	public boolean connects(City c, City d) {
		return this.source == c && this.destination == d ||
		       this.source == d && this.destination == c;
	}

	public String toString() {
		return "Mission(source: "+source+", destination: "+destination+", value: "+value+")";
	}
	
	public String coordinatesString() {
		return "source x="+source.x+", y="+source.y+"---destination x"+destination.x+", y="+destination.y;
	}
}
