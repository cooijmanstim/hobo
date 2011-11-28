package hobo;

import java.util.*;

public class Util {
	public static <E> Set<Set<E>> powerset(Set<E> set) {
		Set<Set<E>> powerset = new HashSet<Set<E>>((int)Math.pow(2, set.size()));
		powerset.add(new HashSet<E>());
		for (E element: set)
			for (Set<E> subset: new HashSet<Set<E>>(powerset))
				powerset.add(with(element, subset));
		return powerset;
	}

	public static <E> Set<E> with(E x, Set<E> xs) {
		xs = new HashSet<E>(xs);
		xs.add(x);
		return xs;
	}
}
