package hobo;

import java.util.*;

public class Util {
	// some of these assume enum elements; if we ever need more generality we can
	// implement separate versions..  java sucks

	// you'll have to pass in an empty set from now on because java sucks
	public static <E extends Enum<E>> Set<Set<E>> powerset(Set<E> set, Set<E> empty_set) {
		Set<Set<E>> powerset = new LinkedHashSet<Set<E>>((int)Math.pow(2, set.size()));
		powerset.add(empty_set);
		for (E element: set)
			for (Set<E> subset: new LinkedHashSet<Set<E>>(powerset))
				powerset.add(with(element, subset));
		return powerset;
	}

	public static <E extends Enum<E>> Set<E> with(E x, Set<E> xs) {
		xs = EnumSet.copyOf(xs);
		xs.add(x);
		return xs;
	}
	
	// XXX: this runs in linear time
	public static <E> E sample(Set<E> xs, Random random) {
		int i = random.nextInt(xs.size());
		for (E x: xs) {
			if (i == 0)
				return x;
			i--;
		}
		throw new RuntimeException("this shouldn't happen");
	}

	// the sample will be put into ys; this is so that the caller can choose the set implementation
	@SuppressWarnings("unchecked")
	public static <E> Set<E> sample(Set<E> xs, int k, Random random, Set<E> ys) {
		if (xs.size() <= k) {
			ys.addAll(xs);
		} else {
			Object[] os = xs.toArray(); // fuck you java
			// this can take long if k is large
			while (ys.size() < k)
				ys.add((E)os[random.nextInt(os.length)]);
		}
		return ys;
	}
	
	public static <E> Set<E> remove_sample(Set<E> xs, int k, Random random, Set<E> ys) {
		sample(xs, k, random, ys);
		xs.removeAll(ys);
		return ys;
	}
	
	public static <E> E arb(Set<E> e) {
		return e.iterator().next();
	}
	
	public static List<Railway> shortestPath(City a, City b, Set<Railway> railways) {
		if (railways.size() == 0)
			return null;
		
		Set<City> seen = EnumSet.noneOf(City.class);
		PriorityQueue<AStarNode> fringe = new PriorityQueue<AStarNode>(railways.size());
		fringe.offer(new AStarNode(a, null, null, b));
		AStarNode x;
		while ((x = fringe.poll()) != null) {
			if (seen.contains(x.city))
				continue;
			seen.add(x.city);
			if (x.city == b)
				return x.reconstructPath();
			for (Railway r: x.city.railways) {
				if (!railways.contains(r))
					continue;
				fringe.offer(new AStarNode(r.otherCity(x.city), r, x, b));
			}
		}
		return null;
	}
	
	private static class AStarNode implements Comparable<AStarNode> {
		public final Railway railway;
		public final City city;
		public final AStarNode prev;
		public final double f, g, h;

		public AStarNode(City city, Railway railway, AStarNode prev, City target) {
			this.city = city; this.railway = railway; this.prev = prev;
			h = euclideanDistance(city, target);
			g = (prev == null ? 0 : prev.g + euclideanDistance(prev.city, city));
			f = g + h;
		}

		public int compareTo(AStarNode that) {
			return Double.compare(this.f, that.f);
		}

		public List<Railway> reconstructPath() {
			if (this.prev == null)
				return new ArrayList<Railway>();
			List<Railway> path = this.prev.reconstructPath();
			path.add(railway);
			return path;
		}
	}
	
	public static ArrayList<Railway> getShortestWay(City city1, City city2, ArrayList<Railway> rails, State s) {
		if (rails.size() > 0 && rails.get(rails.size()-1).connects(city2))
				return rails;
		Railway railwayChoose = null;
		double dist = Double.POSITIVE_INFINITY;
		
//		Set<Railway> OccupiedRailways = s.owner_by_railway.keySet();
//		Set<Railway> allRailways = new HashSet<Railway>(city1.railways);
//		allRailways.removeAll(OccupiedRailways);
//		System.out.println(city1);
//		System.out.println(allRailways.size());
		for (Railway r : city1.railways) {
			City city = r.otherCity(city1);
			double distance = euclideanDistance(city, city2);
			if (dist > distance) {
				dist = distance;
				railwayChoose = r;
			}
		}
		rails.add(railwayChoose);

		getShortestWay(railwayChoose.otherCity(city1), city2, rails, s);					
		
		return rails;
	}
	
	private static double euclideanDistance(City city1, City city2) {
		return Math.sqrt(Math.pow(city2.x-city1.x,2)+Math.pow(city2.y-city1.y,2));
	}
	
	public static double log2(double number) {
		return (Math.log10(number)/Math.log10(2));
	}

	public static double logsig(double x) {
		return 1 / (1 + Math.exp(-x));
	}

	public static Map<String,String> parseConfiguration(String configuration) {
		Map<String,String> entries = new LinkedHashMap<String,String>();
		for (String pair: configuration.trim().split("\\s+")) {
			String[] kv = pair.split(":");
			entries.put(kv[0], kv[1]);
		}
		return entries;
	}
}
