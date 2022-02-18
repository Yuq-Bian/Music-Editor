package musicEd.reaction;

import musicEd.graphicsLib.G;
import musicEd.music.I;
import java.awt.Graphics;

public abstract class Mass extends Reaction.List implements I.Show {
    public Layer layer;

    public Mass(String layerName) {
        layer = Layer.byName.get(layerName);
        if (layer != null) {
            layer.add(this);
        } else {
            System.out.println("Bad layerName!" + layerName);
        }
    }

    public void deleteMass() {clearAll(); layer.remove(this);}

    // Explain this later.
    private int HashCode = G.rnd(100000000);
    public int hashCode(){return HashCode;}
    public boolean equals(Object o) {return this == o;}

    @Override
    public void show(Graphics g) {}
}
