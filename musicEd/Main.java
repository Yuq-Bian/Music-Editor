package musicEd;

import musicEd.graphicsLib.*;
import musicEd.sandbox.*;

public class Main {
    public static void main(String [] args) {
        System.out.println("Music Editor!");
         Window.PANEL = new ShapeTrainer();
         //Window.PANEL = new ReactionTest();
        //Window.PANEL = new MusicApp();
        Window.launch();
    }
}
