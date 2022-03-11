package musicEd.music;

import java.util.ArrayList;
import java.awt.Graphics;

import musicEd.reaction.Gesture;
import musicEd.reaction.Mass;
import musicEd.reaction.Reaction;

public class Sys extends Mass {
    public ArrayList<Staff> staffs = new ArrayList<>();
    public Page page;
    public int iSys;
    public Time.List times;
    public Stem.List stems = new Stem.List();

    public Sys(Page page, int iSys) {
        super("BACK");
        this.page = page;
        this.iSys = iSys;
        times = new Time.List(this);

        addReaction(new Reaction("E-E") {// beaming stems
            @Override
            public int bid(Gesture gest) {
                int x1 = gest.vs.xL(), y1 = gest.vs.yL(), x2 = gest.vs.xH(), y2 = gest.vs.yH();
                System.out.println("E-E in sys");
                if (Sys.this.rejectStemRange(y1, y2)) {
                    return UC.noBid;
                }
                Stem.List temp = Sys.this.stems.allIntersectors(x1, y1, x2, y2);
                System.out.println("intersectors " + temp.size());
                if (temp.size() < 2) {
                    return UC.noBid;
                }
                System.out.println("crossed " + temp.size() + " stems");
                Beam b = temp.get(0).beam;
                for (Stem s : temp) {
                    if (s.beam != b) {
                        return UC.noBid;
                    }
                }
                System.out.println("all stems share owner ");
                if (b == null && temp.size() != 2) {
                    return UC.noBid;
                }
                if (b == null && (temp.get(0).nflag != 0 || temp.get(1).nflag != 0)) {
                    return UC.noBid;
                }
                return 50;
            }

            @Override
            public void act(Gesture gest) {
                int x1 = gest.vs.xL(), y1 = gest.vs.yL(), x2 = gest.vs.xH(), y2 = gest.vs.yH();
                Stem.List temp = Sys.this.stems.allIntersectors(x1, y1, x2, y2);
                Beam b = temp.get(0).beam;
                if (b == null) {// creating beam on 2 stems
                    System.out.println("created Beam");
                    new Beam(temp.get(0), temp.get(1));
                } else {// increment flag count
                    for (Stem s : temp) {
                        s.incFlag();
                    }
                }
            }
        });
    }

    public int yTop() {
        return page.sysTop(iSys);
    }

    public int yBot() {
        return staffs.get(staffs.size() - 1).yBot();
    }

    public void addNewStaff(int iStaff) {
        staffs.add(new Staff(this, iStaff));
    }

    public Time getTime(int x) {
        return times.gTime(x);
    }

    public boolean rejectStemRange(int y1, int y2) {
        int gap = page.sysGap / 2;
        System.out.println("y1 " + y1);
        System.out.println("y2 " + y2);
        System.out.println("yTop()-gap " + (yTop() - gap));
        System.out.println("(yBot()+gap) " + (yBot() + gap));
        System.out.println("" + (y2 < (yTop() - gap)) + " - " + (y1 > (yBot() + gap)));
        return y2 < (yTop() - gap) || y1 > (yBot() + gap);
    }

    @Override
    public void show(Graphics g) {
    }

    // -----------------------------Fmt----------------------------
    public static class Fmt extends ArrayList<Staff.Fmt> {
        public int maxH = 8; // Technically this should be calculated when you add new staff.
        public ArrayList<Integer> staffOffsets = new ArrayList<>();

        public void addNew(int yOff) {
            add(new Staff.Fmt());
            staffOffsets.add(yOff);
        }

        public int height() {
            int last = size() - 1; // index of the last item in the sys
            return staffOffsets.get(last) + get(last).height(); // height of the last item plus its height
        }

        public void showAt(Graphics g, int y, Page page) {
            for (int i = 0; i < size(); i++) {
                get(i).showAt(g, y + staffOffsets.get(i), page);
            }
            int x1 = page.xMargin.lo, x2 = page.xMargin.hi, y2 = y + height();
            g.drawLine(x1, y, x1, y2);
            g.drawLine(x2, y, x2, y2);
        }
    }

}
