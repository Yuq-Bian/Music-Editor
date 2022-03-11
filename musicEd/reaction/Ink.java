package musicEd.reaction;

import musicEd.music.*;
import musicEd.graphicsLib.G;
import musicEd.graphicsLib.G.BBox;

import java.awt.*;
import java.util.ArrayList;

public class Ink implements I.Show {
    public Norm norm;
    public G.VS vs;
    public static G.VS TEMP = new G.VS(100, 100, 100, 100);
    public static Buffer BUFFER = new Buffer(); // this is the only guy that creates a Buffer
    public static final int K = UC.normSampleSize;

    public Ink() {
        norm = new Norm();
        vs = BUFFER.bbox.getNewVS();
    }

    @Override
    public void show(Graphics g) {
        g.setColor(UC.inkColor);
        norm.drawAt(g, vs);
    }

    // ------------------------Ink.Buffer---------------------------------
    public static class Buffer extends G.PL implements I.Show, I.Area {
        public static final int MAX = UC.inkBufferMax;
        public int n; // how many points are actually in the buffer
        public BBox bbox = new BBox();

        private Buffer() {
            super(MAX);
        } // singleton (only want one Ink.Buffer) so this is private

        public void add(int x, int y) {
            if (n < MAX) {
                points[n++].set(x, y);
            }
            bbox.add(x, y);
        }

        public void subSample(G.PL pl) {
            int K = pl.size();
            for (int i = 0; i < K; i++) {
                // linear interpretation (copy the first to the first, last to the last, the
                // rest are proportionately)
                pl.points[i].set(this.points[i * (n - 1) / (K - 1)]);
            }
        }

        public void clear() {
            n = 0;
        } // clear out the buffer

        public void show(Graphics g) {
            drawN(g, n);
        }

        public void dn(int x, int y) {
            clear();
            bbox.set(x, y);
            add(x, y);
        }

        public void up(int x, int y) {
        } // do nothing

        public void drag(int x, int y) {
            add(x, y);
        }

        public boolean hit(int x, int y) {
            return true;
        }
    }

    // ------------------------Ink.List-----------------------------------
    public static class List extends ArrayList<Ink> implements I.Show {
        public void show(Graphics g) {
            for (Ink ink : this) {
                ink.show(g);
            }
        }
    }

    // ------------------------Ink.Norm-----------------------------------
    public static class Norm extends G.PL {
        public static final int N = UC.normSampleSize, MAX = UC.normCoordMax;
        public static final G.VS NCS = new G.VS(0, 0, MAX, MAX);

        public Norm() {
            super(N);
            BUFFER.subSample(this);
            G.V.T.set(BUFFER.bbox, NCS);
            this.transform();
        }

        public void drawAt(Graphics g, G.VS vs) {
            G.V.T.set(NCS, vs);
            for (int i = 1; i < N; i++) {
                g.drawLine(points[i - 1].tx(), points[i - 1].ty(), points[i].tx(), points[i].ty());
            }
        }

        public int dist(Norm n) {
            int res = 0;
            for (int i = 0; i < N; i++) {
                int dx = points[i].x - n.points[i].x;
                int dy = points[i].y - n.points[i].y;
                // delta x and y are the distance between the two points between this and Norm n
                res += dx * dx + dy * dy;
            }
            return res;
        }

        public void blend(Norm norm, int nBlend) {
            for (int i = 0; i < N; i++) {
                points[i].blend(norm.points[i], nBlend);
            }
        }
    }

}
