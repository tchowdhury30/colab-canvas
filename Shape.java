import java.awt.Color;
import java.awt.Graphics;

/**
 * A geometric entity with a color
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2016, based on a related concept from previous terms
 * @author CBK, revised Fall 2016
 */

public interface Shape {
    /**
     * Moves the shape by dx in the x coordinate and dy in the y coordinate
     */
    public void moveBy(int dx, int dy);

    /**
     * Whether or not the point is inside the shape
     */
    public boolean contains(int x, int y);

    /**
     * @return The shape's color
     */
    public Color getColor();

    /**
     * @param color The shape's color
     */
    public void setColor(Color color);

    /**
     * Draws the shape
     */
    public void draw(Graphics g);

    /**
     * adds all the corners to the shape and redefines when necessary (redefining is necessary in ellipse and rectangle)
     */
    public void setCorners(int x1, int y1, int x2, int y2);
}
