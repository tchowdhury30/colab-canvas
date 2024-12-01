import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * A multi-segment Shape, with straight lines connecting "joint" points -- (x1,y1) to (x2,y2) to (x3,y3) ...
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2016
 * @author CBK, updated Fall 2016
 */
public class Polyline implements Shape {
    // TODO: YOUR CODE HERE
    private ArrayList<Segment> segments = new ArrayList<>();
    private Color color;
    private int startX, startY; // Starting point of the polyline

    public Polyline (ArrayList<Segment> segments, Color color){
        this.segments = segments;
        this.color = color;
    }

    public Polyline(int x, int y, Color color) {
        this.segments = new ArrayList<>();
        this.color = color;
        this.startX = x;
        this.startY = y;
        System.out.println("Polyline created with start (" + x + ", " + y + ")");
    }


    public void setEnd(int x, int y) {
        if (!segments.isEmpty()) {
            Segment lastSegment = segments.get(segments.size() - 1);
            segments.add(new Segment(lastSegment.getX2(), lastSegment.getY2(), x, y, color));
        } else {
            segments.add(new Segment(startX, startY, x, y, color));
        }
        System.out.println("Set end to (" + x + ", " + y + ")");
    }

    public void setCorners (int x1, int y1, int x2, int y2){
        Segment segment = new Segment(x1, y1, x2, y2, color);
        segment.setCorners(x1, y1, x2, y2);
        segments.add(segment);
    }

    /*
    @Override
    public void setCorners(int x1, int y1, int x2, int y2) {
        // Clear existing segments
        segments.clear();

        // Set the start point
        startX = x1;
        startY = y1;

        // Add a new segment
        segments.add(new Segment(x1, y1, x2, y2, color));
    }
     */

    public ArrayList<Segment> getSegments() {
        return segments;
    }

    public void setSegments(ArrayList<Segment> segments) {
        this.segments = segments;
    }

    public void addSegment (Segment segment){
        segments.add(segment);
    }

    public Polyline (int x, int y, int z, int a, Color color){
        this.color = color;
        addSegment(new Segment(x, y, z, a, color));
    }

    @Override
    public void moveBy(int dx, int dy) {
        for (Segment segment : segments){
            segment.moveBy(dx, dy);
        }
        System.out.println("Polyline moved by (" + dx + ", " + dy + ")");
    }
    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }


    @Override
    public boolean contains(int x, int y) {
        for (Segment segment: segments)
            if (segment.contains(x, y))
                return true;
        return false;
    }

    @Override
    public void draw(Graphics g) {
        for (Segment segment: segments)
            segment.draw(g);
    }

    @Override
    public String toString() {
        String str = "polyline";
        for (Segment segment : segments)
            str += " " + segment;
        return str;
    }
}
