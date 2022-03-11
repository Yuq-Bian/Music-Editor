package musicEd.music;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;

import musicEd.reaction.Gesture;
import musicEd.reaction.Reaction;

public class Stem extends Duration implements Comparable<Stem> {
    public Staff staff;
    public Head.List heads = new Head.List();
    public boolean isUp = true;
    public Beam beam = null;

    public Stem(Staff staff, Head.List heads, boolean isUp) {
        super();
        this.staff = staff;
        this.isUp = isUp;
        for (Head h : heads) {
            h.unStem();
            h.stem = this;
        }
        this.heads = heads;
        staff.sys.stems.add(this);
        setWrongSides();

        addReaction(new Reaction("E-E") {
            @Override
            public int bid(Gesture gest) {// increment flag
                int y = gest.vs.yM(), x1 = gest.vs.xL(), x2 = gest.vs.xH();
                int xs = Stem.this.x();
                if (x1 > xs || x2 < xs) {
                    return UC.noBid;
                }
                int y1 = Stem.this.yLo(), y2 = Stem.this.yHi();
                if (y < y1 || y > y2) {
                    return UC.noBid;
                }
                return Math.abs(y - (y1 + y2) / 2) + 55;// 55 is a bias so sys E-E can out bid it
            }

            @Override
            public void act(Gesture gest) {
                Stem.this.incFlag();
            }
        });

        addReaction(new Reaction("W-W") {
            @Override
            public int bid(Gesture gest) {// decrement flag
                int y = gest.vs.yM(), x1 = gest.vs.xL(), x2 = gest.vs.xH();
                int xs = Stem.this.x();
                if (x1 > xs || x2 < xs) {
                    return UC.noBid;
                }
                int y1 = Stem.this.yLo(), y2 = Stem.this.yHi();
                if (y < y1 || y > y2) {
                    return UC.noBid;
                }
                return Math.abs(y - (y1 + y2) / 2) + 55;// 55 is a bias so sys W-W can out bid it
            }

            @Override
            public void act(Gesture gest) {
                Stem.this.decFlag();
            }
        });
    }

    @Override
    public void show(Graphics g) {
        if (nflag >= -1 && heads.size() > 0) {
            int x = x(), h = staff.H(), yH = yFirstHead(), yB = yBeamEnd();
            g.drawLine(x, yH, x, yB);
            if (nflag > 0 && beam == null) {
                (isUp ? Glyph.dnFlags : Glyph.upFlags)[nflag - 1].showAt(g, h, x, yBeamEnd());
            }
        }

    }

    public Head firstHead() {
        return heads.get(isUp ? heads.size() - 1 : 0);
    }

    public Head lastHead() {
        return heads.get(isUp ? 0 : heads.size() - 1);
    }

    public int yFirstHead() {
        if (heads.size() == 0) {
            return 200;
        }
        Head h = firstHead();
        return h.staff.yLine(h.line);
    }

    public int yBeamEnd() {
        if (heads.size() == 0) {
            return 100;
        }
        if (beam != null && beam.stems.size() > 1 && beam.first() != this && beam.last() != this) {
            Stem b = beam.first(), e = beam.last();
            return Beam.yOfX(x(), b.x(), b.yBeamEnd(), e.x(), e.yBeamEnd());
        }
        Head h = lastHead();
        int line = h.line;
        line += (isUp ? -7 : 7); // default is one octave from last head on the beam
        int flagInc = nflag > 2 ? 2 * (nflag - 2) : 0; // if more than 2 flags we adjust stem end..
        line += (isUp ? -flagInc : flagInc); // .. but direction of adjustment depends on up or down stem
        if ((isUp && line > 4) || (!isUp && line < 4)) {
            line = 4;
        } // meet center line if we must
        return h.staff.yLine(line);
    }

    public int yLo() {
        return isUp ? yBeamEnd() : yFirstHead();
    }

    public int yHi() {
        return isUp ? yFirstHead() : yBeamEnd();
    }

    public int x() {
        if (heads.size() == 0) {
            return 100;
        } // guard against empty stem
        Head h = firstHead();
        return h.time.x + (isUp ? h.w() : 0);
    }

    public void setWrongSides() {// ToDo
        Collections.sort(heads);
        int i, last, next;
        if (isUp) {
            i = heads.size() - 1;
            last = 0;
            next = -1;
        } else {
            i = 0;
            last = heads.size() - 1;
            next = 1;
        }
        Head ph = heads.get(i);
        ph.wrongSide = false;
        while (i != last) {
            i += next;
            Head nh = heads.get(i);
            nh.wrongSide = (ph.staff == nh.staff) && (Math.abs(nh.line - ph.line) <= 1) && !ph.wrongSide;
            ph = nh;
        }
    }

    public void deleteStem() {
        if (heads.size() != 0) {
            System.out.println("ERROR - deleting stem that had heads on it");
        }
        staff.sys.stems.remove(this);
        if (beam != null) {
            beam.removeStem(this);
        } // tell the beam that you are leaving
        deleteMass();
    }

    @Override
    public int compareTo(Stem s) {
        return x() - s.x();
    }

    public static Stem getStem(Staff staff, Time time, int y1, int y2, boolean up) {
        Head.List heads = new Head.List();
        for (Head h : time.heads) {
            int yh = h.y();
            if (yh > y1 && yh < y2) {
                heads.add(h);
            }
        }
        if (heads.size() == 0) {
            return null;
        }
        Beam b = internalStem(staff.sys, time.x, y1, y2);// could be null
        Stem res = new Stem(staff, heads, up);
        if (b != null) {
            b.addStem(res);
        }
        return res;
    }

    public static Beam internalStem(Sys sys, int x, int y1, int y2) {
        for (Stem s : sys.stems) {
            if (s.beam != null && s.x() < x && s.yLo() < y2 && s.yHi() > y1) {
                int bx = s.beam.first().x(), by = s.beam.first().yBeamEnd();
                int ex = s.beam.last().x(), ey = s.beam.last().yBeamEnd();
                if (Beam.verticalLineCrossesSegment(x, y1, y2, bx, by, ex, ey)) {
                    return s.beam;
                }
            }
        }
        return null;
    }

    // ----------------List----------------------------------
    public static class List extends ArrayList<Stem> {
        public void sort() {
            Collections.sort(this);
        }

        public Stem.List allIntersectors(int x1, int y1, int x2, int y2) {
            Stem.List res = new Stem.List();
            for (Stem s : this) {
                int x = s.x(), y = Beam.yOfX(x, x1, y1, x2, y2);
                System.out.print(" x " + x);
                System.out.print(" y " + y);
                System.out.print(" x1 " + x1);
                System.out.print(" y1 " + y1);
                System.out.print(" x2 " + x2);
                System.out.println(" y2 " + y2);
                if (x > x1 && x < x2 && y > s.yLo() && y < s.yHi()) {
                    res.add(s);
                }
            }
            return res;
        }
    }

}
