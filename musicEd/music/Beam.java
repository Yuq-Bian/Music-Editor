package musicEd.music;

import musicEd.reaction.Mass;
import java.awt.*;
public class Beam extends Mass{
    public static Polygon poly;
    static {
        int[] foo = {0,0,0,0};
        poly = new Polygon(foo,foo,4);
    }
    public Stem.List stems = new Stem.List();
    public static int mx1,my1,mx2,my2;

    public Beam(Stem first,Stem last){
        super("NOTE");
        addStem(first);
        addStem(last);


    }
    public Stem first(){return stems.get(0);}
    public Stem last(){return stems.get(stems.size()-1);}
    public void deleteBeam(){//remove beam from layer
        for(Stem s:stems){s.beam=null;}
        deleteMass();
    }
    public void addStem(Stem s){
        if(s.beam==null) {
            stems.add(s);
            s.beam = this;
            s.nflag = 1;
            stems.sort();
        }
    }
    public void setMasterBeam(){
        mx1 = first().x();
        my1 = first().yBeamEnd();
        mx2 = last().x();
        my2 = last().yBeamEnd();
    }
    public void show(Graphics g){g.setColor(Color.BLACK);drawBeamGroup(g);}
    public void drawBeamGroup(Graphics g){
        setMasterBeam();
        Stem firstStem = first();
        int H = firstStem.staff.H(), sH = firstStem.isUp?H:-H; // signed H needed for beam stack
        int nPrev = 0, nCur = firstStem.nflag, nNext = stems.get(1).nflag;
        int px;// prev stem x loc
        int cx = firstStem.x();// current stem x loc
        int bx = cx + 3*H; // forward leaning beamlet
        if(nCur>nNext){drawBeamStack(g, nNext, nCur, cx, bx, sH);}
        for(int cur = 1; cur < stems.size(); cur++){
            Stem sCur = stems.get(cur); px = cx; cx = sCur.x();
            nPrev = nCur; nCur = nNext; nNext = (cur<stems.size()-1)?stems.get(cur+1).nflag:0;
            int nBack = Math.min(nPrev, nCur);
            drawBeamStack(g, 0, nBack, px, cx, sH);//these are beams back to previous stem
            if(nCur>nPrev && nCur>nNext){// need beamlet
                if(nPrev<nNext){// beamlets lean towards side of more beams
                    bx = cx + 3*H;
                    drawBeamStack(g, nNext, nCur, cx, bx, sH);
                }else{
                    bx = cx - 3*H;
                    drawBeamStack(g, nPrev, nCur, bx, cx, sH);
                }
            }
        }
    }
    public void removeStem(Stem s){
        if(s == first() || s == last()){deleteBeam();} else {stems.remove(s); stems.sort();}
    }
    


    public static int yOfX(int x, int x1, int y1, int x2, int y2){
        int dy = y2 - y1, dx = x2 -x1;
        return (x - x1) * dy/dx + y1;
    }
    
    public static int yOfX(int x){return yOfX(x,mx1,my1,mx2,my2);}
    public static void setMasterBeam(int x1, int y1, int x2, int y2){
        mx1 = x1;
        my1 = y1;
        mx2 = x2;
        my2 = y2;
    }
    public static boolean verticalLineCrossesSegment(int x, int y1, int y2, int bx, int by, int ex, int ey){
        if(x<bx || x>ex){return false;}
        int y = yOfX(x, bx, by, ex, ey);
        if(y1<y2){return y1<y && y<y2;}else{return y2<y && y<y1;}
    }
    public static void setPoly(int x1, int y1, int x2, int y2, int h){
        int[] a = poly.xpoints;
        a[0]=x1; a[1]=x2; a[2]=x2; a[3]=x1;
        a = poly.ypoints;
        a[0] = y1; a[1]=y2; a[2]=y2+h; a[3]=y1+h;
    }
    public static void drawBeamStack(Graphics g, int n1, int n2, int x1, int x2, int h){
        int y1 = yOfX(x1), y2 = yOfX(x2);
        for(int i = n1; i < n2; i++){
            setPoly(x1, y1+i*2*h, x2, y2+i*2*h, h);
            g.fillPolygon(poly);
        }
    }

    

}
