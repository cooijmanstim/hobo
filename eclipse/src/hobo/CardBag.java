package hobo;

import java.util.*;

public class CardBag implements Cloneable, Iterable<Color> {
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
		int h = 0;
		for (int i = 0; i < Color.all.length; i++) {
			if (ks[i] > 0)
				h = (h << ks[i]) ^ Color.all[i].hashCode();
		}
		return h;
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
		if (ks[c.ordinal()] > 0) {
			ks[c.ordinal()]--;
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

	// get a non-grey color of which at least one card is present
	public Color arbitraryNonGrey() {
		int j = Color.GREY.ordinal();
		for (int i = 0; i < Color.all.length; i++) {
			if (ks[i] > 0 && i != j)
				return Color.all[i];
		}
		return null;
	}

	public int count(Color c) {
		return ks[c.ordinal()];
	}

	// weighted but otherwise uniformly random selection
	public Color draw(Random random) {
		assert(!isEmpty());
		double x = random.nextDouble();
		int m = 0;
		for (int i = 0; i < Color.all.length; i++) {
			m += ks[i];
			if (x < ((double)m)/size) {
				ks[i]--;
				size--;
				return Color.all[i];
			}
		}
		throw new RuntimeException();
	}

	public CardBag draw(int k, Random random) {
		assert(size() >= k);
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
	// e.g., grey can be used in place of blue.
	public int countEquivalent(Color c) {
		// grey is a problematic case.  all cards that are individually
		// equivalent to grey may not be mutually equivalent.
		// (i.e., equivalence is symmetric but not transitive)
		if (c == Color.GREY)
			throw new IllegalArgumentException();
		return count(c) + count(Color.GREY);
	}

	public boolean allEquivalent() {
		return count(Color.GREY) == size || countEquivalent(arbitraryNonGrey()) == size;
	}
	
	// A bag of cards that can be used to claim the railway
	// If the railway is grey, the caller must specify the
	// color of cards to use as a second argument.
	public CardBag cardsToClaim(Railway r) {
		assert(r.color != Color.GREY);
		return cardsToClaim(r, r.color);
	}

	public CardBag cardsToClaim(Railway r, Color c) {
		int n = r.length;
		CardBag cards;
		if (c == Color.GREY) {
			if (count(c) < n)
				return null;

			cards = new CardBag();
			cards.add(c, n);
		} else {
			// use as many cards of color c as possible
			int k;
			if (c != r.color && r.color != Color.GREY)
				k = 0; // can't use cards of color c at all
			else
				k = Math.min(count(c), n);

			// add wildcards as necessary
			if (count(Color.GREY) < n-k)
				return null;

			cards = new CardBag();
			cards.add(c, k);
			cards.add(Color.GREY, n-k);
		}
		return cards;
	}

	public boolean canAfford(Railway r) {
		if (r.color != Color.GREY)
			return cardsToClaim(r) != null;
		for (Color c: Color.all)
			if (cardsToClaim(r, c) != null)
				return true;
		return false;
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
	
	public int[] multiplicities() {
		return ks.clone();
	}
}
