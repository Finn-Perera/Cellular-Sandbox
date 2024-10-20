package finnperera.sandbox.particles.placeable;

import finnperera.sandbox.Grid;
import finnperera.sandbox.particles.Flammable;
import finnperera.sandbox.particles.Particle;
import finnperera.sandbox.particles.ParticleType;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Fire extends Particle {

    // burn time counter?
    // material it is burning? each material contains a flammability or fuel number? Closer to max of fuel number
    // more likely to burn out

    // maybe include a burn function in each particle describing how they burn (smoke/steam)

    private int burningCounter = 0;
    private int fuelNum = 3; // assume 4, change when spreading to materials you can check

    public Fire(int x, int y, int fuelNum) {
        super(x, y);
        color = pickColour();
        flammability = 0;
        this.fuelNum = fuelNum;
    }

    public Fire(int x, int y) {
        super(x, y);
    }

    public static boolean spreadFire(Grid grid, Particle particle) {
        // check if flammable material and their fuel cost
        // maybe pick random number 0 - 2? per fire particle that spreads on a 50/50 basis or 30/70
        // want to expand to any surrounding flammable

        ArrayList<Particle> neighbours = grid.getNeighbourParticles(particle); // make sure not empty?
        Collections.shuffle(neighbours); // not necessary if no cap on fire num

        for (Particle p : neighbours) {
            if (p.getFlammability() == 0) {
                continue;
            }

            if (Math.random() < (p.getFlammability() * 0.3)) {
                // burn?

                if (p instanceof Flammable flammable) {
                    flammable.burn(grid);
                }
                grid.setParticle(p.getX(), p.getY(),
                        new Fire(p.getX(), p.getY(), p.getFuelValue())); // This won't save state of particle (velocity)
            }
            // maybe stop after max num of fires set?
        }

        return false;
    }

    private Color pickColour() {
        //Color[] colourArray = new Color[]{Color.RED, Color.YELLOW, Color.ORANGE};
        int rand = new Random().nextInt(10);
        if (rand < 5) {
            return Color.ORANGE;
        } else if (rand < 8) {
            return Color.YELLOW;
        } else {
            return Color.RED;
        }
    }

    private void extinguish(Grid grid, Particle particle) {
        // won't store state
        grid.setParticle(particle.getX(), particle.getY(), ParticleType.EMPTY);
    }


    @Override
    public boolean run(Grid grid, double gravity, double tickRate) {
        if (burningCounter > fuelNum) {
            extinguish(grid, this); // might cause an error if particle is deleted?
        }

        boolean spread = spreadFire(grid, this);
        burningCounter++;

        return spread;
    }
}
