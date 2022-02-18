package musicEd.music;

import musicEd.reaction.Gesture;
import musicEd.reaction.Mass;
import musicEd.reaction.Reaction;

import java.awt.*;
import java.util.ArrayList;

public class Head extends Mass implements Comparable<Head>{
    public Staff staff;
    public int line;//line -- y coordinate
    public Time time;
    public Stem stem = null;
    public boolean wrongSide = false;
    public Glyph forcedGlyph = null;
    
    public Head(Staff staff, int x, int y){
        super("NOTE");
        this.staff = staff;
        time = staff.sys.getTime(x);
        line = staff.lineOff(y); // avoid negetive division 
        time.heads.add(this);
        
        addReaction(new Reaction("S-S"){
            public int bid(Gesture g){
                int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yH();
                int W = Head.this.w(), hy = Head.this.y();
                if(y1 > y || y2 < y){return UC.noBid;} // heads not in y range reject this gesture
                int hl = Head.this.time.x, hr = hl + W; // left and right side of Head.
                if(x < hl-W || x > hr+W){return UC.noBid;} // must be reasonably close to the head.
                if(x < (hl+W/2)){return hl-x;}
                if(x > (hr-W/2)){return x-hr;}
                return UC.noBid;
            }
            public void act(Gesture g){
                int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yH(); // gesture locations
                Staff staff = Head.this.staff; // Head parameters
                Time t = Head.this.time;
                int W = Head.this.w();
                boolean up = x > (t.x+ W/2); // 
                if(Head.this.stem == null){ // winner of bid gets to choose between stem or unStem action
                    //t.stemHeads(staff, up, y1,y2); // staff and up needed to create the stem
                    Stem.getStem(staff, time, y1, y2, up);
                }else{
                    t.unStemHeads(y1,y2);
                }
            }
        });

        addReaction(new Reaction("DOT"){
            @Override
            public int bid(Gesture gest) {
                int xh = Head.this.x(),yh = Head.this.y(),w = Head.this.w(), H = Head.this.staff.H();
                int x = gest.vs.xM(), y = gest.vs.yM();
                if(x < xh || x > xh+2*w || y < yh - H || y > yh+H){return UC.noBid;}
                return Math.abs(xh+w-x)+Math.abs(yh-y);
            }
            @Override
            public void act(Gesture gest) {
                if(Head.this.stem!=null){
                    Head.this.stem.cycleDot();
                }
            }
        });

        addReaction(new Reaction("S-N"){ // delete head
            public int bid(Gesture g){
                int w2 = w()/2, h = staff.H();
                int x = g.vs.xM(), xHead = x() + w2, dx = Math.abs(x-xHead);
                if(dx > w2){return UC.noBid;}
                int y = g.vs.yL(), yHead =y(), dy = Math.abs(y-yHead);
                if(dy > h){return UC.noBid;}
                return dx+dy;
            }
            public void act(Gesture g){
                Head.this.deleteHead();
            }
          }); 
    }

    public void show(Graphics g){
        int H = staff.H();
        Glyph glyph = forcedGlyph != null ? forcedGlyph : normalGlyph();
        System.out.println(stem);
        g.setColor(stem == null ? Color.ORANGE : Color.BLACK); 
        glyph.showAt(g, H, x(), y());
        if(stem!=null){
            int off = UC.restAugDotOffset;
            int sp = UC.augDotSpacing;
            for(int i = 0; i < stem.nDot; i++){
                g.fillOval(time.x+off+i*sp, y()-3*H/2, 2*H/3, 2*H/3);
            }
            
        }
        //g.setColor(wrongSide?Color.GREEN:Color.BLUE);
        //if(stem!= null && stem.heads.size() != 0 && this == stem.firstHead()) {g.setColor(Color.RED);}
    }
    public Glyph normalGlyph(){
        if(stem == null){return Glyph.HEAD_QU;}
        if(stem.nflag == -1) {return Glyph.HEAD_HALF;}
        if(stem.nflag == -2) {return Glyph.HEAD_WH;}
        return Glyph.HEAD_QU;
    }
    public int h(){return 24 * staff.H() / 10;}
    public int w(){return 24 * staff.H() / 10;}
    public int y(){return staff.yLine(line);}
    public int x(){
        int res = time.x;
        if(wrongSide) {
            res += (stem != null && stem.isUp) ? w() : -w();
        }
        return res;
    }
    // public void delete(){
    //     time.heads.remove(this);
    // }
    public void unStem(){
        if(stem != null){  // no need to unStem a head that is already unStemed
          stem.heads.remove(this); // get out of old stem
          if(stem.heads.size() == 0){stem.deleteStem();}else{stem.setWrongSides();} // old stem vasishes if it becomes empty
          stem = null; // this head now has no stem
          wrongSide = false; // so can't possibly be on the wrong side
        }
    }
    public void joinStem(Stem s){
        if(stem != null){unStem();} // make sure that this head is NOT on some other stem..
        s.heads.add(this); // ..before it joins the Stem's heads list
        stem = s; // reference your stem - this head now has a stem.
    } 

    public void deleteHead(){
        unStem(); // remove head from stem which could delete stem
        time.removeHead(this);
        deleteMass(); // remove from the layers
      }

    


//-----------------List---------------------------------
    public static class List extends ArrayList<Head>{}



//------------------------------------------------------
    @Override
    public int compareTo(Head h) {
        return (staff.iStaff != h.staff.iStaff)? staff.iStaff-h.staff.iStaff : line-h.line;
    }
    
    
}
