package hobo;

import java.util.*;

public class CardBag implements Cloneable, Iterable<Color> {
	private static final int ncolors = Color.values().length;
	private int[] ks = new int[ncolors]; // multiplicities
	private int size = 0;

	public CardBag() {}
	public CardBag(Color... cs) {
		for (Color c: cs)
			add(c);
	}

	public int size() { return size; }
	public boolean isEmpty() { return size == 0; }

	public void add(Color c) {
		ks[c.ordinal()]++;
		size++;
	}

	public void addAll(CardBag that) {
		for (int i = 0; i < ncolors; i++) {
			this.ks[i] += that.ks[i];
			size += that.ks[i];
		}
	}

	public void remove(Color c) {
		if (ks[c.ordinal()] > 0) {
			ks[c.ordinal()]--;
			size--;
		}
	}

	public void removeAll(CardBag that) {
		for (int i = 0; i < ncolors; i++) {
			int k = Math.min(this.ks[i], that.ks[i]);
			this.ks[i] -= k;
			size -= k;
		}
	}

	public boolean contains(Color c) {
		return ks[c.ordinal()] > 0;
	}

	public boolean containsAll(CardBag that) {
		for (int i = 0; i < ncolors; i++) {
			if (this.ks[i] < that.ks[i])
				return false;
		}
		return true;
	}

	// get a non-grey color of which at least one card is present
	public Color arbitraryNonGrey() {
		int j = Color.GREY.ordinal();
		for (int i = 0; i < ncolors; i++) {
			if (ks[i] > 0 && i != j)
				return Color.values()[i];
		}
		return null;
	}

	public int count(Color c) {
		return ks[c.ordinal()];
	}

	// weighted but otherwise uniformly random selection
	public Color draw() {
		assert(!isEmpty());
		double x = Math.random();
		int m = 0;
		for (int i = 0; i < ncolors; i++) {
			m += ks[i];
			if (x < ((double)m)/size) {
				ks[i]--;
				size--;
				return Color.values()[i];
			}
		}
		throw new RuntimeException();
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
		for (int i = 0; i < ncolors; i++) {
			if (ks[i] == 0)
				continue;
			sb.append(ks[i]);
			sb.append(" ");
			sb.append(Color.values()[i]);
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
				while (cs[0] < ncolors && ks[cs[0]] == 0)
					cs[0]++;
				
				if (cs[0] >= ncolors) {
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
				return Color.values()[cs[0]];
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
	
	public static void main(String[] args) {}
}
