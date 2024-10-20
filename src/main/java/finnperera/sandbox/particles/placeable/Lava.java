package finnperera.sandbox.particles.placeable;

import finnperera.sandbox.Grid;
import finnperera.sandbox.particles.Flammable;
import finnperera.sandbox.particles.Liquid;
import finnperera.sandbox.particles.Particle;
import finnperera.sandbox.particles.ParticleType;
import javafx.scene.paint.Color;

import java.util.Optional;

public class Lava extends Liquid {

    public Lava(int x, int y) {
        super(x, y);
        density = 1.6;
        dispersionRate = 1;
        hardness = 1.1; // can't be melted through
        flammability = 0;
        color = Color.ORANGERED;
    }

    // Wrong way to do things? Maybe melt all particles around as it passes?
    @Override
    public boolean applySink(Grid grid, Particle current) {
        if (moved) return false;

        if (!grid.isValidPosition(current.getX(), current.getY() + 1)) { // change floating number? base on density?
            return false;
        }

        Particle below = grid.getParticle(current.getX(), current.getY() + 1);

        if (below instanceof Water water) {
            moved = true;
            int waterX = water.getX();
            int waterY = water.getY();
            grid.replaceParticle(current, water);
            grid.setParticle(waterX, waterY, ParticleType.ROCK);
        } else if (isMeltable(below)) {
            moved = true;
            if (below instanceof Flammable flammable) {
                flammable.burn(grid);
            }
            grid.replaceParticle(current, below);
        }

        return moved;
    }

    @Override
    public boolean applySideMovement(Grid grid, Particle p) {
        if (moved) return false;
        // want to check a side to see if you can move to it
        // get valid random move left or right
        boolean left = grid.getNextRandBool();
        int moveBy;

        if (left && grid.isValidPosition(p.getX() - 1, p.getY())) {
            // move left if empty
            moveBy = -1;

        } else if (!left && grid.isValidPosition(p.getX() + 1, p.getY())) {
            // move right if empty
            moveBy = 1;
        } else if (left && grid.isValidPosition(p.getX() + 1, p.getY())) {
            // Can't move left but can move right
            moveBy = 1;
        } else if (!left && grid.isValidPosition(p.getX() - 1, p.getY())) {
            // Cant move right but can move left
            moveBy = -1;
        } else {
            // Can't move either way
            return false;
        }

        Optional<Particle> furthest = disperse(grid, moveBy);

        if (furthest.isEmpty()) {
            return false;
        }

        Particle furthestAsPart = furthest.get();

        if (furthestAsPart instanceof Water water) {
            moved = true;
            int waterX = water.getX();
            int waterY = water.getY();
            grid.replaceParticle(furthestAsPart, water);
            grid.setParticle(waterX, waterY, ParticleType.ROCK);
        } else if (isMeltable(furthestAsPart)) {
            moved = true;
            if (furthestAsPart instanceof Flammable flammable) {
                flammable.burn(grid);
            }
            grid.replaceParticle(this, furthestAsPart);
        }

        return moved;
    }

    @Override
    public Optional<Particle> disperse(Grid grid, int direction) {
        Particle furthestValid = null;
        for (int i = 1; i <= dispersionRate; i++) {
            if (!grid.isValidPosition(this.x + (i * direction), this.y)) {
                continue;
            }

            Particle next = grid.getParticle(this.x + (i * direction), this.y);
            if (canPass(this, next) || isMeltable(next)) {
                furthestValid = next;
            } else {
                break; // Don't like this but it works?
            }
        }
        if (furthestValid == null) return Optional.empty();

        return Optional.of(furthestValid);
    }

    private boolean isMeltable(Particle particle) {
        return Math.random() > particle.getHardness();
        // get random. if > hardness -> melt
    }

    private boolean setFireToSurroundings(Grid grid, Particle p) {
        return Fire.spreadFire(grid, p);
    }

    @Override
    public boolean run(Grid grid, double gravity, double tickRate) {
        boolean spreadFire = setFireToSurroundings(grid, this);
        return super.run(grid, gravity, tickRate) || spreadFire;
    }
}
