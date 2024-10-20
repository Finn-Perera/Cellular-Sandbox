package finnperera.sandbox;

import finnperera.sandbox.particles.*;
import finnperera.sandbox.particles.placeable.Empty;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.Random;


public class Grid {

    private final int width;
    private final int height;
    private Particle[] grid;
    private final int cellSize;
    private final Random random = new Random();

    public Grid(int width, int height, int cellSize) {
        this.width = width;
        this.height = height;
        this.grid = new Particle[width * height];
        this.cellSize = cellSize;
        initializeGrid();
    }

    private void initializeGrid() {
        for (int i = 0; i < grid.length; i++) {
            int x = i % width;
            int y = i / width;
            grid[i] = new Empty(x, y);
        }
    }

    public ArrayList<Particle> plotLine(int x1, int y1, int x2, int y2) {
        if (Math.abs(y2 - y1) < Math.abs(x2 - x1)) {
            if (x1 > x2) {
                return plotLineLow(x2, y2, x1, y1);
            } else {
                return plotLineLow(x1, y1, x2, y2);
            }
        } else {
            if (y1 > y2) {
                return plotLineHigh(x2, y2, x1, y1);
            } else {
                return plotLineHigh(x1, y1, x2, y2);
            }
        }
    }

    private ArrayList<Particle> plotLineLow(int x1, int y1, int x2, int y2) {
        ArrayList<Particle> particles = new ArrayList<>();
        int dx = x2 - x1;
        int dy = y2 - y1;
        int yi = 1;

        if (dy < 0) {
            yi = -1;
            dy = -dy;
        }

        int D = (2 * dy) - dx;
        int y = y1;

        for (int x = x1; x < x2; x++) {
            particles.add(getParticle(x, y));
            if (D > 0) {
                y += yi;
                D += (2 * (dy - dx));
            } else {
                D += 2 * dy;
            }
        }
        return particles;
    }

    private ArrayList<Particle> plotLineHigh(int x1, int y1, int x2, int y2) {
        ArrayList<Particle> particles = new ArrayList<>();
        int dx = x2 - x1;
        int dy = y2 - y1;
        int xi = 1;

        if (dx < 0) {
            xi = -1;
            dx = -dx;
        }

        int D = (2 * dx) - dy;
        int x = x1;

        for (int y = y1; y < y2; y++) {
            particles.add(getParticle(x, y));
            if (D > 0) {
                x += xi;
                D += (2 * (dx - dy));
            } else {
                D += 2 * dx;
            }
        }
        return particles;
    }

    public Particle getParticle(int x, int y) {
        int index = getIndex(x, y);
        if (index != -1) {
            return grid[index];
        } else {
            return null;
        }
    }

    public Particle getParticle(int index) {
        if (isValidIndex(index)) {
            return grid[index];
        } else {
            return null;
        }
    }

    public void setParticle(int x, int y, ParticleType currentBrush) {
        try {
            Particle particle = currentBrush.createInstance(x, y);
            setParticle(x, y, particle);
        } catch (Exception e) {
            Empty empty = new Empty(x, y); // If it can't get the correct particle, default to empty
            setParticle(x, y, empty);
        }
    }

    public void setParticle(int x, int y, Particle particle) {
        int index = getIndex(x, y);

        if (index == -1) {
            return;
        }

        try {
            particle.setMoved(true);
            grid[index] = particle; // find a way to update particles on creation
        } catch (Exception e) {
            Empty empty = new Empty(x, y);
            empty.setMoved(true);
            grid[index] = empty;
        }
    }

    public void replaceParticle(Particle replacement, Particle old) {
        int x = replacement.getX();
        int y = replacement.getY();
        swapParticles(replacement, old);
        setParticle(x, y, ParticleType.EMPTY);
    }

    public int getIndex(int x, int y) {
        if (isValidPosition(x, y)) {
            return y * width + x;
        } else {
            return -1; // error
        }
    }

    public ArrayList<Particle> getNeighbourParticles(Particle particle) {
        // could do a couple ways use index's or x,y positions
        ArrayList<Particle> neighbours = new ArrayList<>();

        if (this.isValidPosition(particle.getX(), particle.getY() - 1)) {
            // above
            neighbours.add(this.getParticle(particle.getX(), particle.getY() - 1));
        }

        if (this.isValidPosition(particle.getX(), particle.getY() + 1)) {
            // below
            neighbours.add(this.getParticle(particle.getX(), particle.getY() + 1));
        }

        if (this.isValidPosition(particle.getX() + 1, particle.getY())) {
            // right
            neighbours.add(this.getParticle(particle.getX() + 1, particle.getY()));
        }

        if (this.isValidPosition(particle.getX() - 1, particle.getY())) {
            // left
            neighbours.add(this.getParticle(particle.getX() - 1, particle.getY()));
        }

        return neighbours;
    }

    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public boolean isValidIndex(int index) {
        return index >= 0 && index < grid.length;
    }

    // Fix these?
    public void swapParticles(int x1, int y1, int x2, int y2) {
        int index1 = getIndex(x1, y1);
        int index2 = getIndex(x2, y2);
        if (index1 != -1 && index2 != -1) {
            Particle temp = grid[index1];
            grid[index1] = grid[index2];
            grid[index2] = temp;
        }
    }

    public void swapParticles(int index1, int index2) {
        if (index1 != -1 && index2 != -1) {
            Particle temp = grid[index1];
            grid[index1] = grid[index2];
            grid[index2] = temp;
        }
    }

    public void swapParticles(Particle p1, Particle p2) {

        // XOR swap of x and y
        int x1 = p1.getX();
        int y1 = p1.getY();
        int x2 = p2.getX();
        int y2 = p2.getY();

        int p1Index = getIndex(x1, y1);
        int p2Index = getIndex(x2, y2);

        if (p1Index != -1 && p2Index != -1) {
            x1 = x1 ^ x2;
            x2 = x1 ^ x2;
            x1 = x1 ^ x2;
            p1.setX(x1);
            p2.setX(x2);

            y1 = y1 ^ y2;
            y2 = y1 ^ y2;
            y1 = y1 ^ y2;

            p1.setY(y1);
            p2.setY(y2);

            Particle temp = grid[p1Index];
            grid[p1Index] = grid[p2Index];
            grid[p2Index] = temp;
        }
    }

    public void render(GraphicsContext gc) {
        for (int i = 0; i < grid.length; i++) {
            int x = i % width;
            int y = i / width;
            Particle particle = grid[i];
            if (particle != null) {
                renderParticle(gc, particle, x * cellSize, y * cellSize);
            }
        }
    }

    private void renderParticle(GraphicsContext gc, Particle particle, int x, int y) {
        gc.setFill(particle.getColor());
        gc.fillRect(x, y, cellSize, cellSize);
    }

    public Particle[] getParticles() {
        return grid;
    }

    public int getGridX(float fx) {
        return (int) (fx / cellSize);
    }

    public int getGridY(float fy) {
        return (int) (fy / cellSize);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getGridLength() {
        return grid.length;
    }

    public int getNextRandInt() {
        return random.nextInt();
    }

    public boolean getNextRandBool() {
        return random.nextBoolean();
    }
}
