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
	
	
}
