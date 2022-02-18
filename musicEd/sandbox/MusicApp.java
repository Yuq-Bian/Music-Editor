package musicEd.sandbox;

import musicEd.graphicsLib.G;
import musicEd.graphicsLib.Window;
import musicEd.music.UC;
import musicEd.music.Beam;
import musicEd.music.Glyph;
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
            public int bid(Gesture gest) {return 0;}
            public void act(Gesture gest) {new Page(gest.vs.yM()); this.disable();}
        });
    }
    //public static int[] xPoly = {100,200,200,100};
    //public static int[] yPoly = {50,70,80,60};
    //public static Polygon poly = new Polygon(xPoly, yPoly, 4);
    public void paintComponent(Graphics g) {
        G.fillBackground(g);
        Layer.ALL.show(g);
        g.setColor(Color.BLACK);
        Ink.BUFFER.show(g);
        /*int h = 8;
        int x1 = 100, x2 = 200; 
        Beam.setMasterBeam(x1, 100+G.rnd(100), x2, 100+G.rnd(100));
        g.drawLine(0, Beam.my1, x1, Beam.my1);
        Beam.drawBeamStack(g, 0, 1, x1, x2, h);
        g.setColor(Color.ORANGE);
        Beam.drawBeamStack(g, 1, 3, x1+10, x2-10, h);
        //g.fillPolygon(Beam.poly);
        /*if(Page.PAGE != null){
            int H = 32;
            Glyph.HEAD_QU.showAt(g, H, 200, Page.PAGE.yMargin.lo + 4*H);
            g.setColor(Color.RED);
            g.drawRect(200, Page.PAGE.yMargin.lo + 3*H, 24*H/10, 2*H);
        }*/
        //g.fillPolygon(poly);
        //poly.ypoints[3]++;
        
    }

    public void mousePressed(MouseEvent me) {Gesture.AREA.dn(me.getX(), me.getY()); repaint();}

    public void mouseDragged(MouseEvent me) {Gesture.AREA.drag(me.getX(), me.getY()); repaint();}

    public void mouseReleased(MouseEvent me) {Gesture.AREA.up(me.getX(), me.getY()); repaint();}
}
