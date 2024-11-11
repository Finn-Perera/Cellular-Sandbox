package finnperera.sandbox;

import finnperera.sandbox.particles.Particle;
import finnperera.sandbox.particles.ParticleType;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Random;


public class SandBox extends Application {
    private static final int TICK_INTERVAL_M = 20; // milliseconds
    private static final int ELEMENT_PANEL_WIDTH = 150; // find a way to make a minimum height for canvas and menu?
    private static final int CANVAS_WIDTH = 700;
    private static final int CANVAS_HEIGHT = 700;
    private static final int CELL_SIZE = 4;
    private double brushRandValue = 0.5;
    private Random random = new Random();
    private Grid grid;
    private GraphicsContext gc;
    private int brushSize = 1;
    private ParticleType currentBrush = ParticleType.EMPTY;
    private ArrayList<Integer> points = new ArrayList<>();
    private Timeline tickTimeLine;
    private MouseEvent currentMouseEvent;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        this.grid = new Grid(CANVAS_WIDTH / CELL_SIZE, CANVAS_HEIGHT / CELL_SIZE, CELL_SIZE);
        gc = canvas.getGraphicsContext2D();
        tickTimeLine = new Timeline();
        tickTimeLine.setCycleCount(Animation.INDEFINITE);
        Controller controller = new Controller(grid, gc);

        HBox root = new HBox();
        root.getChildren().add(canvas);
        root.getChildren().add(createSideBar());

        Scene scene = new Scene(root, CANVAS_WIDTH + ELEMENT_PANEL_WIDTH, CANVAS_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();

        grid.render(gc);
        controller.flipSimRunning();
        canvas.setFocusTraversable(true);
        canvas.setOnMouseDragged(this::handleMouseDragged);
        /*canvas.setOnMousePressed(this::handleMousePressed);
        canvas.setOnMouseReleased(this::handleMouseReleased);
        canvas.setOnMouseMoved(this::updateMouseEvent);
        canvas.setOnMouseMoved(this::drawCursor);*/
    }

    private void drawCursor (MouseEvent m) {
        int radius = brushSize * CELL_SIZE;
        gc.setFill(Color.RED);
        gc.fillRect(m.getX() - radius , m.getY() - radius  , radius, radius);
        gc.setFill(Color.BLACK);
        gc.fillRect(m.getX() - radius + CELL_SIZE,
                m.getY() - radius - CELL_SIZE, radius, radius);
    }

    private void updateMouseEvent(MouseEvent mouseEvent) {
        currentMouseEvent = mouseEvent;
    }

    private VBox createSideBar() {
        VBox sidePanel = new VBox();
        sidePanel.getStylesheets().add("side-panel.css");
        sidePanel.getStyleClass().add("side-panel");


        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        Label title = new Label("Cellular Sandbox");
        title.getStyleClass().add("title");
        title.setAlignment(Pos.TOP_CENTER);

        sidePanel.getChildren().add(title);
        sidePanel.getChildren().add(createParticleButtons());
        sidePanel.getChildren().add(spacer);
        sidePanel.getChildren().add(createBrushButtons());


        sidePanel.setSpacing(75);
        return sidePanel;
    }

    private TilePane createParticleButtons() {
        TilePane buttonPane = new TilePane(); // maybe make a flowPane/TilePane

        buttonPane.getStyleClass().add("button-pane");
        buttonPane.setMinWidth(ELEMENT_PANEL_WIDTH);
        Button waterButton = createButton("Water", ParticleType.WATER);
        Button sandButton = createButton("Sand", ParticleType.SAND);
        Button emptyButton = createButton("Empty", ParticleType.EMPTY); // erase?
        Button stoneButton = createButton("Stone", ParticleType.ROCK); // Rock?
        Button fireButton = createButton("Fire", ParticleType.FIRE);
        Button lavaButton = createButton("Lava", ParticleType.LAVA);
        Button oilButton = createButton("Oil", ParticleType.OIL);
        Button woodButton = createButton("Wood", ParticleType.WOOD);

        buttonPane.getChildren().addAll(waterButton, sandButton, emptyButton, stoneButton,
                fireButton, lavaButton, oilButton, woodButton);
        return buttonPane;
    }

    private TilePane createBrushButtons() {
        TilePane brushPane = new TilePane();

        brushPane.getStyleClass().add("brush-pane");
        brushPane.setPrefColumns(3);
        brushPane.setTileAlignment(Pos.CENTER);

        Button increaseBrushButton = new Button("▲");
        Label brushSizeCount = new Label(String.valueOf(brushSize + 1));
        Button decreaseBrushButton = new Button("▼");

        increaseBrushButton.setOnAction(e -> {
            brushSize += 1;
            updateBrushCount(brushSizeCount);
        });

        decreaseBrushButton.setOnAction(e -> {
            if (brushSize >= 1) {
                brushSize -= 1;
                updateBrushCount(brushSizeCount);
            }
        });

        // maybe remove from button?
        increaseBrushButton.getStyleClass().add("brush-button");
        decreaseBrushButton.getStyleClass().add("brush-button");
        brushSizeCount.getStyleClass().add("brush-count");
        brushPane.getChildren().addAll(increaseBrushButton, brushSizeCount, decreaseBrushButton);

        return brushPane;
    }

