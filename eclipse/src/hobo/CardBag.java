package hobo;

import java.util.*;

public class CardBag implements Cloneable {
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
		sb.append("CardBag: ");
		for (int i = 0; i < ncolors; i++) {
			if (ks[i] == 0)
				continue;
			sb.append(ks[i]);
			sb.append(" ");
			sb.append(Color.values()[i]);
			if (i < ncolors - 1)
				sb.append(", ");
		}
		return sb.toString();
	}
}
