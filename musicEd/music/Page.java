package musicEd.music;

import java.awt.Graphics;
import java.awt.Color;
import java.util.ArrayList;

import musicEd.graphicsLib.G;
import musicEd.reaction.Gesture;
import musicEd.reaction.Mass;
import musicEd.reaction.Reaction;

public class Page extends Mass {
    public static Page PAGE; // singleton (can make it final but we're not. only have one alive and can use undo to remove)
    public static Reaction R1; // a place to remember a particular reaction.

    public G.LoHi xMargin, yMargin;
    public int sysGap;
    public Sys.Fmt sysFmt = new Sys.Fmt();
    public ArrayList<Sys> sysList = new ArrayList<>();

    public Page(int y) {
        super("BACK");
        PAGE = this;
        int MM = 50;
        xMargin = new G.LoHi(MM, UC.windowWidth - MM);
        yMargin = new G.LoHi(y, UC.windowHeight - MM);
        addNewStaffFmtToSysFmt(y);
        addNewSys();

        // Reactions below
        addReaction(R1 = new Reaction("E-E") { // E-E reaction is to add new staff growing sysFmt
            public int bid(Gesture gest) {
                return (gest.vs.yM() < PAGE.allSysBot()) ? UC.noBid : 0;
            }

            public void act(Gesture gest) {
                addNewStaffFmtToSysFmt(gest.vs.yM());
            }            
        });

        addReaction(new Reaction("W-W") { // W-W reaction is to add a new system (already defined)
            public int bid(Gesture gest) {
                return (gest.vs.yM() < PAGE.allSysBot()) ? UC.noBid : 0;
            }

            public void act(Gesture gest) {
                if (PAGE.sysList.size() == 1) { // then we are adding the second one right after we defined the sys
                    PAGE.sysGap = gest.vs.yM() - PAGE.allSysBot();
                    R1.disable(); // disable the E-E reaction that can add new staff (disable further additions to sysFmt)
                }
                addNewSys();
            }

        });
    }

    public void addNewStaffFmtToSysFmt(int y) {
        int yOff = y - yMargin.lo;
        int iStaff = sysFmt.size(); // index of new staff
        sysFmt.addNew(yOff);
        for (Sys sys : sysList) {sys.addNewStaff(iStaff);}
    }

    public void addNewSys() {
        Sys sys = new Sys(this, sysList.size());
        sysList.add(sys);
        for (int i = 0; i < sysFmt.size(); i++) {
            sys.addNewStaff(i);
        }
    }

    public int sysTop(int iSys) {return yMargin.lo + iSys * (sysFmt.height() + sysGap);}

    public int allSysBot() { // Bot stands for bottom
        int n = sysList.size();
        return yMargin.lo + n * sysFmt.height() + (n - 1) * sysGap;
    }

    @Override
    public void show(Graphics g) {
        g.setColor(Color.RED);
        g.drawLine(0, yMargin.lo, 30, yMargin.lo);
        for (int i = 0; i < sysList.size(); i++) {
            sysFmt.showAt(g, sysTop(i), this);
        }
    }
}
