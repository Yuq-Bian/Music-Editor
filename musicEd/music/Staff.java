package musicEd.music;

import musicEd.reaction.Gesture;
import musicEd.reaction.Mass;
import musicEd.reaction.Reaction;
import musicEd.graphicsLib.*;

import java.awt.Graphics;
import java.awt.Color;

public class Staff extends Mass {
    public Sys sys;
    public int iStaff; // index in the system
    public Staff.Fmt fmt;
    public Clef initialClef = null;

    public Staff(Sys sys, int iStaff) {
        super("BACK");
        this.sys = sys;
        this.iStaff = iStaff;
        fmt = sys.page.sysFmt.get(iStaff);

        // Reactions below
        addReaction(new Reaction("S-S") { // Create bar lines.
            public int bid(Gesture gest) {
                int x = gest.vs.xM(), y1 = gest.vs.yL(), y2 = gest.vs.yH();
                G.LoHi m = Page.PAGE.xMargin;
                if (x < m.lo || (x > m.hi + UC.marginSnap)) return UC.noBid;
                int d = Math.abs(y1 - Staff.this.yTop()) + Math.abs(y2 - Staff.this.yBot());
                return (d < 30) ? d + UC.marginSnap : UC.noBid; // we bias to prefer barCycle over barType
            }

            public void act(Gesture gest) {
                int rightMargin = Page.PAGE.xMargin.hi;
                int x = gest.vs.xM();
                if (x > rightMargin - UC.marginSnap) { // x is close to the right margin
                    x = rightMargin;
                }
                new Bar(Staff.this.sys, x);
            }
        });

        addReaction(new Reaction("S-S") { // Toggle bar continues.
            public int bid(Gesture gest) {
                if (Staff.this.sys.iSys != 0) return UC.noBid; // only bar continues in first system.
                int y1 = gest.vs.yL(), y2 = gest.vs.yH();
                int iStaff = Staff.this.iStaff;
                int iLastStaff = Page.PAGE.sysFmt.size() - 1;
                if (iStaff == iLastStaff) return UC.noBid; // this is the last staff which cannot continue.
                if (Math.abs(y1 - Staff.this.yBot()) > 20) return UC.noBid;
                Staff nextStaff = Staff.this.sys.staffs.get(iStaff + 1);
                if (Math.abs(y2 - nextStaff.yTop()) > 20) return UC.noBid;
                return 10;
            }

            public void act(Gesture gest) {
                Page.PAGE.sysFmt.get(Staff.this.iStaff).toggleBarContinues();
            }
        });

        addReaction(new Reaction("SE-SW") { // F clef
            public int bid(Gesture gest) {
                int x = gest.vs.xM(), y1 = gest.vs.yL(), y2 = gest.vs.yH();
                G.LoHi m = Page.PAGE.xMargin;
                if (x < m.lo || x > m.hi) return UC.noBid;
                int d = Math.abs(y1 - Staff.this.yTop()) + Math.abs(y2 - Staff.this.yBot());
                return (d > 50) ? UC.noBid : d;
            }

            public void act(Gesture gest) {
                Staff.this.initialClef = Clef.F;
            }
        });

        addReaction(new Reaction("SW-SE") { // G clef
            public int bid(Gesture gest) {
                int x = gest.vs.xM(), y1 = gest.vs.yL(), y2 = gest.vs.yH();
                G.LoHi m = Page.PAGE.xMargin;
                if (x < m.lo || x > m.hi) return UC.noBid;
                int d = Math.abs(y1 - Staff.this.yTop()) + Math.abs(y2 - Staff.this.yBot());
                return (d > 50) ? UC.noBid : d;
            }

            public void act(Gesture gest) {
                Staff.this.initialClef = Clef.G;
            }
        });

        addReaction(new Reaction("SW-SW"){// create note head
            @Override
            public int bid(Gesture gest) {
                int x = gest.vs.xM(), y = gest.vs.yM();
                G.LoHi m = Page.PAGE.xMargin;
                if(x < m.lo || x > m.hi) {return UC.noBid;}
                int H = Staff.this.H(), top = Staff.this.yTop() - H, bot = Staff.this.yBot() + H;
                if(y < top || y > bot) {return UC.noBid;}
                return 10;
            }

            @Override
            public void act(Gesture gest) {
                new Head(Staff.this, gest.vs.xM(), gest.vs.yM());
                
            }
        });

        addReaction(new Reaction("E-S"){// create one/eighth rest

            @Override
            public int bid(Gesture gest) {
                int x = gest.vs.xL(), y = gest.vs.yM();
                G.LoHi m = Page.PAGE.xMargin;
                if(x < m.lo || x > m.hi){return UC.noBid;}
                int H = Staff.this.H(), top = Staff.this.yTop() - H, bot = Staff.this.yBot() + H;
                if(y < top || y > bot){return UC.noBid;}
                return 10;
            }

            @Override
            public void act(Gesture gest) {
                Time t = Staff.this.sys.getTime(gest.vs.xL());
                (new Rest(Staff.this, t)).nflag = 1;
                
            }
            
        });

    }

    public int yTop() {return sys.yTop() + sysOff();}
    public int yBot() {return yTop() + fmt.height();}
    public int H() {return fmt.H;}
    public int yLine(int line) {return yTop() + H() * line;}
    public int lineOff(int y){
        int H = H();
        int bias = 50;
        int top = yTop() - H * bias;
        return ((y-top+H/2)/H - bias);
    }
    public int sysOff() {return sys.page.sysFmt.staffOffsets.get(iStaff);}
    public void show(Graphics g) {
        if (initialClef != null) {
            initialClef.showAt(g, Page.PAGE.xMargin.lo + 4 * fmt.H, yTop(), fmt.H);
        }
    }

    //-----------------------------Format----------------------------
    public static class Fmt{
        public int nLines = 5, H = 8; // H is half the spacing between two lines.
        public boolean barContinues = false;

        public void toggleBarContinues() {barContinues = !barContinues;}

        public int height() {return 2 * H * (nLines - 1);}

        public void showAt(Graphics g, int y, Page page) {
            g.setColor(Color.GRAY);
            int x1 = page.xMargin.lo, x2 = page.xMargin.hi, h = 2 * H;
            for (int i = 0; i < nLines; i++) {
                g.drawLine(x1, y, x2, y);
                y += h;
            }
        }
    }//-----------------------------Format----------------------------
}
