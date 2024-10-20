package finnperera.sandbox.particles.placeable;

import finnperera.sandbox.Grid;
import finnperera.sandbox.particles.Flammable;
import finnperera.sandbox.particles.Particle;
import finnperera.sandbox.particles.ParticleType;
import finnperera.sandbox.particles.Solid;
import javafx.scene.paint.Color;

public class Wood extends Solid implements Flammable {
    public Wood(int x, int y) {
        super(x, y);
        color = adjustColorRandomly(Color.SADDLEBROWN, 0.1, 0.1, 0.1);
        flammability = 0.1;
        fuelVal = 100;
        hardness = 0.6;
    }

    @Override
    public void burn(Grid grid) { // I can implement this better
        if (grid.isValidPosition(this.x, this.y - 1)) {
            Particle above = grid.getParticle(this.x, this.y - 1);
            if (above instanceof Empty) {
                grid.setParticle(this.x, this.y - 1, ParticleType.SMOKE);
            }
        }
    }
}
