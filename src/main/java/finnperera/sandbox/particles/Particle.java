package finnperera.sandbox.particles;

import finnperera.sandbox.particles.placeable.Empty;
import javafx.scene.paint.Color;
import finnperera.sandbox.*;

import java.util.Optional;
import java.util.Random;

public abstract class Particle {
    public static double DEFAULT_FRICTION = 0.5;
    // Array position
    protected int x;
    protected int y;
    // Actual position
    protected double fx;
    protected double fy;
    // Velocity
    protected double vx;
    protected double vy;
    protected Color color;
    protected double density;
    protected boolean moved;
    protected double friction;
    protected boolean falling;
    protected int dispersionRate;
    protected double flammability; // 0 - 1
    protected double hardness;
    protected int fuelVal; // how long it burns

    public Particle(int x, int y) {
        moved = false;
        this.x = x;
        this.y = y;
        vx = 0;
        vy = 0;
        fx = x;
        fy = y;
        falling = true;
        dispersionRate = 0;
        flammability = 0;
        fuelVal = 2;
        hardness = 0;
        friction = DEFAULT_FRICTION;
    }

    public boolean simpleGravity(Grid grid, Particle p) { // Only drops to the furthest empty space.
        if (moved) {
            return false;
        }

        Optional<Particle> targetParticle = furthestEmptyVertically(grid, 1, true); //

        if (targetParticle.isEmpty()) return false;

        moved = true;
        grid.swapParticles(this, targetParticle.get());
        return true; // maybe return moved?
    }

    protected Optional<Particle> furthestEmptyVertically(Grid grid, int velocity, boolean down) {  // Velocity? Int?
        Particle furthestValid = null;
        int direction = down ? 1 : -1;
        for (int i = 1; i <= velocity; i++) {
            if (!grid.isValidPosition(this.x, this.y + i * direction)) {
                continue;
            }

            Particle next = grid.getParticle(this.x, this.y + i * direction);
            if (next instanceof Empty) {
                furthestValid = next;
            } else {
                break; // Don't like this but it works?
            }
        }

        if (furthestValid == null) return Optional.empty();

        return Optional.of(furthestValid);
    }

    // Obsolete
    @Deprecated
    public boolean applyGravity(Grid grid, Particle p, double gravity, double tickrate) {
        if (moved) {
            return false;
        }
        boolean found = false;

        // This is a hack
        if (grid.isValidPosition(p.x, p.y + 1)) {
            if (grid.getParticle(p.x, p.y + 1) instanceof Empty) {
                falling = true;
            }
        }

        if (p.falling) {
            p.setVelocityY(p.getVelocityY() + gravity * tickrate);
            System.out.println(p.getVelocityY());
        }

        try {
            for (int i = (int) Math.ceil(p.getVelocityY()); i >= 1 && !found; i--) {
                // Searches furthest to closest
                Particle below;
                if (grid.isValidPosition(p.x, p.y + i)) {
                    below = grid.getParticle(p.x, p.y + i);
                } else {
                    continue; // This would work with furthest to closest?
                }

                if (canPass(p, below)) {
                    found = true;
                    grid.swapParticles(p, below);
                    p.falling = true;
                    p.moved = true;
                    //System.out.println("Moved: x " + p.x + " y "  + p.y + " To: " + "x " +  below.x + " y " + below.y );
                }
            }

            if (!found) {
                p.falling = false;
                p.setVelocityY(minimiseVelocity(p.getVelocityY() * 0.5)); // Example: reduce velocity by half
                System.out.println("!found " + p.getVelocityY());
            }
        } catch (NullPointerException npe) {
            // Tried to access a particle out of the grid
            System.out.println("Particle out of grid");
            p.moved = false;
        }
        return found;
    }

    private double minimiseVelocity(double velocity) {
        if ((velocity < 0.1 && velocity >= 0) || (velocity > -0.1 && velocity <= 0)) {
            return 0;
        }

        return velocity;
    }

    // might need to include falling tag
    public boolean applyVerticalSideMovement(Grid grid, Particle p, boolean down) {
        if (moved) {
            return false;
        }

        int direction = down ? 1 : -1;
        // maybe add a liquid check to see if it goes down when it can
        Optional<Particle> right; // up/down
        Optional<Particle> left; // up/down

        right = grid.isValidPosition(p.x + 1, p.y + direction) ?
                Optional.ofNullable(grid.getParticle(p.x + 1, p.y + direction)) : Optional.empty();
        left = grid.isValidPosition(p.x - 1, p.y + direction) ?
                Optional.ofNullable(grid.getParticle(p.x - 1, p.y + direction)) : Optional.empty();

        boolean validR = right.isPresent() && canPass(p, right.get());
        boolean validL = left.isPresent() && canPass(p, left.get());


        if (!(validL || validR)) {
            return false;
        }

        moved = true;

        if (validL && validR) {
            if (grid.getNextRandBool()) {
                grid.swapParticles(p, left.get());
            } else {
                grid.swapParticles(p, right.get());
            }
        } else if (validL) {
            grid.swapParticles(p, left.get());
        } else {
            grid.swapParticles(p, right.get());
        }
        return true;
    }

