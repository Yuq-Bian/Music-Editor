package musicEd.sandbox;

import musicEd.graphicsLib.G;
import musicEd.music.I;
import musicEd.graphicsLib.Window;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Squares extends Window {
    // public static G.VS theVS = new G.VS(100, 100, 200, 300);
    // public static Color color = G.rndColor();
    public static Square.List squares = new Square.List();
    public static Square theSquare;
    // public static boolean dragging = false;
    public static G.V mouseDelta = new G.V(0, 0);
    public static I.Area curArea;
    public static Square BACKGROUND = new Square(0, 0) {
        @Override
        public void dn(int x, int y) {
            theSquare = new Square(x, y);
            squares.add(theSquare);
        }

        @Override
        public void drag(int x, int y) {
            theSquare.resize(x, y);
        }
    };

    static {
        BACKGROUND.color = Color.WHITE;
        BACKGROUND.size.set(5000, 5000);
        squares.add(BACKGROUND);
    }

    public Squares() {super("Squares!", 1000, 800);}

    protected void paintComponent(Graphics g) {
        G.fillBackground(g);
        squares.draw(g);
    }

    public void mousePressed(MouseEvent me) {
        int x = me.getX(), y = me.getY();
        theSquare = squares.hit(x, y);
        curArea = theSquare;
        curArea.dn(x, y);
        repaint();        
    }

    public void mouseDragged(MouseEvent me) {
        int x = me.getX(), y = me.getY();
        curArea.drag(x, y);
        repaint();
    }
    // -------------------------------------------------------
    public static class Square extends G.VS implements I.Area {
        Color color = G.rndColor();
        public Square(int x, int y) {super(x, y, 100, 100);}
        public void resize(int x, int y) {
            if (x > loc.x && y > loc.y) {
                size.set(x - loc.x, y - loc.y);
            }
        }

        public void move(int x, int y) {
            loc.set(x, y);
        }

        @Override
        public void dn(int x, int y) {
            mouseDelta.set(theSquare.loc.x - x, theSquare.loc.y - y);
        }
        @Override
        public void up(int x, int y) {
            
        }
        @Override
        public void drag(int x, int y) {theSquare.move(x + mouseDelta.x, y + mouseDelta.y);}

        // No need to write the hit() method since G.VS already implemented this class.
        // @Override
        // public boolean hit(int x, int y) {
        //     return x >= loc.x && y >= loc.y && x <= loc.x + size.x && y <= loc.y + size.y;
        // }

        // ------------------------------------------------------
        public static class List extends ArrayList<Square> {
            public void draw(Graphics g) {for (Square s : this) {s. fill(g, s.color);}}
            // public void addNew(int x, int y) {this.add(new Square(x, y));}
            public Square hit(int x, int y) {
                Square res = null;
                for (Square s : this) {
                    if (s.hit(x, y)) {
                        res = s; //we want to return the last one in the list, which is why we don't return here.
                    }
                }
                return res;
            }
        }
    }
}
