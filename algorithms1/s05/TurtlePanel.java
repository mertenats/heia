package s05;

//==============================================================================
// EIA-FR - TurtlePanel
//==============================================================================

import static java.lang.Math.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;

//------------------------------------------------------------------------------
public class TurtlePanel extends JComponent {
  public static final int      PLAIN                  = 0;
  public static final int      DOTTED                 = 1;
  public static final int      DASHED                 = 2;
  public static final int      DASHED_DOTTED          = 3;

  private static final boolean TEST_MODE              = false;

  private static final Color   GRID_COLOR             = new Color(100, 255, 100);
  private static final Color   GRID_0_0_COLOR         = new Color(250, 150, 0);

  private static final Color   DEF_BACKGROUND_COLOR   = Color.WHITE;
  private static final Color   DEF_PAINT_COLOR        = Color.BLACK;
  private static final float   DEF_PEN_SIZE           = 1.0f;
  private static final float   DEF_ORIENTATION        = 0;
  private static final int     DEF_STROKE_PATTERN     = PLAIN;
  private static final boolean INIT_IS_DOWN           = true;
  private static final boolean INIT_AUTO_REPAINT_MODE = true;

  private static final float   DEG_TO_RAD_FACT        = (float) (PI / 180);
  private static final int     MIN_BUFFER_WIDTH       = 1400;
  private static final int     MIN_BUFFER_HEIGHT      = 1000;

  private float                x;
  private float                y;
  private boolean              isDown                 = INIT_IS_DOWN;
  private float                penSize                = DEF_PEN_SIZE;
  private Color                penColor               = DEF_PAINT_COLOR;
  private Color                backgroundColor        = DEF_BACKGROUND_COLOR;
  private int                  strokePattern          = DEF_STROKE_PATTERN;
  private double               orientation            = DEF_ORIENTATION;
  private boolean              autoRepaint            = INIT_AUTO_REPAINT_MODE;
  private float                initialXPos;
  private float                initialYPos;
  private int                  panelWidth;
  private int                  panelHeight;
  private boolean              firstDisp              = false;

  private FontRenderContext    frc;
  private BufferedImage        buffer;                                           // Internal
                                                                                  // image
  private Graphics2D           bg2d;                                             // Graphical
                                                                                  // context
                                                                                  // associated
                                                                                  // to
                                                                                  // internal
                                                                                  // image
  private boolean              bReady;                                           // Buffer
                                                                                  // ready
                                                                                  // to
                                                                                  // paint
                                                                                  // on
                                                                                  // screen

  // ----------------------------------------------------------------------------
  // Create a TurtlePanel
  //
  // width : Panel width [pixel]
  // height : Panel height [pixel]
  // initX : Initial pen position (x-axis) [pixel]
  // initY : Initial pen position (y-axis) [pixel]
  // ----------------------------------------------------------------------------
  /* package */TurtlePanel(int width, int height, float initX, float initY) {

    setPreferredSize(new Dimension(width, height));

    x = initialXPos = initX;
    y = initialYPos = initY;

    setOpaque(true);

    // --- Create and register panel resize adapter
    addComponentListener(new PanelResizeAdapter(this));

    // --- Create and register mouse adapter
    addMouseListener(new PanelMouseAdapter(this));
  }

  // ----------------------------------------------------------------------------
  // Create an instance of a TurtleFrame with a TurtlePanel inside
  // - Initial pen position : 0, 0 (upper-left)
  // - Initial orientation : 0� (toward positive x axis
  // - Initial pen state : down
  // - Initial pen color : black
  // - Initial pen size : 1.0
  // - Initial background color : white
  //
  // title : Title of th frame
  // width : TurtlePanel width [pixel]
  // height : TurtlePanel height [pixel]
  // initX : Initial pen position (x-axis) [pixel]
  // initY : Initial pen position (y-axis) [pixel]
  // ----------------------------------------------------------------------------
  public static TurtlePanel create(String title, int width, int height,
      float initX, float initY, boolean exitOnClose) {

    TurtleFrame tf = new TurtleFrame(title, width, height, initX, initY,
        exitOnClose);
    return tf.tPanel;
  }

