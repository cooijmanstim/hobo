package hobo;

import java.util.*;

public class CardBag implements Cloneable, Iterable<Color> {
	private static final Color wildcard_color = Color.GREY;
	private static final int wildcard_ordinal = wildcard_color.ordinal();

	private int[] ks = new int[Color.all.length]; // multiplicities
	private int size = 0;
	
	public CardBag() {}
	public CardBag(Color... cs) {
		for (Color c: cs)
			add(c);
	}
	public CardBag(CardBag that) {
		this.addAll(that);
	}

	public CardBag clone() {
		return new CardBag(this);
	}
	
	@Override public boolean equals(Object o) {
		if (!(o instanceof CardBag))
			return false;
		return equals((CardBag)o);
	}
	
	@Override public int hashCode() {
		return Arrays.hashCode(ks);
	}

	public int size() { return size; }
	public boolean isEmpty() { return size == 0; }
	
	public void add(Color c) {
		ks[c.ordinal()]++;
		size++;
	}
	
	public void add(Color c, int k) {
		ks[c.ordinal()] += k;
		size += k;
	}

	public void addAll(CardBag that) {
		for (int i = 0; i < Color.all.length; i++) {
			int k = that.ks[i];
			this.ks[i] += k;
			size += k;
		}
	}

	public void remove(Color c) {
		int i = c.ordinal();
		if (ks[i] > 0) {
			ks[i]--;
			size--;
		}
	}

	public void removeAll(CardBag that) {
		for (int i = 0; i < Color.all.length; i++) {
			int k = Math.min(this.ks[i], that.ks[i]);
			this.ks[i] -= k;
			size -= k;
		}
	}

	public boolean contains(Color c) {
		return ks[c.ordinal()] > 0;
	}

	public boolean containsAll(CardBag that) {
		for (int i = 0; i < Color.all.length; i++) {
			if (this.ks[i] < that.ks[i])
				return false;
		}
		return true;
	}
	
	public boolean equals(CardBag that) {
		for (int i = 0; i < Color.all.length; i++) {
			if (this.ks[i] != that.ks[i])
				return false;
		}
		return true;
	}

	// get a non-wild color of which at least one card is present
	public Color arbitraryNonWildcard() {
		for (int i = 0; i < Color.all.length; i++) {
			if (ks[i] > 0 && i != wildcard_ordinal)
				return Color.all[i];
		}
		return null;
	}

	public int count(Color c) {
		return ks[c.ordinal()];
	}
	
	public double entropy() {
		if (size == 0)
			return 0;
		double e = 0;
		for (int k: ks) {
			if (k == 0)
				continue;

			double p = k * 1.0 / size;
			e -= p*Util.log2(p);
		}
		return e;
	}
	
	public double maxEntropy() {
		return size == 0 ? 0 : Util.log2(size);
	}
	
	public double utilityAsHand() {
		int u = 0;
		for (int k: ks) {
			// raise to some power to emphasize importance of having
			// multiple cards of one color
			// (specifically, raise to the 3rd instead of the 2nd
			// because 2^2 == 2*2)
			u += k * k * k;
			
			if (k == wildcard_ordinal)
				u += k * k * k;
		}
		// now take a root to keep the value somewhat in check
		return Math.pow(u, 1/3.0);
	}
	
	// weighted but otherwise uniformly random selection
	public Color draw(MersenneTwisterFast random) {
		Color c = sample(random);
		ks[c.ordinal()]--;
		size--;
		return c;
	}
	
	public Color sample(MersenneTwisterFast random) {
		assert(!isEmpty());
		double x = random.nextDouble();
		int m = 0;
		for (int i = 0; i < Color.all.length; i++) {
			m += ks[i];
			if (x < ((double)m)/size) {
				return Color.all[i];
			}
		}
		throw new RuntimeException();
	}
	
	public CardBag sample(int k, MersenneTwisterFast random) {
		return clone().remove_sample(k, random);
	}

	public CardBag remove_sample(int k, MersenneTwisterFast random) {
		CardBag subbag = new CardBag();
		for (int i = 0; i < k && size > 0; i++)
			subbag.add(draw(random));
		return subbag;
	}

	public double probabilityOfSample(CardBag that) {
		return Util.multivariate_hypergeometric(that.ks, this.ks);
	}
	
	// see what kind of card would be drawn next
	public Color cardOnTop(MersenneTwisterFast random) {
		return sample(random.clone());
	}

	public CardBag draw(int k, MersenneTwisterFast random) {
		k = Math.min(k, size);
		CardBag cs = new CardBag();
		for (; k > 0; k--)
			cs.add(draw(random));
		return cs;
	}

