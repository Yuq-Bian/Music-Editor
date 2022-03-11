package musicEd.sandbox;

import musicEd.graphicsLib.G;
import musicEd.graphicsLib.Window;
import musicEd.music.UC;
import musicEd.music.Page;
import musicEd.reaction.Gesture;
import musicEd.reaction.Ink;
import musicEd.reaction.Layer;
import musicEd.reaction.Reaction;

import java.awt.*;
import java.awt.event.*;

public class MusicApp extends Window {
    static {
        new Layer("BACK");
        new Layer("NOTE");
        new Layer("FORE");
    }

    public MusicApp() {
        super("Music!", UC.windowWidth, UC.windowHeight);
        Reaction.initialReactions.addReaction(new Reaction("E-E") { // Create a page.
            @Override
            public int bid(Gesture gest) {
                return 0;
            }

            @Override
            public void act(Gesture gest) {
                new Page(gest.vs.yM());
                this.disable();
            }
        });
    }

    public void paintComponent(Graphics g) {
        G.fillBackground(g);
        Layer.ALL.show(g);
        g.setColor(Color.BLACK);
        Ink.BUFFER.show(g);
    }

    public void mousePressed(MouseEvent me) {
        Gesture.AREA.dn(me.getX(), me.getY());
        repaint();
    }

    public void mouseDragged(MouseEvent me) {
        Gesture.AREA.drag(me.getX(), me.getY());
        repaint();
    }

    public void mouseReleased(MouseEvent me) {
        Gesture.AREA.up(me.getX(), me.getY());
        repaint();
    }

}