  // ----------------------------------------------------------------------------
  // Initialize panel
  // Important : has to be called AFTER the associated frame is realized
  // (i.e. after pack() or setVisible(true))
  // If image param is not null, it is drawn on the panel after initialization
  // ----------------------------------------------------------------------------
  public void init(BufferedImage image) {
    panelWidth = getWidth();
    panelHeight = getHeight();

    bReady = false;  // Avoid to paint during initialization

    // --- Create internal image and graphical context used to draw
    // (internal image size is (at least) 150% the panel size)
    int bufferWidth = (int) (1.5 * panelWidth);
    int bufferHeight = (int) (1.5 * panelHeight);
    if (bufferWidth < MIN_BUFFER_WIDTH)
      bufferWidth = MIN_BUFFER_WIDTH;
    if (bufferHeight < MIN_BUFFER_HEIGHT)
      bufferHeight = MIN_BUFFER_HEIGHT;
    buffer = (BufferedImage) this.createImage(bufferWidth, bufferHeight);
    bg2d = buffer.createGraphics();
    frc = bg2d.getFontRenderContext();

    // --- Set rendering hints
    bg2d.setRenderingHint(RenderingHints.KEY_RENDERING,
        RenderingHints.VALUE_RENDER_QUALITY);
    bg2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    bg2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    // --- Paint image background
    bg2d.setPaint(backgroundColor);
    bg2d.fill(new Rectangle2D.Float(0, 0, bufferWidth, bufferHeight));
    setPenColor(penColor);
    setPenSize(penSize);

    // --- Restore an image (if any)
    if (image != null) {
      bg2d.drawImage(image, 0, 0, this);
    }

    bReady = true;

    repaint();
  }

  // ----------------------------------------------------------------------------
  // Initialize panel
  // Important : has to be called AFTER the associated frame is realized
  // (i.e. after pack() or setVisible(true))
  // ----------------------------------------------------------------------------
  public void init() {
    init(null);
  }

  // ============================================================================
  // Primary functions
  // ============================================================================

  // ----------------------------------------------------------------------------
  // Set current pen position (absolute values, without drawing)
  // ----------------------------------------------------------------------------
  public void setAbsPos(float x, float y) {
    this.x = x;
    this.y = y;
  }

  // ----------------------------------------------------------------------------
  // Set current orientation (absolute angle)
  // ----------------------------------------------------------------------------
  public void setAbsOrientation(float angle) {
    orientation = angle % 360;
  }

  // ----------------------------------------------------------------------------
  // Set pen color
  // ----------------------------------------------------------------------------
  public void setPenColor(Color penColor) {
    this.penColor = penColor;
    bg2d.setPaint(penColor);
  }

  // ----------------------------------------------------------------------------
  // Set pen width (thinnest 0.0f)
  // ----------------------------------------------------------------------------
  public void setPenSize(float penSize) {
    this.penSize = penSize;
  }

  // ----------------------------------------------------------------------------
  // Raise the pen
  // ----------------------------------------------------------------------------
  public void penUp() {
    isDown = false;
  }

  // ----------------------------------------------------------------------------
  // Drop the pen
  // ----------------------------------------------------------------------------
  public void penDown() {
    isDown = true;
  }

  // ----------------------------------------------------------------------------
  // Relative advance (draw if pen is down)
  // ----------------------------------------------------------------------------
  public void advance(float distance) {
    double orientationRad = orientation * DEG_TO_RAD_FACT;
    float nx = x + (float) (distance * cos(orientationRad));
    float ny = y - (float) (distance * sin(orientationRad));

    if (isDown) {

      final Line2D.Float line = new Line2D.Float(x, y, nx, ny);

      // --- Draw in EDT to allow to resize the window during successive calls
      // to advance() without trouble
      try {
        EventQueue.invokeAndWait(new Runnable() {
          public void run() {
            setCurrentStroke();
            bg2d.draw(line);
          }
        });
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      }

      repaintIfNeeded();
    }
    x = nx;
    y = ny;
  }

  // ----------------------------------------------------------------------------
  // Turn left 90� (relative to current orientation)
  // ----------------------------------------------------------------------------
  public void turnLeft() {
    turnLeft(90);
  }

  // ----------------------------------------------------------------------------
  // Turn left (angle in degree, relative to current orientation)
  // ----------------------------------------------------------------------------
  public void turnLeft(float angle) {
    orientation = (orientation + angle) % 360;
  }

