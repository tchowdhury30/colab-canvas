import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * Basic shape drawing GUI
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012; loosely based on CS 5 code by Tom Cormen
 * @author CBK, lightly revised Winter 2014
 * @author CBK, restructured Shape/Drawer and some of the GUI, Spring 2016
 * @author CBK, more restructuring and simplification, Fall 2016
 *
 * @author Ahmad Herzalah and Tasnim Chowdhury
 */

public class Editor extends JFrame {
    private static final int width = 800, height = 800;
    private static String serverIP = "localhost";			// IP address of sketch server


    // Current settings on GUI
    public enum Mode {
        DRAW, MOVE, RECOLOR, DELETE
    }
    private Mode mode = Mode.DRAW;				// drawing/moving/recoloring/deleting objects
    private String shapeType = "ellipse";		// type of object to add
    private Color color = Color.black;			// current drawing color

    // Drawing state
    private Shape shape = null;				// the only object (if any) in our editor
    private Shape selectedShape = null;				// the only object (if any) in our editor
    private String selectedShapeId = null;

    private Sketch sketch;
    private Point drawFrom = null;				// where the drawing started
    private Point moveFrom = null;				// where object is as it's being dragged
    int startX, startY, endX, endY, dx, dy;

    private EditorCommunicator comm;

    public Editor() {
        super("Graphical Editor");
        sketch = new Sketch();

        comm = new EditorCommunicator(serverIP, this);
        comm.start();

        // Helpers to create the canvas and GUI (buttons, etc.)
        JComponent canvas = setupCanvas();
        JComponent gui = setupGUI();

        // Put the buttons and canvas together into the window
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(canvas, BorderLayout.CENTER);
        cp.add(gui, BorderLayout.NORTH);

        // Usual initialization
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }

    /**
     * Creates a component to draw into
     */
    private JComponent setupCanvas() {
        JComponent canvas = new JComponent() {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                // TODO: YOUR CODE HERE
                drawSketch(g);
                // Call helper method to draw the sketch on g
            }
        };

        canvas.setPreferredSize(new Dimension(width, height));

        canvas.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                // TODO: YOUR CODE HERE
                startX = event.getPoint().x;
                startY = event.getPoint().y;

