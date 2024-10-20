package finnperera.sandbox.particles.placeable;

import finnperera.sandbox.Grid;
import finnperera.sandbox.particles.Flammable;
import finnperera.sandbox.particles.Liquid;
import finnperera.sandbox.particles.Particle;
import finnperera.sandbox.particles.ParticleType;
import javafx.scene.paint.Color;

public class Water extends Liquid implements Flammable {

    public Water(int x, int y) {
        super(x, y);
        this.color = Color.BLUE;
        density = 1;
        friction = 0.2;
        dispersionRate = 5;
        flammability = 0.1;
        fuelVal = 3;
    }

    @Override
    public void burn(Grid grid) {
        // turn into steam?
        if (grid.isValidPosition(this.x, this.y - 1)) {
            Particle above = grid.getParticle(this.x, this.y - 1);
            if (above instanceof Empty) {
                grid.setParticle(this.x, this.y - 1, ParticleType.STEAM);
            }
        }
    }
}
