package musicEd.reaction;

import java.util.ArrayList;

import musicEd.graphicsLib.*;
import musicEd.music.I;

public class Gesture {
    public Shape shape;
    public G.VS vs;
    public static List UNDO = new List();
    public static I.Area AREA = new I.Area() {
        public boolean hit(int x, int y) {
            return true;
        }

        public void dn(int x, int y) {
            Ink.BUFFER.dn(x, y);
        }

        public void drag(int x, int y) {
            Ink.BUFFER.drag(x, y);
        }

        public void up(int x, int y) {
            Ink.BUFFER.add(x, y);
            Ink ink = new Ink();
            Gesture gest = Gesture.getNew(ink); // can fail.
            Ink.BUFFER.clear();
            if (gest != null) {
                if (gest.shape.name.equals("N-N")) {
                    undo();
                } else {
                    gest.doGesture();
                }
            }
        }
    };

    // private constructor because we will use factory to build it.
    private Gesture(Shape shape, G.VS vs) {
        this.shape = shape;
        this.vs = vs;
    }

    public void redoGesture() { // is called from the UNDO list. don't add to the UNDO list
        Reaction r = Reaction.best(this); // could fail
        if (r != null) {
            r.act(this);
        }
    }

    public void doGesture() {
        Reaction r = Reaction.best(this); // could fail
        if (r != null) {
            UNDO.add(this);
            r.act(this);
        }
    }

    public static void undo() {
        if (UNDO.size() > 0) {
            UNDO.remove(UNDO.size() - 1); // remove the last element
            Layer.nukeAll(); // eliminate on all the masses
            Reaction.nuke(); // clear byShape and reload initialReactons
            UNDO.redo();
        }
    }

    public static Gesture getNew(Ink ink) {// can return null.
        Shape s = Shape.recognize(ink);
        return (s == null) ? null : new Gesture(s, ink.vs);
    }

    // -----------------------------Gesture.List--------------------------
    public static class List extends ArrayList<Gesture> {
        public void redo() {
            for (Gesture g : this) {
                g.redoGesture();
            }
        }
    }

}
