package s05;

public class FractalCurve {

  private static final int L           = 0;                                  // Code
                                                                              // meaning
                                                                              // TurnLeft
  private static final int R           = 1;                                  // Code
                                                                              // meaning
                                                                              // TurnRight
  private static final int A           = 3;                                  // Code
                                                                              // meaning
                                                                              // Advance

  private static final int panelWidth  = 800;
  private static final int panelHeight = 600;
  private static final int HMargin     = 30;
  private static final int startX      = HMargin / 2;
  private static final int startY      = panelHeight / 2;

  private static int       maxLevels   = 4;

  private static int[]     pattern     = { A, R, A, L, A, L, A, A, R, A, R, A,
      L, A                            };

  private TurtlePanel      t;
  private double           scaleFactor = Math.pow(getStraightAdvance(),
                                           maxLevels);
  private int              usableWidth = (panelWidth - HMargin);
  private float            baseDist    = (float) (usableWidth / scaleFactor);

  // ----------------------------------------------------------------------------
  public FractalCurve() {
    t = TurtlePanel.create("Fractal Curve", panelWidth, panelHeight, startX,
        startY, true);
    drawPattern(maxLevels);
  }

  // ----------------------------------------------------------------------------
  public void pause(long time) {
    try {
      Thread.sleep(time);
    } catch (InterruptedException e) {
    }
  }

  // ----------------------------------------------------------------------------
  public void drawPattern(int level) {
    // A COMPLETER !
    t.setAbsPos(50, 50);
    t.setPenSize(5);
    t.advance(100);
    t.turnLeft();
    t.advance(200);
  }

  // ----------------------------------------------------------------------------
  // Get number of (advance) steps in initial direction
  // ----------------------------------------------------------------------------
  private int getStraightAdvance() {
    int ix = 1, iy = 0;    // Initial direction (orientation vectors)
    int ctSteps = 0;     // Count steps in initial direction

    for (int op : pattern) {
      if (op == A) {
        if (ix == 1)
          ctSteps++;  // Step backward
        else if (ix == -1)
          ctSteps--;  // Step forward
      } else if (op == L) {
        int tx = ix;
        ix = -iy;
        iy = tx;
      } else if (op == R) {
        int tx = ix;
        ix = iy;
        iy = -tx;
      }
    }
    return ctSteps;
  }

  // ----------------------------------------------------------------------------
  public static void main(String[] args) {
    if (args.length == 1)
      maxLevels = Integer.parseInt(args[0]);
    new FractalCurve();
  }
}