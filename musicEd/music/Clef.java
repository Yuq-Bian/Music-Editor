package musicEd.music;

import java.awt.Graphics;

public class Clef {
    public Glyph clef;
    public static Clef G = new Clef(Glyph.CLEF_G);
    public static Clef F = new Clef(Glyph.CLEF_F);

    public Clef(Glyph g) {
        this.clef = g;
    }

    public void showAt(Graphics g, int x, int yTop, int h) {
        clef.showAt(g, h, x, yTop + 4 * h);
    }
    
}
