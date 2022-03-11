package musicEd.reaction;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import musicEd.music.*;

public class Layer extends ArrayList<I.Show> implements I.Show {
    public String name;
    public static HashMap<String, Layer> byName = new HashMap<>();
    public static Layer ALL = new Layer("ALL");

    public Layer(String name) {
        this.name = name;
        if (!name.equals("ALL")) {
            ALL.add(this);
        }
        byName.put(name, this);
    }

    @Override
    public void show(Graphics g) {
        for (I.Show s : this) {
            s.show(g);
        }
    }

    public static void nukeAll() {
        for (I.Show lay : ALL) {
            ((Layer) lay).clear();
        }
    }

}
