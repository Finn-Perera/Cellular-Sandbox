package finnperera.sandbox.particles;

import javafx.scene.paint.Color;

public class TestParticle extends Solid{
    public TestParticle(int x, int y) {
        super(x, y);
        color = Color.RED;
        hardness = 1.1;
        flammability = 0;
        density = 0;
    }
}
