package finnperera.sandbox.particles.placeable;

import finnperera.sandbox.Grid;
import finnperera.sandbox.particles.Flammable;
import finnperera.sandbox.particles.Liquid;
import finnperera.sandbox.particles.Particle;
import finnperera.sandbox.particles.ParticleType;
import finnperera.sandbox.particles.placeable.Empty;
import javafx.scene.paint.Color;

public class Oil extends Liquid implements Flammable {

    Color baseColour = Color.rgb(255,253,100);
    public Oil(int x, int y) {
        super(x, y);
        color = baseColour;
        density = 0.9;
        dispersionRate = 3;
        flammability = 0.05;
        fuelVal = 200;
    }

    @Override
    public void burn(Grid grid) {
        if (grid.isValidPosition(this.x, this.y - 1)) {
            Particle above = grid.getParticle(this.x, this.y - 1);
            if (above instanceof Empty) {
                grid.setParticle(this.x, this.y - 1, ParticleType.SMOKE);
            }
        }
    }
}
