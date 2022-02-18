package musicEd.reaction;

import java.util.ArrayList;
import java.util.HashMap;

import musicEd.music.I;
import musicEd.music.UC;

public abstract class Reaction implements I.React {
    public Shape shape;
    public static Map byShape = new Map();
    public static List initialReactions = new List();

    public Reaction(String shapeName) {
        shape = Shape.DB.get(shapeName);
        if (shape == null) {System.out.println("Error! Shape.DB doesn't know " + shapeName);}
    }

    public void enable() {byShape.getList(shape).safeAdd(this);}

    public void disable() {byShape.getList(shape).remove(this);}

    public static Reaction best(Gesture gest) {// Can fail.
        return byShape.getList(gest.shape).lowBid(gest);
    }

    public static void nuke() { // is used to reset for undo.
        byShape = new Map();
        initialReactions.enable();
    }

    //------------------------List-----------------------
    public static class List extends ArrayList<Reaction> {
        public void safeAdd(Reaction r) {if (!contains(r)) {add(r);}}

        public void addReaction(Reaction reaction) {add(reaction); reaction.enable();}

        public void removeReaction(Reaction r) {remove(r); r.disable();}

        public void clearAll() {
            for (Reaction r : this) {
                r.disable();
            }
            this.clear();
        }

        public Reaction lowBid(Gesture gest) {// Can return null.
            Reaction res = null; int bestSoFar = UC.noBid;
            for (Reaction r : this) {
                int b = r.bid(gest);
                if (b < bestSoFar) {bestSoFar = b; res = r;}
            }
            return res;
        }

        public void enable() {for (Reaction r : this) {r.enable();}}
    }

    //-------------------------Map-----------------------
    public static class Map extends HashMap<Shape, List> {
        public List getList(Shape s) {// Always succeed getting a list.
            List res = get(s);
            if (res == null) {res = new List(); put(s, res);}
            return res;
        }
    }
}
