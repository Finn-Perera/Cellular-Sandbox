package finnperera.sandbox.particles;

import javafx.scene.paint.Color;

public class Smoke extends Gas {

    Color baseColour = Color.DARKGRAY;
    public Smoke(int x, int y) {
        super(x, y);
        color = adjustColorRandomly(baseColour, 0.01, 0.01, 0.01);
        density = 1.15;
        flammability = 0;
        dispersionRate = 3;
    }
}