  // ----------------------------------------------------------------------------
  // Turn right 90� (relative to current orientation)
  // ----------------------------------------------------------------------------
  public void turnRight() {
    turnRight(90);
  }

  // ----------------------------------------------------------------------------
  // Turn right (angle in degree, relative to current orientation)
  // ----------------------------------------------------------------------------
  public void turnRight(float angle) {
    orientation = (orientation - angle) % 360;
  }

  // ----------------------------------------------------------------------------
  // Draw text at current position (x centered, slightly above current y pos)
  // ----------------------------------------------------------------------------
  public void mark(String s, int fontSize) {
    Font fnt = new Font("SanSerif", Font.PLAIN, fontSize);

    // --- Get text bounds
    TextLayout tLayout = new TextLayout(s, fnt, frc);
    Rectangle2D bounds = tLayout.getBounds();

    float cX = x - (float) (bounds.getWidth()) / 2;
    float cY = y - (float) (bounds.getHeight()) / 4 - penSize / 2;

    tLayout.draw(bg2d, cX, cY);

    repaintIfNeeded();
  }

  // ----------------------------------------------------------------------------
  // Draw text (x, y : coordinates of the text's basis line)
  // ----------------------------------------------------------------------------
  public Rectangle2D drawText(String s, int fontSize, float x, float y) {
    Font font = new Font("SanSerif", Font.PLAIN, fontSize);

    return drawText(s, font, x, y);
  }

  // ----------------------------------------------------------------------------
  // Draw text (x, y : coordinates of the text's basis line)
  // ----------------------------------------------------------------------------
  public Rectangle2D drawText(String s, Font font, float x, float y) {
    // --- Get text bounds
    TextLayout tLayout = new TextLayout(s, font, frc);
    Rectangle2D bounds = tLayout.getBounds();

    tLayout.draw(bg2d, x, y);

    repaintIfNeeded();
    return bounds;
  }

  // ----------------------------------------------------------------------------
  // Draw text (x, y : coordinates of the text's basis line + orientation)
  // ----------------------------------------------------------------------------
  public Rectangle2D drawText(String s, Font font, float x, float y, float angle) {
    // --- Get text bounds
    TextLayout tLayout = new TextLayout(s, font, frc);
    Rectangle2D bounds = tLayout.getBounds();

    bg2d.rotate(-angle * DEG_TO_RAD_FACT, x, y);
    tLayout.draw(bg2d, x, y);
    bg2d.rotate(+angle * DEG_TO_RAD_FACT, x, y);  // Restore orientation

    repaintIfNeeded();
    return bounds;
  }

  // ============================================================================
  // Additional functions
  // ============================================================================

  // ----------------------------------------------------------------------------
  // Repaint background (erase drawing)
  // ----------------------------------------------------------------------------
  public void setBackground(Color bgColor) {
    backgroundColor = bgColor;
    init();
  }

  // ----------------------------------------------------------------------------
  // Set auto-repaint mode
  // - if true : automatic repaint after each drawing operation
  // - if false : no repaint until explicit call to repaint()
  // (or system repaint or window resize)
  // ----------------------------------------------------------------------------
  public void setAutoRepaint(boolean autoRepaint) {
    this.autoRepaint = autoRepaint;
  }

  // ----------------------------------------------------------------------------
  // Set pen stroke pattern
  // ----------------------------------------------------------------------------
  public void setPenPattern(int pattern) {
    if (pattern == DOTTED || pattern == DASHED || pattern == DASHED_DOTTED) {
      strokePattern = pattern;
    } else {  // --- Plain
      strokePattern = PLAIN;
    }
  }

  // ----------------------------------------------------------------------------
  // Clear panel (erase drawing)
  // ----------------------------------------------------------------------------
  public void clear() {
    init();
  }

  // ----------------------------------------------------------------------------
  // Erase zone
  // ----------------------------------------------------------------------------
  public void erase(float x, float y, float width, float height) {
    bg2d.setPaint(backgroundColor);
    bg2d.fill(new Rectangle2D.Float(x, y, x + width, y + height));

    repaintIfNeeded();
  }

  // ----------------------------------------------------------------------------
  // Clear panel (erase) and reset current position to initial coordinates
  // ----------------------------------------------------------------------------
  public void reset() {
    clear();
    x = initialXPos;
    y = initialYPos;
  }

