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
