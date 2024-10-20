package finnperera.sandbox.particles;

import finnperera.sandbox.Grid;

import java.util.Optional;

public class Gas extends Particle {
    int diffuseLife = 50;
    int diffuseCount = 0;

    public Gas(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean simpleGravity(Grid grid, Particle p) {
        // Only moves to the furthest empty space.
        if (moved) {
            return false;
        }

        Optional<Particle> targetParticle = furthestEmptyVertically(grid, 1, false);

        if (targetParticle.isEmpty()) return false;

        moved = true;
        grid.swapParticles(p, targetParticle.get());
        return true; // maybe return moved?
    }

    protected boolean checkDiffusion(Grid grid, Particle p, boolean moved) {
        if (diffuseCount > diffuseLife) {
            diffuse(grid, p);
            return true;
        } else if (!moved) {
            diffuseCount++;
        } else {
            diffuseCount = 0;
        }
        return false;
    }

    protected void diffuse(Grid grid, Particle p) {
        grid.setParticle(p.getX(), p.getY(), ParticleType.EMPTY);
    }


    @Override
    public boolean run(Grid grid, double gravity, double tickRate) {
        boolean movedFloat = simpleGravity(grid, this);
        boolean sideMovement = applyVerticalSideMovement(grid, this, false);
        boolean dispersion = applySideMovement(grid, this);

        return movedFloat || sideMovement || dispersion;
    }
}
