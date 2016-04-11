package s11;

public class DemoObjQueue {
  static void demo(int n) {
    ObjQueue f;
    int i, sum = 0;
    f = new ObjQueue();
    for (i = 0; i < n; i++)
      f.enqueue(i);
    while (!f.isEmpty())
      sum = sum + (Integer) f.dequeue();
    System.out.println(sum);
  }
}