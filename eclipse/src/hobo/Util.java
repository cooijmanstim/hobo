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
	
	public static int pathCost(List<Railway> rs, Set<Railway> zeroCostRailways) {
		int cost = 0;
		for (Railway r: rs) {
			if (!zeroCostRailways.contains(r))
				// score is a measure of difficulty of getting the railway
				cost += r.score();
		}
		return cost;
	}

	// only railways in railways are used
	public static List<Railway> shortestPath(City a, City b, Set<Railway> railways, Set<Railway> zeroCostRailways) {
		if (railways.isEmpty())
			return null;
		
		Set<City> seen = EnumSet.noneOf(City.class);
		PriorityQueue<AStarNode> fringe = new PriorityQueue<AStarNode>(railways.size());
		fringe.offer(new AStarNode(a, null, null, b, zeroCostRailways));
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
				fringe.offer(new AStarNode(r.otherCity(x.city), r, x, b, zeroCostRailways));
			}
		}
		return null;
	}
	
	private static class AStarNode implements Comparable<AStarNode> {
		public final Railway railway;
		public final City city;
		public final AStarNode prev;
		public /* pretend final */ double f, g, h;

		public AStarNode(City city, Railway railway, AStarNode prev, City target, Set<Railway> zeroCostRailways) {
			this.city = city; this.railway = railway; this.prev = prev;
			h = city.distances[target.ordinal()];

			if (prev == null)
				g = 0;
			else if (zeroCostRailways.contains(railway))
				g = prev.g;
			else
				g = prev.g + railway.score();

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

	public static City getClosestCity(Set<Railway> rails, City toCity) {
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

	public static Set<Set<Mission>> clusterMissions(Set<Mission> missions) {
		Set<Set<Mission>> clusters = new HashSet<Set<Mission>>();
		Set<Mission> unclustered_missions = EnumSet.copyOf(missions);
		while (!unclustered_missions.isEmpty()) {
			Iterator<Mission> i = unclustered_missions.iterator();

			// take an unclustered mission
			Mission m = i.next();

			// put it into a new cluster
			Set<Mission> cluster = EnumSet.of(m);
			i.remove();
			
			// put all suitable leftovers into the cluster
			while (i.hasNext()) {
				Mission n = i.next();
				if (lineIntersect(m, n)) {
					cluster.add(n);
					i.remove();
				}
			}

			clusters.add(cluster);
		}
		return clusters;
	}

	public static Set<Railway> getSpanningTree(Set<Mission> missions, Set<Railway> railways, Set<Railway> zeroCostRailways) {
		Set<Railway> tree = EnumSet.noneOf(Railway.class);
		if(missions.isEmpty())
			return tree;
		Set<Set<Mission>> missionClusters = clusterMissions(missions);
		for (Set<Mission> cluster: missionClusters) {
			Set<Railway> subtree = EnumSet.noneOf(Railway.class);
			for (Mission m: cluster) {
				List<Railway> rs;
				if(subtree.isEmpty()) {
					rs = shortestPath(m.source, m.destination, railways, zeroCostRailways);
					if (rs != null) subtree.addAll(rs);
				} else {
					rs = shortestPath(m.source, getClosestCity(subtree, m.source), railways, zeroCostRailways);
					if (rs != null) subtree.addAll(rs);
					rs = shortestPath(m.destination, getClosestCity(subtree, m.destination), railways, zeroCostRailways);
					if (rs != null) subtree.addAll(rs);
				}
			}
			tree.addAll(subtree);
		}
		return tree;
	}

	// nicked from http://stackoverflow.com/questions/563198/how-do-you-detect-where-two-line-segments-intersect
	public static boolean segmentsIntersect(double[] p0, double[] p1, double[] p2, double[] p3) {
		double[] s1 = minus(p1, p0), s2 = minus(p3, p2);
		double s = (-s1[1] * (p0[0] - p2[0]) + s1[0] * (p0[1] - p2[1])) / (-s2[0] * s1[1] + s1[0] * s2[1]),
		       t = ( s2[0] * (p0[1] - p2[1]) - s2[1] * (p0[0] - p2[0])) / (-s2[0] * s1[1] + s1[0] * s2[1]);
		return s >= 0 && s <= 1 && t >= 0 && t <= 1;
	}

	public static boolean lineIntersect(Mission mission1, Mission mission2) {
		return Mission.intersections[mission1.ordinal()][mission2.ordinal()];
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

	// java sucks!
	public static double[] toArrayOfPrimitives(Collection<Double> xs) {
		double[] ys = new double[xs.size()];
		int i = 0;
		for (double y: xs)
			ys[i++] = y;
		return ys;
	}

	public static double stdev(double[] xs) {
		xs = xs.clone();
		double mean = mean(xs);
		for (int i = 0; i < xs.length; i++)
			xs[i] = Math.pow(xs[i] - mean, 2);
		return Math.sqrt(stableSum(xs) / xs.length);
	}
	
	public static double mean(double[] xs) {
		return stableSum(xs) / xs.length;
	}
	
	public static double stableSum(double[] xs) {
		return stableSum(xs, 0, xs.length);
	}
	
	public static double stableSum(double[] xs, int a, int b) {
		int n = b - a;
		if (n <= 0) return 0;
		if (n == 1) return xs[a];
		return stableSum(xs, a, a + n/2) + stableSum(xs, a + n/2, b);
	}

	public static double[][] transpose(double[][] xss) {
		double[][] yss = new double[xss[0].length][xss.length];
		for (int i = 0; i < xss.length; i++) {
			for (int j = 0; j < xss[i].length; j++)
				yss[j][i] = xss[i][j];
		}
		return yss;
	}
}
