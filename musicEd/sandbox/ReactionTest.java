package musicEd.sandbox;

import musicEd.graphicsLib.G;
import musicEd.graphicsLib.Window;
import musicEd.music.UC;
import musicEd.reaction.*;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.*;

public class ReactionTest extends Window {
    static {
        new Layer("BACK");
        new Layer("FORE");
    }

    public ReactionTest() {
        super("ReactionTest!", UC.windowWidth, UC.windowHeight);
        Reaction.initialReactions.addReaction(new Reaction("SW-SW") {
            @Override
            public int bid(Gesture gest) {
                return 0;
            }

            @Override
            public void act(Gesture gest) {
                new Box(gest.vs);
            }
        });
    }

    public void paintComponent(Graphics g) {
        G.fillBackground(g);
        g.setColor(Color.BLUE);
        Ink.BUFFER.show(g);
        Layer.ALL.show(g);
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

    // ----------------------------------Box-----------------------------------
    public static class Box extends Mass {
        public G.VS vs;
        public Color c = G.rndColor();
        public boolean isCircle = false;

        public Box(G.VS vs) {
            super("BACK");
            this.vs = vs;

            addReaction(new Reaction("S-S") {
                @Override
                public int bid(Gesture gest) {
                    int x = gest.vs.xM(), y = gest.vs.yL();
                    if (!Box.this.vs.hit(x, y)) {
                        return UC.noBid;
                    } // "this" is this Reaction's Box. Read Oracle docs..
                    return Math.abs(x - Box.this.vs.xM());
                }

                @Override
                public void act(Gesture gest) {
                    Box.this.deleteMass();
                }
            });
        }

        @Override
        public void show(Graphics g) {
            if (isCircle) {
                g.setColor(c);
                g.fillOval(vs.loc.x, vs.loc.y, vs.size.x, vs.size.y);
            } else {
                vs.fill(g, c);
            }
        }
    }
    
}
