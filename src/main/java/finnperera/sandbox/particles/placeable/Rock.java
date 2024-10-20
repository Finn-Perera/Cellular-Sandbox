package finnperera.sandbox.particles.placeable;

import finnperera.sandbox.Grid;
import finnperera.sandbox.particles.Solid;
import javafx.scene.paint.Color;

public class Rock extends Solid {
    Color baseColour = Color.rgb(45,44,44);
    public Rock(int x, int y) {
        super(x, y);
        this.color = adjustColorRandomly(baseColour, 1, 0.1, 0.1);
        friction = 0.7;
        vx = 0;
        vy = 0;
        hardness = 0.99;
        falling = false;
    }

}
