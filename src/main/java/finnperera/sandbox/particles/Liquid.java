package finnperera.sandbox.particles;

import finnperera.sandbox.Grid;

public class Liquid extends Particle {

    public Liquid(int x, int y) {
        super(x, y);
        density = 1;
        friction = 0.2;
        dispersionRate = 3;
    }

    @Override
    public boolean run(Grid grid, double gravity, double tickRate) {

        boolean s = simpleGravity(grid, this);
        boolean sinks = applySink(grid, this);
        boolean sm = applyVerticalSideMovement(grid, this, true);
        boolean m = applySideMovement(grid, this);

        moved = false;

        return s || m || sinks || sm;
    }
}