  // ----------------------------------------------------------------------------
  // Draw quadratic curve (from ... to, with one control point)
  // ----------------------------------------------------------------------------
  public void drawCurve(float fromX, float fromY, float ctrlX, float ctrlY,
      float toX, float toY) {

    QuadCurve2D.Float curve = new QuadCurve2D.Float(fromX, fromY, ctrlX, ctrlY,
        toX, toY);
    setCurrentStroke();
    bg2d.draw(curve);

    repaintIfNeeded();
  }

  // ----------------------------------------------------------------------------
  // Draw circle (center at current position)
  // ----------------------------------------------------------------------------
  public void drawCircle(float radius, boolean filled) {
    drawEllipse(x, y, radius * 2, radius * 2, filled);
  }

  // ----------------------------------------------------------------------------
  // Draw circle (center at x, y)
  // ----------------------------------------------------------------------------
  public void drawCircle(float centerX, float centerY, float radius,
      boolean filled) {
    drawEllipse(centerX, centerY, radius * 2, radius * 2, filled);
  }

  // ----------------------------------------------------------------------------
  // Draw ellipse
  // ----------------------------------------------------------------------------
  public void drawEllipse(float centerX, float centerY, float width,
      float height, boolean filled) {

    Ellipse2D.Float ellipse = new Ellipse2D.Float(centerX - width / 2, centerY
        - height / 2, width, height);
    if (filled) {
      bg2d.fill(ellipse);
    } else {
      setCurrentStroke();
      bg2d.draw(ellipse);
    }

    repaintIfNeeded();
  }

  // ----------------------------------------------------------------------------
  // Draw rectangle (center at current position)
  // ----------------------------------------------------------------------------
  public void drawRectangle(float width, float height, boolean filled) {
    drawRectangle(x - width / 2, y - height / 2, width, height, filled);
  }

  // ----------------------------------------------------------------------------
  // Draw rectangle
  // ----------------------------------------------------------------------------
  public void drawRectangle(float upperLeftX, float upperLeftY, float width,
      float height, boolean filled) {

    Rectangle2D.Float rectangle = new Rectangle2D.Float(upperLeftX, upperLeftY,
        width, height);
    if (filled) {
      bg2d.fill(rectangle);
    } else {
      setCurrentStroke();
      bg2d.draw(rectangle);
    }

    repaintIfNeeded();
  }

  // ============================================================================
  // Getters
  // ============================================================================

  // ----------------------------------------------------------------------------
  // Current x position
  // ----------------------------------------------------------------------------
  public float getXPos() {
    return x;
  }

  // ----------------------------------------------------------------------------
  // Current y position
  // ----------------------------------------------------------------------------
  public float getYPos() {
    return y;
  }

  // ----------------------------------------------------------------------------
  // Current orientation (0 : aligned to positive x axis)
  // ----------------------------------------------------------------------------
  public float getOrientation() {
    return (float) orientation;
  }

  // ----------------------------------------------------------------------------
  // Current pen size
  // ----------------------------------------------------------------------------
  public float getPenSize() {
    return penSize;
  }

  // ----------------------------------------------------------------------------
  // Current pen color
  // ----------------------------------------------------------------------------
  public Color getPenColor() {
    return penColor;
  }

  // ----------------------------------------------------------------------------
  // Current background color
  // ----------------------------------------------------------------------------
  public Color getBackground() {
    return backgroundColor;
  }

  // ----------------------------------------------------------------------------
  // Current pen stroke pattern
  // ----------------------------------------------------------------------------
  public int getPenPattern() {
    return strokePattern;
  }

  // ----------------------------------------------------------------------------
  // Get paint mode
  // ----------------------------------------------------------------------------
  public boolean getPaintMode() {
    return autoRepaint;
  }

  // ----------------------------------------------------------------------------
  // Get current panel width
  // ----------------------------------------------------------------------------
  public int getPanelWidth() {
    return panelWidth;
  }

  // ----------------------------------------------------------------------------
  // Get current panel height
  // ----------------------------------------------------------------------------
  public int getPanelHeight() {
    return panelHeight;
  }

  // ============================================================================
  // Transformation
  // ============================================================================

  // ----------------------------------------------------------------------------
  // Translate coordinate system
  // ----------------------------------------------------------------------------
  public void translate(float dx, float dy) {
    bg2d.translate(dx, dy);
  }

