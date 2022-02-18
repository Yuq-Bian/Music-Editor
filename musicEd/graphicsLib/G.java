package musicEd.graphicsLib;

import java.util.Random;
import java.awt.*;
import java.io.Serializable;

public class G {
    public static Random RND = new Random();
    public static int rnd(int max) {return RND.nextInt(max);}
    public static Color rndColor() {return new Color(rnd(256), rnd(256), rnd(256));}
    public static void fillBackground(Graphics g) {g.setColor(Color.WHITE); g.fillRect(0, 0, 5000, 5000);}

    // Helper class below:
    // vector to track x and y value (more like a point)
    public static class V implements Serializable {
        public int x, y;
        public static Transform  T = new Transform();
        public V(int x, int y) {this.set(x, y);} //top down programming since we haven't written set() yet. bottom up is better for testing
        public void set(int x, int y) {this.x = x; this.y = y;}
        public void set(V v) {set(v.x, v.y);}
        public void add(V v) {x += v.x; y+= v.y;}
        public int tx() {return x * T.n / T.d + T.dx;} // multiple first then divide, as the division is a int division.
        public int ty() {return y * T.n / T.d + T.dy;}
        public void setT(V v) {set(v.tx(), v.ty());}
        public void blend(V v, int k) {
            set((k * x + v.x) / (k + 1), (k * y + v.y) / (k + 1));
        }

        // -------------------------------G.V.Transform--------------------------
        public static class Transform {
            public int dx, dy, n, d;
            public void setScale(int oW, int oH, int nW, int nH) {
                n = (nW > nH)? nW : nH; //we want the bigger one of the new one
                d = (oW > oH)? oW : oH; //we want the bigger one of the old one
            }

            public int setOff(int oX, int oW, int nX, int nW) {
                return nX + nW / 2 - (oX + oW / 2) * n / d;
            }

            public void set(VS oVS, VS nVS) {
                setScale(oVS.size.x, oVS.size.y, nVS.size.x, nVS.size.y);
                dx = setOff(oVS.loc.x, oVS.size.x, nVS.loc.x, nVS.size.x);
                dy = setOff(oVS.loc.y, oVS.size.y, nVS.loc.y, nVS.size.y);
            }

            public void set(BBox oBox, VS nVS) {
                setScale(oBox.h.size(), oBox.v.size(), nVS.size.x, nVS.size.y);
                dx = setOff(oBox.h.lo, oBox.h.size(), nVS.loc.x, nVS.size.x);
                dy = setOff(oBox.v.lo, oBox.v.size(), nVS.loc.y, nVS.size.y);
            }
        } // ---------------------------G.V.Transform-------------------------------
    } // ---------------------------G.V-----------------------------

    // Vector(location) and size
    public static class VS {
        public V loc, size;
        public VS(int x, int y, int w, int h) {loc = new V(x, y); size = new V(w, h);}
        public void fill(Graphics g, Color c) {g.setColor(c); g.fillRect(loc.x, loc.y, size.x, size.y);}
        public boolean hit(int x, int y) {return x >= loc.x && x <= (loc.x + size.x) && y >= loc.y && y <= (loc.y + size.y);}
        public int xL() {return loc.x;} // return the left side of the low value of x
        public int xH() {return loc.x + size.x;} // return the left side of the high value of x
        public int xM() {return (loc.x + loc.x + size.x) / 2;} // return the left side of the mid value of x
        public int yL() {return loc.y;} // return the left side of the low value of y
        public int yH() {return loc.y + size.y;} // return the left side of the high value of y
        public int yM() {return (loc.y + loc.y + size.y) / 2;} // return the left side of the mid value of y
    }

    // Range for low and high (if out of the range, push it, or keep track of boundaries so far)
    public static class LoHi {
        public int lo, hi;

        public LoHi(int lo, int hi) {this.lo = lo; this.hi = hi;}

        public void set(int v) {lo = v; hi = v;}
        public void add(int v) {if (v < lo) {lo = v;} if (v > hi) {hi = v;}} // update when there's a new point
        public int size() {return ((hi - lo) == 0)? 1 : hi - lo;} // if hi == low, we return 1 (don't want to return 0)
        public int constrain(int v) 
            {if (v < lo) return lo;
            return (v > hi)? hi : v;}
    }

    // Bounding box (smallest & biggest what you drew)
    public static class BBox {
        public LoHi h, v; //vertical and horizontal
        public BBox() {h = new LoHi(0, 0); v = new LoHi(0, 0);}
        public void set(int x, int y) {h.set(x); v.set(y);}
        public void add(int x, int y) {h.add(x); v.add(y);}
        public void add(V v) {add(v.x, v.y);}
        public VS getNewVS() {return new VS(h.lo, v.lo, h.hi - h.lo, v.hi - v.lo);}
        public void draw(Graphics g) {g.drawRect(h.lo, v.lo, h.hi - h.lo, v.hi - v.lo);}
    }

    // Poly lines
    public static class PL implements Serializable {
        public V[] points;

        public PL(int count) {
            points = new V[count];
            for (int i = 0; i < count; i++) {points[i] = new V(0, 0);}
        }

        public int size() {return points.length;}

        public void draw(Graphics g) {drawN(g, points.length);}

        public void drawN(Graphics g, int n) {
            for (int i = 1; i < n; i++) {
                g.drawLine(points[i - 1].x, points[i - 1].y, points[i].x, points[i].y);
                // g.drawOval(points[i - 1].x - 2, points[i - 1].y - 2, 4, 4); // draw ovals around each point
            }
        }

        public void transform() {
            for (int i = 0; i < points.length; i++) {
                points[i].setT(points[i]);
            }
        }
    }
}

