package s03;

public class List {
	ListNode first, last;
	public int size;
	
	public List() {
		this.first = null;
		this.last = null;
		this.size = 0;
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public int size() {
		return this.size;
	}

	public static void main(String[] args) {
		if (findsBugInScenario1())
			System.out.println("Something is wrong...");
		else
			System.out.println("Test passed successfully");
	}

	private static boolean findsBugInScenario1() {
		int[] t = { 10, 20, 30, 40, 50 };
		List l = new List();
		ListItr li = new ListItr(l);
		if (!l.isEmpty())
			return true;
		if (!li.isFirst())
			return true;
		if (!li.isLast())
			return true;
		li.insertAfter(30);
		li.insertAfter(10);
		li.goToNext();
		li.insertAfter(20);
		li.goToLast();
		li.insertAfter(50);
		li.insertAfter(40);
		li.goToFirst();
		for (int i = 0; i < t.length; i++) {
			if (li.consultAfter() != t[i])
				return true;
			li.goToNext();
		}
		if (!li.isLast())
			return true;
		li.goToFirst();
		li.removeAfter();
		li.goToNext();
		li.removeAfter();
		li.goToNext();
		li.removeAfter();
		li.goToFirst();
		if (li.consultAfter() != 20)
			return true;
		li.goToNext();
		if (li.consultAfter() != 40)
			return true;
		li.goToNext();
		if (!li.isLast())
			return true;

		return false;
	}
}
