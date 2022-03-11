package musicEd.music;

import java.util.ArrayList;

public class Time {
  public int x;
  public Head.List heads = new Head.List();

  private Time(int x, List tl) {
    this.x = x;
    tl.add(this);
  }

  public void unStemHeads(int y1, int y2) {
    for (Head h : heads) {
      int y = h.y();
      if (y > y1 && y < y2) {
        h.unStem();
      }
    }
  }

  public void removeHead(Head h) {
    heads.remove(h);
    if (heads.size() == 0) { // this time group is now empty so
      h.staff.sys.times.remove(this); // remove it from the system.
    }
  }

  // --------------------------List---------------------
  public static class List extends ArrayList<Time> {
    public Sys sys;

    public List(Sys sys) {
      this.sys = sys;
    }

    public Time gTime(int x) {
      Time res = null;
      int dist = UC.SNAPTIME;
      for (Time t : this) {
        int d = Math.abs(t.x - x);
        if (d < dist) {
          dist = d;
          res = t;
        } // only find time less than snaptime
      }
      return (res != null) ? res : new Time(x, this);
    }
  }

}
