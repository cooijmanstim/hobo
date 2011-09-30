package hobo;

import java.util.*;

public class CardBag implements Cloneable {
	private static final int nkinds = Card.values().length;
	private int[] ks = new int[nkinds]; // multiplicities
	private int size = 0;

	public int size() { return size; }
	public boolean isEmpty() { return size == 0; }

	public void add(Card c) {
		ks[c.ordinal()]++;
		size++;
	}

	public void addAll(CardBag that) {
		for (int i = 0; i < nkinds; i++) {
			this.ks[i] += that.ks[i];
			size += that.ks[i];
		}
	}

	public void remove(Card c) {
		if (ks[c.ordinal()] > 0) {
			ks[c.ordinal()]--;
			size--;
		}
	}

	public void removeAll(CardBag that) {
		for (int i = 0; i < nkinds; i++) {
			int k = Math.min(this.ks[i], that.ks[i]);
			this.ks[i] -= k;
			size -= k;
		}
	}

	public boolean contains(Card c) {
		return ks[c.ordinal()] > 0;
	}

	public boolean containsAll(CardBag that) {
		for (int i = 0; i < nkinds; i++) {
			if (this.ks[i] < that.ks[i])
				return false;
		}
		return true;
	}

	public int count(Card c) {
		return ks[c.ordinal()];
	}


	// weighted but otherwise uniformly random selection
	public Card draw() {
		assert(!isEmpty());
		double x = Math.random();
		int m = 0;
		for (int i = 0; i < nkinds; i++) {
			m += ks[i];
			if (x < ((double)m)/size) {
				ks[i]--;
				size--;
				return Card.values()[i];
			}
		}
	}

	// draw a card like the one given
	public Card draw(Card c) {
		assert(contains(c));
		ks[c.ordinal()]--;
		size--;
		return c;
	}


	public void addAll(Collection<Card> cs) {
		for (Card c: cs)
			add(c);
	}
}
