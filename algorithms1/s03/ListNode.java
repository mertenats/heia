package s03;

public class ListNode {
	public int elt;
	ListNode next, prev;

	ListNode(int theElement, ListNode thePrev, ListNode theNext) {
		elt = theElement;
		next = theNext;
		prev = thePrev;
	}
}