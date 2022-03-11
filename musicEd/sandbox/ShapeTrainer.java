package musicEd.sandbox;

import java.awt.*;
import java.awt.event.*;

import musicEd.graphicsLib.Window;
import musicEd.graphicsLib.G;
import musicEd.music.UC;
import musicEd.reaction.Shape;
import musicEd.reaction.Ink;

public class ShapeTrainer extends Window {
    public static String UNKNOWN = "This name is currently unknown";
    public static String ILLEGAL = "This is not a legal shape name";
    public static String KNOWN = "This is a known shape";
    public static String currName = "";
    public static String currState = ILLEGAL;
    public static Shape.Prototype.List pList = null;

    public ShapeTrainer() {
        super("ShapeTrainer!", UC.windowWidth, UC.windowWidth);
    }

    public void setState() {
        currState = (!Shape.Database.isLegal(currName)) ? ILLEGAL : UNKNOWN;
        if (currState == UNKNOWN) {
            if (Shape.DB.containsKey(currName)) {
                currState = KNOWN;
                pList = Shape.DB.get(currName).prototypes;
            } else {
                pList = null;
            }
        }
    }

    public void paintComponent(Graphics g) {
        G.fillBackground(g);
        g.setColor(Color.BLACK);
        g.drawString(currName, 600, 30);
        g.drawString(currState, 700, 30);
        g.setColor(Color.RED);
        Ink.BUFFER.show(g);
        if (pList != null) {
            pList.show(g);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        System.out.println("typed: " + c);
        // space bar means to clear those. The other are the different return key for
        // Unix and Windows
        currName = (c == ' ' || c == 0x0D || c == 0x0A) ? "" : currName + c;
        if (c == 0x0D || c == 0x0A) {
            Shape.saveDB();
        }
        setState();
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent me) {
        Ink.BUFFER.dn(me.getX(), me.getY());
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        Ink.BUFFER.drag(me.getX(), me.getY());
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        Ink ink = new Ink();
        Shape.DB.train(currName, ink.norm);
        setState();
        repaint();
    }

}