    // Can refactor
    private Optional<Integer> pickSideVelocity(Grid grid, Particle p, double xVel) {
        Optional<Particle> right = grid.isValidPosition(p.x + 1, p.y) ?
                Optional.ofNullable(grid.getParticle(p.x + 1, p.y)) : Optional.empty();
        Optional<Particle> left = grid.isValidPosition(p.x - 1, p.y) ?
                Optional.ofNullable(grid.getParticle(p.x - 1, p.y)) : Optional.empty();

        boolean validR = right.isPresent() && right.get() instanceof Empty;
        boolean validL = left.isPresent() && left.get() instanceof Empty;

        if (!(validL || validR)) {
            return Optional.empty();
        }

        if (xVel < 0 && validL) {
            return Optional.of(-1);
        }
        if (xVel > 0 && validR) {
            return Optional.of(1);
        }

        if (validL && validR) {
            return grid.getNextRandBool() ? Optional.of(1) : Optional.of(-1);
        } else if (validL) {
            return Optional.of(-1);
        } else {
            return Optional.of(1);
        }
    }

    public boolean applySink(Grid grid, Particle current) {
        if (moved) return false;

        if (!grid.isValidPosition(current.getX(), current.getY() + 1)) { // change floating number? base on density?
            return false;
        }

        Particle below = grid.getParticle(current.getX(), current.getY() + 1);

        if (canPass(current, below)) {
            moved = true;
            grid.swapParticles(current, below);
            return true;
        }

        return false;
    }

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

        moved = true;
        grid.swapParticles(this, furthest.get());
        return true;
    }

    public Optional<Particle> disperse(Grid grid, int direction) {
        Particle furthestValid = null;
        boolean blocked = false;
        for (int i = 1; i <= dispersionRate; i++) {
            if (!grid.isValidPosition(this.x + (i * direction), this.y)) {
                continue;
            }

            Particle next = grid.getParticle(this.x + (i * direction), this.y);
            if (canPass(this, next)) {
                furthestValid = next;
            } else {
                break; // Don't like this but it works?
            }
        }
        if (furthestValid == null) return Optional.empty();

        return Optional.of(furthestValid);
    }

    // going to need to change this to get direction after density to handle gasses
    public boolean canPass(Particle current, Particle target) {
        return !(target instanceof Solid) && current.getDensity() > target.getDensity();
    }

    // find a better name, checks that the particle below it is one that the current particle moves through
    @Deprecated
    public boolean isRemainFalling(Particle current, Particle below) {
        if (below instanceof Solid) {
            return false;
        }

        return current.density > below.getDensity(); // This is wrong when solids are involved
    }

    private int getBelowIndex(int index, int width, int velocity) {
        return index + (width * velocity);
    }

    public static Color adjustColorRandomly(Color baseColor, double maxHueDelta, double maxSaturationDelta, double maxBrightnessDelta) {
        // Create a random number generator
        Random random = new Random();

        // Generate random deltas within the specified ranges
        double saturationDelta = random.nextDouble() * maxSaturationDelta * 2 - maxSaturationDelta; // range: [-maxSaturationDelta, maxSaturationDelta]
        double brightnessDelta = random.nextDouble() * maxBrightnessDelta * 2 - maxBrightnessDelta; // range: [-maxBrightnessDelta, maxBrightnessDelta]

        // Adjust the color using the random deltas
        return varyColour(baseColor, saturationDelta, brightnessDelta);
    }

    public static Color varyColour(Color baseColor, double saturationDelta, double brightnessDelta) {
        double hue = baseColor.getHue();
        double saturation = baseColor.getSaturation();
        double brightness = baseColor.getBrightness();

        // Adjust HSB components
        saturation = clamp(saturation + saturationDelta, 0, 1);
        brightness = clamp(brightness + brightnessDelta, 0, 1);
        //System.out.println(hue);
        //System.out.println(saturation);
        //System.out.println(brightness);
        // Create and return the new color
        return Color.hsb(hue, saturation, brightness);
    }

    // Clamp a value between min and max
    private static double clamp(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }

    public boolean run(Grid grid, double gravity, double tickRate) {
        // this might not be the best
        boolean g = simpleGravity(grid, this);
        boolean sink = applySink(grid, this);
        boolean d = applyVerticalSideMovement(grid, this, true);
        boolean s = applySideMovement(grid, this);

        // apply across motion

        moved = false;
        return g || d || s || sink; //
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Color getColor() {
        return color;
    }

    public double getDensity() {
        return density;
    }

    public double getHardness() {
        return hardness;
    }

    public void setVelocityX(double vx) {
        this.vx = vx;
    }

    public void setVelocityY(double vy) {
        this.vy = vy;
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    public double getVelocityX() {
        return vx;
    }

    public double getVelocityY() {
        return vy;
    }

    public double getFlammability() {
        return flammability;
    }

    public int getFuelValue() {
        return fuelVal;
    }

    public boolean hasMoved() {
        return moved;
    }
}
