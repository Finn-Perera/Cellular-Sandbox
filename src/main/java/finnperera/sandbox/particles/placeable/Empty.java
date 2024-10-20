package finnperera.sandbox.particles.placeable;

import finnperera.sandbox.Grid;
import finnperera.sandbox.particles.Particle;
import javafx.scene.paint.Color;

public class Empty extends Particle {
    public Empty(int x, int y) {
        super(x, y);
        this.color = Color.BLACK;
        density = 0.07;
        flammability = 0.02;
        fuelVal = 25;
    }

    @Override
    public boolean run(Grid grid, double gravity, double tickRate) {
        return false;
    }
}
