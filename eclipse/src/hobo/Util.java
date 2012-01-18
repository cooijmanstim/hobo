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
	public static <E> E sample(Set<E> xs, MersenneTwisterFast random) {
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
	public static <E> Set<E> sample(Set<E> xs, int k, MersenneTwisterFast random, Set<E> ys) {
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
	
	public static <E> Set<E> remove_sample(Set<E> xs, int k, MersenneTwisterFast random, Set<E> ys) {
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
			h = city.distances[target.ordinal()];
			g = (prev == null ? 0 : prev.g + prev.city.distances[city.ordinal()]);
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

	public static City getClosestCity(List<Railway> rails, City toCity) {
		double xmin = Double.POSITIVE_INFINITY;
		City city = null;
		int i = toCity.ordinal();
		for(Railway r : rails) {
			if(xmin > r.source.distances[i]) {
				city = r.source;
				xmin = r.source.distances[i];
			} else if(xmin > r.destination.distances[i]) {
				city = r.destination;
				xmin = r.destination.distances[i];
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
			entries.put(kv[0], kv.length > 1 ? kv[1] : null);
		}
		return entries;
	}

	public static double segmentAlongness(double[] a, double[] b, double[] c, double[] d) {
		return Math.abs(dot(unit(minus(b, a)), unit(minus(d, c))));
	}
	
	public static double distanceOfPointToSegment(double[] p, double[] a, double[] b) {
		double[] dab = minus(b, a);
		double x = dot(minus(p, a), unit(dab));
		x = Math.min(x, norm(dab));
		x = Math.max(x, 0);
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

	public static long binomial_coefficient(int k, int n) {
		if (k == 0 || k == n) return 1;
		if (n == 0 || k > n) return 0;
		return binomial_coefficient(k - 1, n - 1) + binomial_coefficient(k, n - 1);
	}
	
	// ns contains the number of marbles of each color in the urn,
	// ks describes the desired selection
	// this is used for cardbags, so the numbers are hopefully manageable
	// speed was assumed to be not too important
	public static double multivariate_hypergeometric(int[] ks, int[] ns) {
		assert(ns.length == ks.length);
		
		// collect the factorials
		int N = 0, K = 0;
		// need boxed type because of the reverse sort later... (java sucks)
		// numerator is sum(ks)! * (sum(ns) - sum(ks))! * n1! * n2! * ...
		Integer[]   numerator_factorials = new Integer[ns.length * 1 + 2];
		// denominator is sum(ns)! * k1! * (n1 - k1)! * k2! * (n2 - k2)! * ...
		Integer[] denominator_factorials = new Integer[ns.length * 2 + 1];
		for (int i = 0; i < ns.length; i++) {
			N += ns[i];
			K += ks[i];
			numerator_factorials[i*1+2] = ns[i];
			denominator_factorials[i*2+1] = ks[i];
			denominator_factorials[i*2+2] = ns[i] - ks[i];
		}
		numerator_factorials[0] = K;
		numerator_factorials[1] = N - K;
		denominator_factorials[0] = N;

		// ensure we factorial-divide numbers that are like in
		// size -- this way factorial_divide can skip many
		// multiplications
		Arrays.sort(numerator_factorials,   Collections.reverseOrder());
		Arrays.sort(denominator_factorials, Collections.reverseOrder());

		double probability = 1;

		// go through the two arrays and pairwise factorial-divide the elements
		int i, I;
		for (i = 0, I = Math.min(numerator_factorials.length, denominator_factorials.length); i < I; i++)
			probability *= factorial_divide(numerator_factorials[i], denominator_factorials[i]);

		// handle leftovers
		for (; i < numerator_factorials.length; i++)
			probability *= factorial_divide(numerator_factorials[i], 0);
		for (; i < denominator_factorials.length; i++)
			probability *= factorial_divide(0, denominator_factorials[i]);

		return probability;
	}

	// compute n! / d!
	public static double factorial_divide(int n, int d) {
		if (n == d) return 1;
		double y = 1;
		// if n < d, iterate from n + 1 to d, otherwise from d + 1 to n
		for (int i = Math.max(1, Math.min(n, d) + 1), I = Math.max(n, d); i <= I; i++)
			y *= i;
		return n > d ? y : 1/y;
	}
	
	public static long factorial(int n) {
		long f = 1;
		for (; n > 1; n--)
			f *= n;
		return f;
	}
	
	public static void normalize(double[] xs) {
		double sum = 0;
		for (int i = 0; i < xs.length; i++)
			sum += xs[i];
		for (int i = 0; i < xs.length; i++)
			xs[i] /= sum;
	}
	
	public static void normalize(double[][] xs) {
		double sum = 0;
		for (int i = 0; i < xs.length; i++)
			for (int j = 0; j < xs[i].length; j++)
				sum += xs[i][j];
		for (int i = 0; i < xs.length; i++)
			for (int j = 0; j < xs[i].length; j++)
				xs[i][j] /= sum;
	}

	public static double[][] clone(double[][] xs) {
		double[][] ys = new double[xs.length][0];
		for (int i = 0; i < xs.length; i++) {
			ys[i] = new double[xs[i].length];
			for (int j = 0; j < xs[i].length; j++)
				ys[i][j] = xs[i][j];
		}
		return ys;
	}
}
