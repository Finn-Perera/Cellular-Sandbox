package finnperera.sandbox.particles.placeable;

import finnperera.sandbox.Grid;
import finnperera.sandbox.particles.Flammable;
import finnperera.sandbox.particles.Particle;
import finnperera.sandbox.particles.ParticleType;
import javafx.scene.paint.Color;

public class Sand extends Particle implements Flammable {
    Color baseColour = Color.rgb(220,177,89);
    public Sand(int x, int y) {
        super(x, y);
        this.color = adjustColorRandomly(baseColour, 1, 0.1, 0.1);
        density = 1.6;
        friction = 0.3;
        flammability = 0.01;
        fuelVal = 1;
        hardness = 0.6;
    }

    @Override
    public void burn(Grid grid) {
        grid.setParticle(this.x, this.y, ParticleType.GLASS);
    }
}
