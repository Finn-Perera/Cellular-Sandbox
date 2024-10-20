package finnperera.sandbox.particles;

import finnperera.sandbox.Grid;
import javafx.scene.paint.Color;

public class Steam extends Gas {

    public Steam(int x, int y) {
        super(x, y);
        color = Color.LIGHTGRAY;
        density = 0.9;
        flammability = 0;
        dispersionRate = 4;
    }

    @Override
    public boolean run(Grid grid, double gravity, double tickRate) {
        boolean movedFloat = simpleGravity(grid, this);
        boolean sideMovement = applyVerticalSideMovement(grid, this, false);
        boolean dispersion = applySideMovement(grid, this);
        boolean diffused = checkDiffusion(grid, this, moved);

        return movedFloat || sideMovement || dispersion || diffused;
    }
}