  // ----------------------------------------------------------------------------
  // Scale coordinate system
  // ----------------------------------------------------------------------------
  public void scale(float scaleFactor) {
    scale(scaleFactor, scaleFactor);
  }

  public void scale(float xScaleFactor, float yScaleFactor) {
    bg2d.scale(xScaleFactor, yScaleFactor);
  }

  // ----------------------------------------------------------------------------
  // Rotate coordinate system
  // Rotation center is [0,0]
  // ----------------------------------------------------------------------------
  public void rotate(float angle) {
    bg2d.rotate(-angle * DEG_TO_RAD_FACT);
  }

  // ----------------------------------------------------------------------------
  // Rotate coordinate system with a translation before and after
  // Rotation center is [rx,ry]
  // ----------------------------------------------------------------------------
  public void rotate(float angle, float rx, float ry) {
    bg2d.rotate(-angle * DEG_TO_RAD_FACT, rx, ry);
  }

  // ============================================================================
  // Others
  // ============================================================================

  // ----------------------------------------------------------------------------
  // Create and return a copy of the current internal image (buffer)
  // ----------------------------------------------------------------------------
  public BufferedImage cloneCurrentImage() {
    return cloneCurrentImage(0, 0, buffer.getWidth(), buffer.getHeight());
  }

  // ----------------------------------------------------------------------------
  // Create and return a copy of a part of the internal image (buffer)
  // ----------------------------------------------------------------------------
  public BufferedImage cloneCurrentImage(int fromX, int fromY, int width,
      int height) {
    BufferedImage bufferMem = (BufferedImage) this.createImage(width, height);
    Graphics2D g2 = bufferMem.createGraphics();
    g2.drawImage(buffer, fromX, fromY, TurtlePanel.this);
    return bufferMem;
  }

  // ----------------------------------------------------------------------------
  // Paste image at x, y location
  // ----------------------------------------------------------------------------
  public void pasteImage(BufferedImage image, int x, int y) {
    bg2d.drawImage(image, x, y, TurtlePanel.this);

    repaintIfNeeded();
  }

  // ----------------------------------------------------------------------------
  public String toString() {
    return "Turtle position: [" + x + ", " + y + "]   orientation: "
        + orientation + '\n' + "       penSize: " + penSize + "   penColor: "
        + penColor;
  }

