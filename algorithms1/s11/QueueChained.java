package s11;

public class QueueChained<E> {
  class QueueNode {
    E         elt;
    QueueNode prev = null;

    QueueNode(E elt) {
      this.elt = elt;
    }
  }

  private QueueNode front;
  private QueueNode back;

  public QueueChained() {
  }

  public boolean isEmpty() {
    return back == null;
  }

  public E consult() {
    return front.elt;
  }

  public void enqueue(E elt) {
    QueueNode tmp = new QueueNode(elt);
    if (!this.isEmpty()) {
      this.back.prev = tmp;
      this.back = tmp;
    } else {
      this.back = this.front = tmp;
    }
  }

  public E dequeue() {
    E e = front.elt;
    if (front == back) {
      back = null;
      front = null;
    } else {
      front = front.prev;
    }
    return e;
  }
}