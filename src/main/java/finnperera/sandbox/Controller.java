package finnperera.sandbox;

import finnperera.sandbox.particles.*;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Duration;

public class Controller {

    // CONTROLLER SHOULD BE CALLING METHODS ON GRID TO UPDATE, NOT UPDATING IN THE GRID FIRST THEN JUST CALLING THAT

    private static final int TICK_INTERVAL_M = 20; // milliseconds
    private static final int TICKS_PER_SECOND = 1000 / TICK_INTERVAL_M;
    private static final double TICK_INTERVAL_S = TICK_INTERVAL_M / 1000.0; // seconds
    private static final double GRAVITY = 9.8; // m/s^2
    private final Timeline tickTimeline;
    private Boolean running = false;
    private Grid grid;
    private GraphicsContext gc;
    private int rowCount;

    public Controller(Grid grid, GraphicsContext gc) {
        this.grid = grid;
        this.gc = gc;
        tickTimeline = new Timeline(new KeyFrame(Duration.millis(TICK_INTERVAL_M), event -> tick()));
        tickTimeline.setCycleCount(Animation.INDEFINITE);
        tickTimeline.play();
        this.rowCount = (int) Math.floor(this.grid.getGridLength() / this.grid.getWidth());
    }

    private void tick() {
        if (update()) {
            grid.render(gc);
        }
        //update();
    }

    public void flipSimRunning() {
        running = !running;
        if (running) {
            tickTimeline.play();
        } else {
            tickTimeline.stop();
        }
    }

    private boolean updateParticle(int index) {
        Particle particle = grid.getParticle(index);
        if (particle.hasMoved()) {
            particle.setMoved(false);
            return true;
        }
        return particle.run(grid, GRAVITY, TICK_INTERVAL_S);
    }

    private boolean update() {
        boolean updated = false;
        for (int row = this.rowCount - 1; row >= 0; row--) {
            int rowOffset = row * grid.getWidth();
            boolean leftToRight = Math.random() > 0.5;
            for (int i = 0; i < grid.getWidth(); i++) {
                int columnOffset = leftToRight ? i : -i - 1 + grid.getWidth();
                if (updateParticle(rowOffset + columnOffset)) {
                    updated = true;
                }
            }
        }

        return updated;
    }
}
