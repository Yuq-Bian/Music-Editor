package musicEd.music;

import musicEd.reaction.Mass;

import java.awt.*;

public abstract class Duration extends Mass {
    public int nflag = 0, nDot = 0;

    public Duration() {
        super("NOTE");
    }

    public abstract void show(Graphics g);

    public void incFlag() {
        if (nflag < 4) {
            nflag++;
        }
    }

    public void decFlag() {
        if (nflag > -2) {
            nflag--;
        }
    }

    public void cycleDot() {
        nDot++;
        if (nDot > 3) {
            nDot = 0;
        }
    }

}