                // Call helper method to handle the mouse press
                handlePress(event.getPoint());
                //System.out.println("pressed at "+event.getPoint());
            }

            public void mouseReleased(MouseEvent event) {
                // TODO: YOUR CODE HERE
                // Call helper method to handle the mouse release
                handleRelease();
                //System.out.println("released at "+event.getPoint());
            }
        });

        canvas.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent event) {
                handleDrag(event.getPoint());
                // TODO: YOUR CODE HERE
                // Call helper method to handle the mouse drag
                //System.out.println("dragged to "+event.getPoint());
            }
        });

        return canvas;
    }

    /**
     * Creates a panel with all the buttons
     */
    private JComponent setupGUI() {
        // Select type of shape
        String[] shapes = {"ellipse", "freehand", "rectangle", "segment"};
        JComboBox<String> shapeB = new JComboBox<String>(shapes);
        shapeB.addActionListener(e -> shapeType = (String)((JComboBox<String>)e.getSource()).getSelectedItem());

        // Select drawing/recoloring color
        // Following Oracle example
        JButton chooseColorB = new JButton("choose color");
        JColorChooser colorChooser = new JColorChooser();
        JLabel colorL = new JLabel();
        colorL.setBackground(color);
        colorL.setOpaque(true);
        colorL.setBorder(BorderFactory.createLineBorder(color));
        colorL.setPreferredSize(new Dimension(25, 25));
        JDialog colorDialog = JColorChooser.createDialog(chooseColorB,
                "Pick a Color",
                true,  //modal
                colorChooser,
                e -> { color = colorChooser.getColor(); colorL.setBackground(color); color = colorChooser.getColor(); },  // OK button
                null); // no CANCEL button handler
        chooseColorB.addActionListener(e -> colorDialog.setVisible(true));

        // Mode: draw, move, recolor, or delete
        JRadioButton drawB = new JRadioButton("draw");
        drawB.addActionListener(e -> mode = Mode.DRAW);
        drawB.setSelected(true);
        JRadioButton moveB = new JRadioButton("move");
        moveB.addActionListener(e -> mode = Mode.MOVE);
        JRadioButton recolorB = new JRadioButton("recolor");
        recolorB.addActionListener(e -> mode = Mode.RECOLOR);
        JRadioButton deleteB = new JRadioButton("delete");
        deleteB.addActionListener(e -> mode = Mode.DELETE);
        ButtonGroup modes = new ButtonGroup(); // make them act as radios -- only one selected
        modes.add(drawB);
        modes.add(moveB);
        modes.add(recolorB);
        modes.add(deleteB);
        JPanel modesP = new JPanel(new GridLayout(1, 0)); // group them on the GUI
        modesP.add(drawB);
        modesP.add(moveB);
        modesP.add(recolorB);
        modesP.add(deleteB);

        // Put all the stuff into a panel
        JComponent gui = new JPanel();
        gui.setLayout(new FlowLayout());
        gui.add(shapeB);
        gui.add(chooseColorB);
        gui.add(colorL);
        gui.add(modesP);
        return gui;
    }


    public Sketch getSketch() {
        return sketch;
    }

    /**
     * Helper method for finding a selected Ellipse
     */


    public Shape findEllipse(int dx, int dy) {
        Shape result = null; // Initialize the result as null
        // Iterate over all shapes in the sketch
        for (String shapeids : sketch.shapes.keySet()) {
            Shape temp = sketch.shapes.get(shapeids); // Get the shape for the current ID
            // Check if the shape contains the point (dx, dy)
            if (temp.contains(dx, dy)) {
                result = temp; // If it does, set result to this shape
                break; // Break the loop as we found our shape
            }
        }
        return result; // Return the found shape, or null if none was found
    }

    public String findEllipseId(int dx, int dy) {
        String id = null; // Initialize the ID as null
        // Iterate over all shapes in the sketch
        for (String shapeids : sketch.shapes.keySet()) {
            Shape temp = sketch.shapes.get(shapeids); // Get the shape for the current ID
            // Check if the shape contains the point (dx, dy)
            if (temp.contains(dx, dy)) {
                id = shapeids; // If it does, set id to this shape's ID
                break; // Break the loop as we found our shape
            }
        }
        return id; // Return the ID of the found shape, or null if none was found
    }



    /**
     * Helper method for deleting an Ellipse
     */


    public void deleteEllipse(int dx, int dy) {
        // Iterate over all shapes in the sketch
        for (String shapeids : sketch.shapes.keySet()) {
            Shape temp = sketch.shapes.get(shapeids); // Get the shape for the current ID
            // Check if the shape contains the point (dx, dy)
            if (temp.contains(dx, dy)) {
                sketch.shapes.remove(shapeids); // Remove the shape from the sketch
                String steps = "delete " + shapeids; // Prepare a delete command
                comm.send(steps); // Send the delete command to the server
                sketch.followSteps(steps); // Apply the delete command to the local sketch

                System.out.println(shapeids + " deleted"); // Log the deletion
                break; // Break the loop as we have deleted the shape
            } else {
                System.out.println("there's no ellipse found"); // Log if no shape is found
            }
        }
    }

    private void finalizePolyline() {
        if (shape != null && shape instanceof Polyline) {
            // Add the current polyline to the sketch
            sketch.shapes.put("polyline" + sketch.shapes.size() + 1, shape);
            String steps = "draw polyline " + (sketch.shapes.size() + 1) + " " + shape;
            comm.send(steps);
            shape = null; // Set shape to null to indicate completion
        }
        repaint();
    }

    /**
     * Helper method for press at point
     */

    private void handlePress(Point p) {
        String steps;
        // TODO: YOUR CODE HERE
        // In drawing mode, start drawing a new shape
        if (mode == Mode.DRAW) {
            if (shapeType.equals("ellipse")) {
                shape = new Ellipse(p.x, p.y, color);
            } else if (shapeType.equals("rectangle")) {
                shape = new Rectangle(p.x, p.y, color);
            } else if (shapeType.equals("segment")) {
                shape = new Segment(p.x, p.y, color);
            } else if (shapeType.equals("polyline")) {
                shape = new Polyline(p.x, p.y, color);
            }
        }

        // In moving mode, start dragging if clicked in the shape
        if (mode == Mode.MOVE) {

            selectedShape = findEllipse(p.x, p.y);
            selectedShapeId = findEllipseId(p.x, p.y);


        }
        // In recoloring mode, change the shape's color if clicked in it
        if (mode == Mode.RECOLOR) {

            selectedShape = findEllipse(p.x, p.y);
            selectedShapeId = findEllipseId(p.x, p.y);

            selectedShape.setColor(color);

            String idToColor = findEllipseId(p.x, p.y);
            steps = "recolor "+ idToColor +" " + color;
            sketch.followSteps(steps);
            comm.send(steps);

        }
        // In deleting mode, delete the shape if clicked in it

        if (mode == Mode.DELETE) {
            deleteEllipse(p.x, p.y);

        }
        repaint();
    }

    /**
     * Helper method for drag to new point
     */
    private void handleDrag(Point p) {
        // TODO: YOUR CODE HERE
        endX = p.x;
        endY = p.y;
        // In drawing mode, revise the shape as it is stretched out
        /* BRUH
        if (mode == Mode.DRAW) {
            if (shapeType.equals("freehand")) {
                System.out.println(shapeType + "HUEIAGRYWHFJ");
                if (shape == null) {
                    shape = new Polyline(startX, startY, color);
                }
                ((Polyline) shape).setEnd(endX, endY);
            } else {
                shape.setCorners(startX, startY, endX, endY);
            }
        }
         */
        if (mode == Mode.DRAW) {
            shape.setCorners(startX, startY, endX, endY);
        }
        // In moving mode, shift the object and keep track of where next step is from
        if (mode == Mode.MOVE) {
            if (selectedShape != null && selectedShapeId != null) {
                dx = endX - startX;
                dy = endY - startY;
                String steps = "move "  + selectedShapeId + " " + dx + " " + dy;
                comm.send(steps);
            }
        }
        repaint();
    }

    /**
     * Helper method for release
     */
    private void handleRelease() {
        // TODO: YOUR CODE HERE


        /* BRUH
        if (mode == Mode.DRAW && shapeType.equals("polyline")) {
            // Add the current polyline to the sketch
            sketch.shapes.put("polyline" + sketch.shapes.size() + 1, shape);
            String steps = "draw polyline " + (sketch.shapes.size() + 1) + " " + shape;
            comm.send(steps);
            shape = null; // Set shape to null to indicate completion
        }
        if (mode == Mode.DRAW && !shapeType.equals("polyline")) {
            // Handle other shapes
            sketch.shapes.put("ellipse" + sketch.shapes.size() + 1, shape);
            String steps = "draw " + shapeType + " " + (sketch.shapes.size() + 1) + " " + shape;
            comm.send(steps);
            shape = null;
        }
         */

        if (mode == Mode.DRAW) {
            sketch.shapes.put("ellipse" + sketch.shapes.size() + 1, shape);
            String steps = "draw "+(sketch.shapes.size() + 1)+" "+ shape;
            comm.send(steps);
            System.out.println(sketch.shapes.size());
            shape = null;
        }
        // In moving mode, stop dragging the object

        if (mode == Mode.MOVE) {
            selectedShape.moveBy(dx, dy);
            selectedShape = null;
        }
        repaint();
    }

    /**
     * Draw the whole sketch (here maybe a single shape)
     */
    private void drawSketch(Graphics g) {
        // TODO: YOUR CODE HERE
        // Draw the current shape if it exists
        if (!sketch.shapes.isEmpty()) {
            for (String shapeid : sketch.shapes.keySet()) {
                sketch.shapes.get(shapeid).draw(g);
            }
        }
        if (shape != null)
            shape.draw(g);

    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Editor();
            }
        });
    }
}