  // ----------------------------------------------------------------------------
  // Draw internal image on screen panel
  // ----------------------------------------------------------------------------
  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;
    if (bReady) {
      g2d.drawImage(buffer, 0, 0, this);
    } else {
      if (TEST_MODE)
        System.out.println("> paintComponent() : Buffer not ready");
    }
  }

  // ----------------------------------------------------------------------------
  // Create stroke with dash pattern
  // ----------------------------------------------------------------------------
  private BasicStroke createStroke(float[] dashPattern) {
    BasicStroke stroke = new BasicStroke(penSize, BasicStroke.CAP_SQUARE,
        BasicStroke.JOIN_BEVEL, 0, dashPattern, 0);
    return stroke;
  }

  // ----------------------------------------------------------------------------
  // Set current stroke according to strokePattern
  // ----------------------------------------------------------------------------
  private void setCurrentStroke() {
    if (strokePattern == DOTTED) {
      BasicStroke bs = createStroke(new float[] { penSize, 2 * penSize });
      bg2d.setStroke(bs);
    } else if (strokePattern == DASHED) {
      BasicStroke bs = createStroke(new float[] { 8 * penSize, 4 * penSize });
      bg2d.setStroke(bs);
    } else if (strokePattern == DASHED_DOTTED) {
      BasicStroke bs = createStroke(new float[] { 8 * penSize, 4 * penSize,
          penSize, 4 * penSize });
      bg2d.setStroke(bs);
    } else {  // --- Plain line
      bg2d.setStroke(new BasicStroke(penSize));
    }
  }

  // ----------------------------------------------------------------------------
  // Draw a grid
  // ----------------------------------------------------------------------------
  public void drawGrid(float fromX, float toX, float xInterval, float fromY,
      float toY, float yInterval) {

    // --- Copy of the graphical context
    Graphics2D bg2dc = (Graphics2D) bg2d.create();

    // --- Thinest line
    bg2dc.setStroke(new BasicStroke(0));

    // --- Vertical lines
    for (float x = fromX; x < toX; x += xInterval) {
      if (abs(x) < 1e-5)
        bg2dc.setPaint(GRID_0_0_COLOR);
      else
        bg2dc.setPaint(GRID_COLOR);
      bg2dc.draw(new Line2D.Float(x, fromY, x, toY));
    }

    // --- Horizontal lines
    for (float y = fromY; y < toY; y += yInterval) {
      if (abs(y) < 1e-5)
        bg2dc.setPaint(GRID_0_0_COLOR);
      else
        bg2dc.setPaint(GRID_COLOR);
      bg2dc.draw(new Line2D.Float(fromX, y, toX, y));
    }

    repaintIfNeeded();

    bg2dc.dispose();
  }

  // ----------------------------------------------------------------------------
  // Repaint if autoRepaint true
  // ----------------------------------------------------------------------------
  private final void repaintIfNeeded() {
    if (autoRepaint)
      repaint();
  }

  // ============================================================================
  // PanelResizeApdapter (Inner Class)
  // ============================================================================

  private static class PanelResizeAdapter extends ComponentAdapter {
    TurtlePanel panel;

    public PanelResizeAdapter(TurtlePanel panel) {
      this.panel = panel;
    }

    @Override
    public void componentResized(ComponentEvent cEvent) {
      if (panel.buffer == null)
        return;  // If called before buffer initialized

      // --- As componentResized() is also called the first time the frame is
      // displayed, it's necessary to avoid a second init()
      if (panel.firstDisp) {
        panel.firstDisp = true;
        return;
      }

      // --- Save image before resizing and copy to new buffer
      BufferedImage memBuffer = panel.cloneCurrentImage();
      panel.init(memBuffer);
      panel.repaint();
    }
  }

  // ============================================================================
  // PanelMouseApdapter (Inner Class)
  // ============================================================================

  private static class PanelMouseAdapter extends MouseAdapter {
    TurtlePanel panel;

    public PanelMouseAdapter(TurtlePanel panel) {
      this.panel = panel;
    }

    @Override
    public void mousePressed(MouseEvent mEvent) {
      panel.repaint();
      if (TEST_MODE)
        System.out.println("> mousePressed() : Repainted");
    }
  }

  // ============================================================================
  // TurtleFrame (Inner Class)
  // ============================================================================

  // ----------------------------------------------------------------------------
  // TurtleFrame
  // ===========
  //
  // Usage :
  // -----
  // public class TestTurtleFrame {
  //
  // public static void main(String[] args) {
  //
  // TurtlePanel t = TurtlePanel.create("Test", 600, 500, 40, 20);
  //
  // t.advance(50);
  // t.turnRight();
  // t.advance(30);
  // t.penUp();
  // t.turnLeft();
  // t.advance(100);
  // t.penDown();
  // t.setPenColor(Color.BLUE);
  // t.setPenSize(2.5f);
  // t.advance(150);
  // t.mark("Texte", 12);
  // . . .
  // ----------------------------------------------------------------------------
  public static class TurtleFrame extends JFrame {

    TurtlePanel tPanel;

    // --------------------------------------------------------------------------
    // Create a TurtelFrame (private constructor)
    //
    // title : Title of the frame
    // width : TurtlePanel width [pixel]
    // height : TurtlePanel height [pixel]
    // initX : Initial pen position (x-axis) [pixel]
    // initY : Initial pen position (y-axis) [pixel]
    // --------------------------------------------------------------------------
    /* package */TurtleFrame(final String title, final int width,
        final int height, final float initX, final float initY,
        final boolean exitOnClose) {

      URL urlIcon = getClass().getResource("/resources/Turtle_Icon.png");
      if (urlIcon != null)
        setIconImage(new ImageIcon(urlIcon).getImage());

      try {
        EventQueue.invokeAndWait(new Runnable() {

          public void run() {
            if (exitOnClose)
              setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setTitle(title);

            tPanel = new TurtlePanel(width, height, initX, initY);
            add(tPanel);
            pack();
            setLocationRelativeTo(null);   // Center on screen
            tPanel.init();
            setVisible(true);
          }
        });
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      }
    }

    // --------------------------------------------------------------------------
    // Return frame's TurtlePanel
    // --------------------------------------------------------------------------
    public TurtlePanel getTurtlePanel() {
      return tPanel;
    }
  }
}
