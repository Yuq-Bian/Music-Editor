package musicEd.music;

import java.awt.Graphics;

import musicEd.reaction.Gesture;
import musicEd.reaction.Reaction;

public class Rest extends Duration{
    public Staff staff;
    public Time time;
    public int line = 4;

    public Rest(Staff staff, Time time){
        this.staff = staff;
        this.time = time;
        addReaction(new Reaction("E-E"){// add flag to rest
            public int bid(Gesture gest) {
                int y = gest.vs.yM(), x1 = gest.vs.xL(), x2 = gest.vs.xH(), x = Rest.this.time.x;
                if(x1 > x || x2 < x) {return UC.noBid;}
                return Math.abs(y - Rest.this.staff.yLine(4));
            }
            public void act(Gesture gest) {
                Rest.this.incFlag();
                
            } 
        });

        addReaction(new Reaction("W-W"){// add flag to rest
            public int bid(Gesture gest) {
                int y = gest.vs.yM(), x1 = gest.vs.xL(), x2 = gest.vs.xH(), x = Rest.this.time.x;
                if(x1 > x || x2 < x) {return UC.noBid;}
                return Math.abs(y - Rest.this.staff.yLine(4));
            }
            public void act(Gesture gest) {
                Rest.this.decFlag();
                
            } 
        });

        addReaction(new Reaction("DOT"){
            @Override
            public int bid(Gesture gest) {
                int xr = Rest.this.time.x, yr = Rest.this.y();
                int x = gest.vs.xM(), y = gest.vs.yM();
                if(x < xr || x > xr + 40 || y < yr -40 || y > yr + 40){return UC.noBid;}
                return Math.abs(x-xr)+Math.abs(y-yr);
            }
            @Override
            public void act(Gesture gest) {
                Rest.this.cycleDot();
            }           
        });
    }
    public int y() {return staff.yLine(line);}
    
    @Override
    public void show(Graphics g) {
        int H = staff.H(), y = staff.yLine(line);
        Glyph glyph = Glyph.REST_W;
        if(nflag == -1) {glyph = Glyph.REST_H;}
        if(nflag == 0) {glyph = Glyph.REST_Q;}
        if(nflag == 1) {glyph = Glyph.REST_1F;}
        if(nflag == 2) {glyph = Glyph.REST_2F;}
        if(nflag == 3) {glyph = Glyph.REST_3F;}
        if(nflag == 4) {glyph = Glyph.REST_4F;}
        glyph.showAt(g, H, time.x, y);    
        
        int off = UC.restAugDotOffset, sp = UC.augDotSpacing;
        for(int i = 0; i < nDot; i++){
            g.fillOval(time.x + off + i * sp, y - 3 * H/2, H*2/3, H*2/3);
        }

    }

    
}
