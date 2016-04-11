package s11;

public class ObjQueue {
  class QueueNode {
    Object    elt;
    QueueNode prev = null;

    QueueNode(Object elt) {
      this.elt = elt;
    }
  }

  private QueueNode front;
  private QueueNode back;

  public ObjQueue() {
  }

  public void enqueue(Object elt) {
    if (isEmpty())
      back = front = new QueueNode(elt);
    else
      back = back.prev = new QueueNode(elt);
  }

  public boolean isEmpty() {
    return back == null;
  };

  // PRE: !isEmtpy()
  public Object consult() {
    assert (!isEmpty());
    return front.elt;
  }

  // PRE: !isEmtpy()
  public Object dequeue() {
    assert (!isEmpty());
    Object e = front.elt;
    if (front == back) {
      back = null;
      front = null;
    } else {
      front = front.prev;
    }
    return e;
  }
}