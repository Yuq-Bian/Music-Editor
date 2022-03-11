package musicEd.reaction;

import musicEd.graphicsLib.G;
import musicEd.music.I;

public abstract class Mass extends Reaction.List implements I.Show {
    public Layer layer;
    private int HashCode = G.rnd(100000000);

    public Mass(String layerName) {
        layer = Layer.byName.get(layerName);
        if (layer != null) {
            layer.add(this);
        } else {
            System.out.println("Bad layerName!" + layerName);
        }
    }

    public void deleteMass() {
        clearAll();
        layer.remove(this);
    }

    public int hashCode() {
        return HashCode;
    }

    public boolean equals(Object o) {
        return this == o;
    }

}
