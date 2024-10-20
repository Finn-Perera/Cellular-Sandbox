package finnperera.sandbox.particles;

import javafx.scene.paint.Color;

public class Glass extends Particle {

    public Glass(int x, int y) {
        super(x, y);
        color = Color.LIGHTBLUE;
        flammability = 0;
        density = 1.6;
        hardness = 0.7;
    }
}
