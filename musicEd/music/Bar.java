package musicEd.music;

import musicEd.graphicsLib.G;
import musicEd.reaction.Gesture;
import musicEd.reaction.Mass;
import musicEd.reaction.Reaction;

import java.awt.Graphics;
import java.awt.Color;

public class Bar extends Mass {
    public static final int LEFT = 8;
    public static final int RIGHT = 4;
    public Sys sys;
    public int x;
    public int barType = 0;
    /*
     * barType =
     * 0 - normal thin line.
     * 1 - double thin line.
     * 2 - thin, fat line, i.e. fine line.
     * 4-7 - fat, thin, dots, i.e. repeat to the right. (actually 4 - 6)
     * 8-11 - dots, thin, fat, i.e. repeat to the left. (actually 8 - 10)
     * 12 - dots, thin, fat, thin, dots, i.e. repeat both sides. (actually 12 - 14)
     */

    public Bar(Sys sys, int x) {
        super("BACK");
        this.sys = sys;
        this.x = x;

        addReaction(new Reaction("S-S") {
            @Override
            public int bid(Gesture gest) {
                int x = gest.vs.xM();
                if (Math.abs(x - Bar.this.x) > 2 * UC.marginSnap)
                    return UC.noBid;
                int y1 = gest.vs.yL(), y2 = gest.vs.yH();
                int sysTop = Bar.this.sys.yTop(), sysBot = Bar.this.sys.yBot();
                if (y1 < sysTop - 10 || y2 > sysBot + 10)
                    return UC.noBid;
                G.LoHi m = Page.PAGE.xMargin;
                if (x < m.lo || x > m.hi + UC.marginSnap)
                    return UC.noBid;
                int d = Math.abs(x - Bar.this.x);
                return (d > UC.marginSnap) ? UC.noBid : d;
            }

            @Override
            public void act(Gesture gest) {
                Bar.this.cycleType();
            }
        });

        addReaction(new Reaction("DOT") { // this means DOT the bar line (repeat)
            @Override
            public int bid(Gesture gest) {
                int x = gest.vs.xM(), y = gest.vs.yM();
                if (y < Bar.this.sys.yTop() || y > Bar.this.sys.yBot()) {
                    return UC.noBid;
                }
                int d = Math.abs(x - Bar.this.x);
                if (d > 3 * Page.PAGE.sysFmt.maxH)
                    return UC.noBid;
                return d;
            }

            @Override
            public void act(Gesture gest) {
                if (gest.vs.xM() < Bar.this.x) {
                    Bar.this.toggleLeft();
                } else {
                    Bar.this.toggleRight();
                }
            }
        });
    }

    @Override
    public void show(Graphics g) {
        g.setColor(Color.BLACK);
        // int sysTop = sys.yTop();
        int y1 = 0, y2 = 0; // y1 and y2 mark top and bottom of connected components.
        boolean justSawBreak = true; // this signals that we are at the top of the new component (doesn't continue).
        for (Staff staff : sys.staffs) {
            int staffTop = staff.yTop();
            if (justSawBreak)
                y1 = staffTop; // remember the start of the new component.
            y2 = staff.yBot(); // assume this ends the connected components.
            if (!staff.fmt.barContinues) { // this is how we know we really are at the end.
                drawVerticalLines(g, y1, y2);
            }
            justSawBreak = !staff.fmt.barContinues;
            if (barType > 3) {
                drawDots(g, x, staffTop);
            }
        }
    }

    public void drawVerticalLines(Graphics g, int y1, int y2) {
        int H = sys.page.sysFmt.maxH;
        if (barType == 0)
            thinBar(g, x, y1, y2);
        if (barType == 1) {
            thinBar(g, x, y1, y2);
            thinBar(g, x - H, y1, y2);
        }
        if (barType == 2) {
            fatBar(g, x - H, y1, y2, H);
            thinBar(g, x - 2 * H, y1, y2);
        }
        if (barType >= 4) {
            fatBar(g, x - H, y1, y2, H);
            if ((barType & LEFT) != 0) {
                thinBar(g, x - 2 * H, y1, y2);
                wings(g, x - 2 * H, y1, y2, -H, H);
            }
            if ((barType & RIGHT) != 0) {
                thinBar(g, x + H, y1, y2);
                wings(g, x + H, y1, y2, H, H);
            }
        }
    }

    public static void thinBar(Graphics g, int x, int y1, int y2) {
        g.drawLine(x, y1, x, y2);
    }

    public static void fatBar(Graphics g, int x, int y1, int y2, int dx) {
        g.fillRect(x, y1, dx, y2 - y1);// dx is how fat it is.
    }

    public static void wings(Graphics g, int x, int y1, int y2, int dx, int dy) {
        // positive dx will draw a rightHigher one. negative will draw the wing towards
        // left.
        g.drawLine(x, y1, x + dx, y1 - dy);
        g.drawLine(x, y2, x + dx, y2 + dy);
    }

    public void drawDots(Graphics g, int x, int top) { // dots from the top of the single staff
        // assume nLine = 5. (if you're writing a guitar staff which has 6 lines, you
        // will rewrite this method.)
        int H = sys.page.sysFmt.maxH;
        if ((barType & LEFT) != 0) {// meaning it is a left leaning bar
            g.fillOval(x - 3 * H, top + 11 * H / 4, H / 2, H / 2);
            g.fillOval(x - 3 * H, top + 19 * H / 4, H / 2, H / 2);
        }

        if ((barType & RIGHT) != 0) {// meaning it is a right leaning bar
            g.fillOval(x + 3 * H / 2, top + 11 * H / 4, H / 2, H / 2);
            g.fillOval(x + 3 * H / 2, top + 19 * H / 4, H / 2, H / 2);
        }
    }

    public void cycleType() {
        barType++;
        if (barType > 2) {
            barType = 0;
        }
    }

    public void toggleLeft() {
        barType = barType ^ LEFT;
    } // switch back and forth. ^ is XOR for bit values. changed to the left digit.

    public void toggleRight() {
        barType = barType ^ RIGHT;
    } // switch back and forth. changed to right digit.

}
