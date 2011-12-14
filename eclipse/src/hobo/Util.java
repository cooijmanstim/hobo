package hobo;

import java.util.*;

public class Util {
	public static <E> Set<Set<E>> powerset(Set<E> set) {
		Set<Set<E>> powerset = new HashSet<Set<E>>((int)Math.pow(2, set.size()));
		powerset.add(new HashSet<E>());
		for (E element: set)
			for (Set<E> subset: new HashSet<Set<E>>(powerset))
				powerset.add(with(element, subset));
		return powerset;
	}

	public static <E> Set<E> with(E x, Set<E> xs) {
		xs = new HashSet<E>(xs);
		xs.add(x);
		return xs;
	}
	
	public static <E> E sample(Set<E> xs, Random random) {
		int n = xs.size();
		int i = random.nextInt(xs.size());
		for (E x: xs) {
			if (i == 0)
				return x;
			i--;
		}
		throw new RuntimeException("this shouldn't happen");
	}
	
	public static <E> E arb(Set<E> e) {
		return e.iterator().next();
	}
	
	public static ArrayList<Railway> getShortestWay(City city1, City city2, ArrayList<Railway> rails, State s) {
		if(rails.size() > 0) {
			if((rails.get(rails.size()-1).destination.name.equals(city2.name) || rails.get(rails.size()-1).source.name.equals(city2.name))) {
				return rails;			
			}
		}
		Railway railwayChoose = null;
		double dist = Double.POSITIVE_INFINITY;
		
//		Set<Railway> OccupiedRailways = s.owner_by_railway.keySet();
//		Set<Railway> allRailways = new HashSet<Railway>(city1.railways);
//		allRailways.removeAll(OccupiedRailways);
//		System.out.println(city1);
//		System.out.println(allRailways.size());
		for(Railway r : city1.railways) { 
			
			if(r.source.name.equals(city1.name)) {
				City city = r.destination;
				double distance = calcDist(city, city2);
				if(dist > distance) {
					dist = distance;
					railwayChoose = r;
				}
			} else {
				City city = r.source;
				double distance = calcDist(city, city2);
				if(dist > distance) {
					dist = distance;
					railwayChoose = r;
				}
			}
		}
		rails.add(railwayChoose);
		if(railwayChoose.source.name.equals(city1.name)) {
			getShortestWay(railwayChoose.destination, city2, rails, s);					
		} else {
			getShortestWay(railwayChoose.source, city2, rails, s);
		}
		
		return rails;
	}
	
	private static double calcDist(City city1, City city2) {
		return Math.sqrt(Math.pow(city2.x-city1.x,2)+Math.pow(city2.y-city1.y,2));
	}
}