	// draw a card of the given color
	public Color draw(Color c) {
		assert(contains(c));
		ks[c.ordinal()]--;
		size--;
		return c;
	}

	public void addAll(Collection<Color> cs) {
		for (Color c: cs)
			add(c);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("CardBag(");
		for (int i = 0; i < Color.all.length; i++) {
			if (ks[i] == 0)
				continue;
			sb.append(ks[i]);
			sb.append(" ");
			sb.append(Color.all[i]);
			sb.append(", ");
		}
		int j = sb.lastIndexOf(", ");
		if (j >= 0)
			sb.delete(j, sb.length());
		sb.append(")");
		return sb.toString();
	}
	
	@Override public Iterator<Color> iterator() {
		return new Iterator<Color>() {
			private int[] cs = null;
			private boolean finalized = false;
			private int[] next_coordinates(int[] cs) {
				if (finalized)
					return null;

				if (cs == null) {
					cs = new int[]{ 0, 0 };
				} else {
					cs = cs.clone();
					if (cs[1] < ks[cs[0]] - 1) {
						// more of the same color
						cs[1]++;
						return cs;
					} else {
						// next color
						cs[1] = 0;
						cs[0]++;
					}
				}

				// next color until we get to a color for which cards are present
				while (cs[0] < Color.all.length && ks[cs[0]] == 0)
					cs[0]++;
				
				if (cs[0] >= Color.all.length) {
					finalized = true;
					return null;
				}
				return cs;
			}
			@Override public boolean hasNext() {
				return next_coordinates(cs) != null;
			}
			@Override public Color next() {
				cs = next_coordinates(cs);
				if (cs == null)
					throw new NoSuchElementException();
				return Color.all[cs[0]];
			}
			@Override public void remove() {
				throw new UnsupportedOperationException(); // YAGNI
			}
		};
	}
	
	// two cards are "equivalent" if they can be used in place of eachother.
	// e.g., a wildcard can be used in place of blue.
	public int countEquivalent(Color c) {
		// wildcard is a problematic case.  all cards that are individually
		// equivalent to a wildcard may not be mutually equivalent.
		// (i.e., equivalence is symmetric but not transitive)
		if (c == wildcard_color)
			throw new IllegalArgumentException();
		return ks[c.ordinal()] + ks[wildcard_ordinal];
	}

	public boolean allEquivalent() {
		return ks[wildcard_ordinal] == size || countEquivalent(arbitraryNonWildcard()) == size;
	}
	
	// A bag of cards that can be used to claim the railway
	// If the railway is wild, the caller must specify the
	// color of cards to use as a second argument.
	public CardBag cardsToClaim(Railway r) {
		assert(r.color != wildcard_color);
		return cardsToClaim(r, r.color);
	}

	public CardBag cardsToClaim(Railway r, Color c) {
		int n = r.length;
		CardBag cards;
		if (c == wildcard_color) {
			if (ks[wildcard_ordinal] < n)
				return null;

			cards = new CardBag();
			cards.add(c, n);
		} else {
			// use as many cards of color c as possible
			int k;
			if (c != r.color && r.color != wildcard_color)
				k = 0; // can't use cards of color c at all
			else
				k = Math.min(count(c), n);

			// add wildcards as necessary
			if (ks[wildcard_ordinal] < n-k)
				return null;

			cards = new CardBag();
			cards.add(c, k);
			cards.add(wildcard_color, n-k);
		}
		return cards;
	}

	public boolean canAfford(Railway r) {
		if (r.color != wildcard_color)
			return cardsToClaim(r) != null;
		for (Color c: Color.all)
			if (cardsToClaim(r, c) != null)
				return true;
		return false;
	}

	public int[] multiplicities() {
		return ks.clone();
	}

	// pffft
	private static final Color[] no_colors = new Color[0];
	public Color[] availableColors() {
		if (size == 0) return no_colors;
		int n = 0;
		for (int i = 0; i < ks.length; i++)
			if (ks[i] > 0)
				n++;
		Color[] colors = new Color[n];
		int j = 0;
		for (int i = 0; i < ks.length; i++)
			if (ks[i] > 0)
				colors[j++] = Color.all[i];
		assert(j == n);
		return colors;
	}

	// tests below here
	public static void requireIteratorFinitude() {
		Color[] colors = new Color[]{ Color.RED, Color.RED, Color.GREEN, Color.BLUE, Color.GREEN };
		CardBag cb = new CardBag(colors);
		Iterator<Color> i = cb.iterator();
		for (Color c: colors) {
			if (!i.hasNext()) throw new RuntimeException("iterator yields too few");
			i.next();
		}
		if (i.hasNext()) throw new RuntimeException("iterator yields too many");
	}
	
	static {
		requireIteratorFinitude();
	}
}
