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
		
		public String toString() {
			return city.toString();
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
			if(city == null) {
				System.out.println("city="+city); throw new RuntimeException();
			}
			if(city2 == null) {
				System.out.println("city2="+city2); throw new RuntimeException();
			}
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
	
	public static City getClosestCity(List<Railway> rails, City toCity) {
		double smallestEuclidianDistance = Double.POSITIVE_INFINITY;
		City city = null;
		for(Railway r : rails) {
			if(smallestEuclidianDistance > euclideanDistance(r.source, toCity)) {
				city = r.source;
				smallestEuclidianDistance = euclideanDistance(r.source, toCity);
			} else if(smallestEuclidianDistance > euclideanDistance(r.destination, toCity)) {
				city = r.destination;
				smallestEuclidianDistance = euclideanDistance(r.destination, toCity);
			}
		}
		return city;
	}
	
	public static ArrayList<Pot> clusterMissions(Set<Mission> missions) {
		ArrayList<Pot> pots = new ArrayList<Util.Pot>();
		int index = -1;
		for(Mission m : missions) {
			Mission mA = m;
			boolean isInPot = false;
			for(int k = 0; k < pots.size(); k++) {
				isInPot = pots.get(k).contains(mA);
				if(isInPot)
					break;
			}
			if(!isInPot) {
				index++;
				pots.add(new Pot());
				pots.get(index).missions.add(mA);
				for(int i = 0; i < pots.get(index).missions.size(); i++) {
					for(Mission t : missions) {
						if(!mA.equals(t)) {
							if(!pots.get(index).contains(t) && lineIntersect(mA, t))
								pots.get(index).missions.add(t);
						}		
					}				
				}
			}
		}
		
		
		return pots;
	}
	
	private static class Pot {
		Set<Mission> missions;
		public Pot() {
			missions = EnumSet.noneOf(Mission.class);
		}
		
		public boolean contains(Mission m) {
			if(missions.contains(m)) return true; else return false;
		}
		
		public String toString() {
			String s = "";
			for(Mission m : missions) {
				s += m;
			}
			return s;
		}
	}
	
	public static List<Railway> getSpanningTree(PlayerState ps, State s) {
		if(!ps.missions.isEmpty()) {
			Set<Railway> allAvailableRails = s.usableRailwaysFor(ps.handle);
			List<Railway> endSet = new ArrayList<Railway>();
			ArrayList<Pot> missionClusters = clusterMissions(ps.missions);
			for(int i = 0; i < missionClusters.size(); i++) {
				if(missionClusters.get(i).missions.size() == 1) {
					endSet = shortestPath(missionClusters.get(i).missions.iterator().next().source, missionClusters.get(i).missions.iterator().next().destination, allAvailableRails);
				} else {
					ArrayList<Mission> missionArray = new ArrayList<Mission>();
					missionArray.addAll(missionClusters.get(i).missions);
//					System.out.println(allAvailableRails);
					List<Railway> tempRailwayList = new ArrayList<Railway>();
					for(int k = 0; k < missionArray.size(); k++) {
						if(k == 0) {
//							System.out.println(shortestPath(missionArray.get(k).source, missionArray.get(k).destination, allAvailableRails));
							tempRailwayList.addAll(shortestPath(missionArray.get(k).source, missionArray.get(k).destination, allAvailableRails));
						} else {
							tempRailwayList.addAll(shortestPath(missionArray.get(k).source, getClosestCity(tempRailwayList, missionArray.get(k).source), allAvailableRails));
							tempRailwayList.addAll(shortestPath(missionArray.get(k).destination, getClosestCity(tempRailwayList, missionArray.get(k).destination), allAvailableRails));							
						}
					}
					endSet.addAll(tempRailwayList);
				}
			}
			return endSet;
		}
		return null;
	}
	
	public static boolean lineIntersect(Mission mission1, Mission mission2) {
		Equation eq1 = getEquation(mission1);
		Equation eq2 = getEquation(mission2);
		
		eq2.b -=eq1.b;
		eq1.b = 0;
		eq1.m -= eq2.m;
		eq2.m = 0;
		eq2.b = eq2.b/eq1.m;
		if(eq2.b > 0 && eq2.b < 968) return true; else return false;
	}
	
	private static Equation getEquation(Mission m) {
		City mission1LeftCity = cityMostLeft(m);
		City mission1RightCity;
		if(mission1LeftCity == m.source)
			mission1RightCity = m.destination;
		else
			mission1RightCity = m.source;
		return new Equation(calculateSlope(mission1LeftCity, mission1RightCity), mission1LeftCity.y);
	}
	
	private static double calculateSlope(City leftCity, City rightCity) {
		return ((rightCity.y-leftCity.y)/(rightCity.x-leftCity.x));
	}
	
	private static City cityMostLeft(Mission mission) { if(mission.source.x > mission.destination.x) return mission.destination; else return mission.source;}
	
	private static class Equation {
		double m;
		double b;
		
		public Equation(double m, double b) {
			this.m = m;
			this.b = b;
		}
	}
	
//	public static void main(String[] args) {
//		Set<Mission> set = EnumSet.noneOf(Mission.class);
//		set.add(Mission.LosAngeles_NewYork);
//		set.add(Mission.SanFrancisco_Atlanta);
//		set.add(Mission.Calgary_SaltLakeCity);
//		PlayerState ps = new PlayerState(1, "TestPlayer", Color.GREEN);
//		ps.missions = set;
//		List<Railway> rSet = getSpanningTree(ps);
//		for(Railway r : rSet) {
//			System.out.println(r);
//		}
//
//		
//	}
	
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
			entries.put(kv[0], kv.length < 2 ? null : kv[1]);
		}
		return entries;
	}

	public static double segmentAlongness(double[] a, double[] b, double[] c, double[] d) {
		return Math.abs(dot(unit(minus(b, a)), unit(minus(d, c))));
	}
	
	public static double distanceOfPointToSegment(double[] p, double[] a, double[] b) {
		double[] dab = minus(b, a);
		double x = dot(minus(p, a), unit(dab));
		if (x > norm(dab))
			x = norm(dab);
		if (x < 0)
			x = 0;
		double[] c = plus(a, times(x, unit(dab)));
		return norm(minus(p, c));
	}
	
	public static double[] unit(double[] u) {
		return times(1 / norm(u), u);
	}

	public static double norm(double[] u) {
		return Math.sqrt(dot(u, u));
	}

	public static double dot(double[] u, double[] v) {
		double x = 0;
		for (int i = 0; i < u.length; i++)
			x += u[i] * v[i];
		return x;
	}

	// java sucks...
	public static double[] times(double x, double[] v) {
		double[] w = new double[v.length];
		for (int i = 0; i < v.length; i++)
			w[i] = x * v[i];
		return w;
	}
	
	public static double[] plus(double[] u, double[] v) {
		double[] w = new double[v.length];
		for (int i = 0; i < v.length; i++)
			w[i] = u[i] + v[i];
		return w;
	}

	public static double[] minus(double[] u, double[] v) {
		double[] w = new double[v.length];
		for (int i = 0; i < v.length; i++)
			w[i] = u[i] - v[i];
		return w;
	}
}
