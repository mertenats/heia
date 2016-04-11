package s17;
import java.util.Random;
// ======================================================================
public class IntQueueArray {
  // to complete !
  private int [] buffer = new int[10];
  private int    front=1;
  private int    back=0;
  private int    size=0;
  // ------------------------------
  public      IntQueueArray()        {}
  // ------------------------------
  public void    enqueue   (int elt) {
    checkSize();
    back++; if(back==buffer.length) back=0;
    buffer[back]=elt;
    size++;
  }
  // ------------------------------
  public boolean isEmpty   ()        {return size==0;}
  // ------------------------------
  public int     consult   ()        {
    return buffer[front];
  }
  // ------------------------------
  public int     dequeue   ()        {
    int e = buffer[front];
    front++; if(front==buffer.length) front=0;
    size--;
    return e;
  }
  // ------------------------------
  private void checkSize() {
    if (size<buffer.length-1) return;
    int [] aux = new int [2*buffer.length];
    int i=front;
    int j;
    for(j=0; j<size; j++) {
      aux[j] = buffer[front];
      front++; if(front==buffer.length) front=0;
    }
    front=0;
    back=j-1;
    buffer=aux;
  }

// ======================================================================
  public static void main(String [] args) {
    if (args.length != 1) {
      System.out.println("Usage : java IntQueueArray testMax");
      System.exit(-1);
    }
    int n = Integer.parseInt(args[0]);
    Random r = new Random();
    IntQueueArray q = new IntQueueArray();
    int m=0; int k=0; int p = 0;
    for(int i=0; i<n; i++) {
      boolean doAdd = r.nextBoolean();
      if (doAdd) {
	k++; 
	q.enqueue(k); 
	ok(!q.isEmpty(),  "should be non-empty "+m+" "+k+" "+p+"\n");
	m++;
	//System.out.print("a("+k+")");
      } else {
	if (m==0) {
	  ok(q.isEmpty(),  "should be empty "+m+" "+k+" "+p+"\n");
	} else {
	  ok( !q.isEmpty(), "should be non-empty "+m+" "+k+" "+p+"\n");
	  int e = q.dequeue();
	  //System.out.print("r("+e+")");
	  m--;
	  ok( e == p+1, "not FIFO "+m+" "+k+" "+p+"\n");
	  p++;
	}
      }
    }
    System.out.println("Test passed successfully");
  }
  // ------------------------------------------------------------
  static void ok(boolean b, String s) {
    if (b) return;
    throw new RuntimeException("property not verified: "+s);
  }
}
