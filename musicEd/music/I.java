package musicEd.music;

import java.awt.Graphics;

import musicEd.reaction.Gesture;

public interface I {
    public interface Draw {public void draw(Graphics g);}

    public interface Hit {public boolean hit(int x, int y);}

    public interface Area extends Hit {
        public void dn(int x, int y);
        public void up(int x, int y);
        public void drag(int x, int y);
    }

    public interface Show {public void show(Graphics g);}

    public interface Act {public void act(Gesture gest);}

    public interface React extends Act {public int bid(Gesture gest);}
}