    private void updateBrushCount(Label count) {
        count.setText(String.valueOf(brushSize + 1));
    }

    private Button createButton(String name, ParticleType type) {
        Button button = new Button(name);
        button.setOnMousePressed(e -> currentBrush = type);
        return button;
    }

    private void handleMousePressed(MouseEvent mouseEvent) {
        System.out.println("mouse pressed");
        tickTimeLine.stop();
        tickTimeLine.getKeyFrames().clear();
        tickTimeLine.getKeyFrames().add(new KeyFrame(Duration.millis(100), event -> tick()));
        tickTimeLine.getKeyFrames().add(new KeyFrame(Duration.millis(1000), event -> draw(mouseEvent.getButton())));
        tickTimeLine.setCycleCount(Timeline.INDEFINITE);
        tickTimeLine.play();
    }

    private void draw(MouseButton mb) {
        drawPoints(mb);
    }

    private void tick() {
        System.out.println("tick");
        if (currentMouseEvent != null) {
            addPoint(currentMouseEvent);
        }
    }

    private void clearAllButLast(ArrayList<Integer> list) {
        if (list.size() <= 2) {
            return;
        }
        int lastPointY = list.get(list.size() - 1);
        int lastPointX = list.get(list.size() - 2);

        list.clear();
        list.add(lastPointX);
        list.add(lastPointY);
    }

    private void handleMouseReleased(MouseEvent event) {
        System.out.println("mouse released");
        tickTimeLine.stop();
    }

    private void addPoint(MouseEvent event) {
        points.add((int) (event.getX() / CELL_SIZE));
        points.add((int) (event.getY() / CELL_SIZE));
    }

    private void drawPoints(MouseButton mb) {
        System.out.println("drawing");
        for (int i = 0; i < points.size() - 2; i += 2) {
            int x1 = points.get(i);
            int y1 = points.get(i + 1);
            int x2 = points.get(i + 2);
            int y2 = points.get(i + 3);

            ArrayList<Particle> particles = grid.plotLine(x1, y1, x2, y2);
            for (Particle p : particles) {
                if (mb == MouseButton.PRIMARY) {
                    drawSquare(p.getX(), p.getY(), currentBrush);
                } else if (mb == MouseButton.SECONDARY) {
                    drawCircle(p.getX(), p.getY(), currentBrush);
                }
            }
        }
    }

    private void handleMouseDragged(MouseEvent mouseEvent) {
        int cellX = (int) (mouseEvent.getX() / CELL_SIZE);
        int cellY = (int) (mouseEvent.getY() / CELL_SIZE);
        MouseButton button = mouseEvent.getButton();
        if (button == MouseButton.PRIMARY) {
            drawSquare(cellX, cellY, currentBrush);
        } else if (button == MouseButton.SECONDARY) {
            drawCircle(cellX, cellY, currentBrush);
        }
    }

    private void drawSquare(int cellX, int cellY, ParticleType currentBrush) {
        for (int y = Math.max(0, cellY - brushSize); y <= Math.min(CANVAS_HEIGHT / CELL_SIZE, cellY + brushSize); ++y) {
            for (int x = Math.max(0, cellX - brushSize); x <= Math.min(CANVAS_WIDTH / CELL_SIZE, cellX + brushSize); ++x) {
                if (random.nextDouble() >= brushRandValue) {
                    grid.setParticle(x, y, currentBrush);
                }
                //System.out.println("Changed cell: " + cellX + ", " + cellY + ", " + currentBrush.toString());
            }
        }
    }

    private void drawCircle(int cellX, int cellY, ParticleType currentBrush) {

        int index = grid.getIndex(cellX, cellY);
        for (int y = Math.max(0, cellY - brushSize); y <= Math.min(CANVAS_HEIGHT / CELL_SIZE, cellY + brushSize); ++y) {
            for (int x = Math.max(0, cellX - brushSize); x <= Math.min(CANVAS_WIDTH / CELL_SIZE, cellX + brushSize); ++x) {
                float deltaX = (x - cellX) * (x - cellX);
                float deltaY = (y - cellY) * (y - cellY);
                if (Math.sqrt(deltaX + deltaY) <= brushSize && random.nextFloat() >= brushRandValue) {
                    grid.setParticle(x, y, currentBrush);
                    //System.out.println("Changed cell: " + cellX + ", " + cellY + ", " + currentBrush.toString());
                }
            }
        }
    }


}
