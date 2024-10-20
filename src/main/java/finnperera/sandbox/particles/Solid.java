package finnperera.sandbox.particles;

import finnperera.sandbox.Grid;

public abstract class Solid extends Particle{

    public Solid(int x, int y) {
        super(x, y);
        density = 0;
        vx = 0;
        vy = 0;
        falling = false;
    }

    @Override
    public boolean run(Grid grid, double gravity, double tickrate) {
        return false;
    }
}
