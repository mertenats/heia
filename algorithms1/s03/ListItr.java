package s03;

public class ListItr {
	List list;
	public ListNode pred;
	public ListNode succ;

	public ListItr(List anyList) {
		list = anyList;
		goToFirst();
	}

	public void insertAfter(int e) {
		ListNode node = new ListNode(e, pred, succ);
		if (list.isEmpty()) {
			list.first = list.last = node;
		} else if (isFirst()) {
			list.first = succ.prev = node;
		} else if (isLast()) {
			list.last = pred.next = node;
		} else {
			succ.prev = pred.next = node;
		}
		succ = node;
		list.size++;
	}

	public void removeAfter() {
		ListNode tmp = succ;
		succ = succ.next;
		if (isFirst()) {
			list.first = tmp.next;
		} else {
			pred.next = succ;
		}

		if (isLast()) {
			list.last = tmp.prev;
		} else {
			succ.prev = pred;
		}
		list.size--;
	}

	public int consultAfter() {
		return succ.elt;
	}

	public void goToNext() {
		pred = succ;
		succ = succ.next;
	}

	public void goToPrev() {
		succ = pred;
		pred = pred.prev;
	}

	public void goToFirst() {
		succ = list.first;
		pred = null;
	}

	public void goToLast() {
		pred = list.last;
		succ = null;
	}

	public boolean isFirst() {
		return pred == null;
	}

	public boolean isLast() {
		return succ == null;
	}
}

// When isFirst(), it is forbidden to call goToPrev()
// When isLast(), it is forbidden to call goToNext()
// When isLast(), it is forbidden to call consultAfter(), or removeAfter()
// For an empty list, isLast()==isFirst()==true
// For a fresh ListItr, isFirst()==true
// Using multiple iterators on the same list is allowed only
// if none of them modifies the list
