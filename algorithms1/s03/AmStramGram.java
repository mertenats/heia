package s03;

public class AmStramGram {
	public static void main(String[] args) {
		int n = 4, k = 2;
		if (args.length == 2) {
			n = Integer.parseInt(args[0]);
			k = Integer.parseInt(args[1]);
		}
		System.out.println("Winner is " + winnerAmStramGram(n, k));
	}

	public static int winnerAmStramGram(int n, int k) {
		List l = new List();
		ListItr li = new ListItr(l);
		int i;

		// creation of the list
		StringBuffer output = new StringBuffer();
		output.append("| ");
		for (i = 1; i <= n; i++) {
			li.insertAfter(i); // add the new item after the last created
			li.goToNext();
			output.append(i + " | ");
		}
		System.out.println(output.toString());
		li.goToFirst();
		
		while (l.size > 1) { // while size is bigger than 1
			for (i = 1; i < k; i++) {
				// if the cursor is at the end, it goes at the beginning
				if (li.isLast())
					li.goToFirst(); 
				li.goToNext(); // move kx times
			}
			if (li.isLast())
				li.goToFirst();
			li.removeAfter(); // then remove the element
		}
		li.goToFirst();
		return li.consultAfter(); // return the last one
	}
}