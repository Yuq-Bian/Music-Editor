package musicEd.sandbox;

import musicEd.graphicsLib.G;
import musicEd.graphicsLib.Window;
import musicEd.music.UC;
import musicEd.music.I;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.Timer;
import java.awt.event.*;

public class Game extends Window implements ActionListener {
    // public static G.VS theVS = new G.VS(100, 100, 200, 300);
    // public static Color color = G.rndColor();
    public static Square.List squares = new Square.List();
    public static Square theSquare;
    public static boolean dragging = false;
    public static G.V mouseDelta = new G.V(0, 0);
    public static Timer timer;
    public static G.V pressedLoc = new G.V(0, 0);
    public static final int WW = UC.windowWidth, WH = UC.windowHeight; // every cosntant is gathered in UC (universal constant). you can then use short simple name here.

    public Game() {
        super("Game!", WW, WH);
        timer = new Timer(30, this); // 30 frame a second is standard for movie 
        timer.setInitialDelay(5000); // 5000 is 5 seconds
        timer.start();
    }

    protected void paintComponent(Graphics g) {
        G.fillBackground(g);
        squares.draw(g);
    }

    public void mousePressed(MouseEvent me) {
        int x = me.getX(), y = me.getY();
        theSquare = squares.hit(x, y);
        if (theSquare == null) {
            dragging = false; // reset to false since we didn't hit anything
            theSquare = new Square(x, y);
            squares.add(theSquare); //can also just write a addNew function in the List class down below (Factory methods)
        } else {
            pressedLoc.set(x, y);
            theSquare.dv.set(0, 0);
            dragging = true;
            mouseDelta.set(theSquare.loc.x - x, theSquare.loc.y - y);            
        }
        repaint();        
    }

    public void mouseReleased(MouseEvent me) {
        if (dragging) {
            theSquare.dv.set(me.getX() - pressedLoc.x, me.getY() - pressedLoc.y);
        }
    }

    public void mouseDragged(MouseEvent me) {
        int x = me.getX(), y = me.getY();
        if (dragging) {
            theSquare.move(x + mouseDelta.x, y + mouseDelta.y);
        } else {
            theSquare.resize(x, y);
        }
        repaint();
    }
    // -------------------------------------------------------
    public static class Square extends G.VS implements I.Draw {
        Color color = G.rndColor();
        public G.V dv = new G.V(G.rnd(20) - 10, G.rnd(20) - 10);

        public Square(int x, int y) {super(x, y, 100, 100);}

        public void resize(int x, int y) {
            if (x > loc.x && y > loc.y) {
                size.set(x - loc.x, y - loc.y);
            }
        }

        public void move(int x, int y) {
            loc.set(x, y);
        }

        public void draw(Graphics g) {
            fill(g, color);
            moveAndBounce();
        }

        public void moveAndBounce() {
            if (xL() < 0 && dv.x < 0) {dv.x = - dv.x;}
            if (xH() > WW && dv.x > 0) {dv.x = - dv.x;}
            if (yL() < 0 && dv.y < 0) {dv.y = - dv.y;}
            if (yH() > WH && dv.y > 0) {dv.y = - dv.y;}
            loc.add(dv);
        }

        // ------------------------------------------------------
        public static class List extends ArrayList<Square> {
            public void draw(Graphics g) {for (Square s : this) {s.draw(g);}}
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

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
        
    }
}
