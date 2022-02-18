package musicEd.sandbox;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import musicEd.graphicsLib.G;
import musicEd.graphicsLib.Window;

public class Paint extends Window {
    public static int count = 0;
    public static Path thePath = new Path();
    public static Pic thePic = new Pic();
    public static Point splineMid = new Point(0, 0);
    
    public Paint() {
        super("Paint", 1000, 800);
    }

    @Override
    protected void paintComponent(Graphics g) {
        G.fillBackground(g);

        Color c = G.rndColor();
        g.setColor(c);
        g.fillOval(100, 100, 200, 300);
        g.setColor(Color.BLACK);
        g.drawLine(100, 600, 600, 100);

        int x = 400, y = 200;
        String msg = "count: " + count;
        g.drawString(msg, x, y);
        g.drawOval(x, y, 3, 3);

        FontMetrics fm = g.getFontMetrics();
        int a = fm.getAscent(), d = fm.getDescent(), w = fm.stringWidth(msg);
        g.drawRect(x, y - a, w, a + d);

        thePic.draw(g);
        if (thePic.size() > 0) {
            Path p = thePic.get(0);
            int ax = p.get(0).x, ay = p.get(0).y;
            int cx = p.get(p.size() - 1).x, cy = p.get(p.size() - 1).y;
            spline(g, ax, ay, splineMid.x, splineMid.y, cx, cy, 5);
        }
    }

    public static void spline(Graphics g, int ax, int ay, int bx, int by, int cx, int cy, int n) {
        if (n == 0) {
            g.drawLine(ax, ay, cx, cy);
            return;
        }
        int abx = (ax + bx) / 2, aby = (ay + by) / 2;
        int bcx = (cx + bx) / 2, bcy = (cy + by) / 2;
        int abcx = (abx + bcx) / 2, abcy = (aby + bcy) / 2;
        spline(g, ax, ay, abx, aby, abcx, abcy, n - 1);
        spline(g, abcx, abcy, bcx, bcy, cx, cy, n - 1);

    }


    @Override
    public void mousePressed(MouseEvent me) {
        count++;
        // thePath.clear();
        // thePath.add(me.getPoint());
        // repaint();

        /* Each time a new mousePress occurs, create a new Path. */
        thePath = new Path();
        thePath.add(me.getPoint());
        thePic.add(thePath);
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        thePath.add(me.getPoint());
        repaint();

        /* Have the most recent path store the dragged points. */
        // Path thisPath = thePic.get(thePic.size() - 1);
        // thisPath.add(me.getPoint());
        // repaint();
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        splineMid.setLocation(me.getX(), me.getY());
        repaint();
    }


    public static class Path extends ArrayList<Point> {
        public void draw(Graphics g) {
            for (int i = 1; i < size(); i++) {
                Point p = get(i - 1), n = get(i);
                g.drawLine(p.x, p.y, n.x, n.y);
            }
        }
    }

    // Helper class
    public static class Pic extends ArrayList<Path> {
        public void draw(Graphics g) {
            for (Path p : this) {p.draw(g);} // we use "this" since we know it will be thePic who calls this method.
        }
    }
}
