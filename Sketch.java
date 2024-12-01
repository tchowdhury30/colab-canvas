import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;


/*
 * @author Ahmad Herzalah and Tasnim Chowdhury
 */
public class Sketch {

    HashMap<String, Shape> shapes = new HashMap<>();

    public void followSteps(String instructions) {
        String[] steps = instructions.split(" ");
        Shape newShape;
        if(steps[0].equals("draw")) {
            if(steps[2].equals("ellipse")) {
                newShape = new Ellipse(Integer.parseInt(steps[3]),
                        Integer.parseInt(steps[4]), Integer.parseInt(steps[5]),
                        Integer.parseInt(steps[6]), new Color(Integer.parseInt(steps[7])));

                shapes.put(steps[1], newShape);
            }
            else if(steps[2].equals("rectangle")) {
                newShape = new Rectangle(Integer.parseInt(steps[3]),
                        Integer.parseInt(steps[4]), Integer.parseInt(steps[5]),
                        Integer.parseInt(steps[6]), new Color(Integer.parseInt(steps[7])));

                shapes.put(steps[1], newShape);
            }
            else if(steps[2].equals("polyline")) {
                newShape = new Polyline(Integer.parseInt(steps[3]),
                        Integer.parseInt(steps[4]), Integer.parseInt(steps[5]),
                        Integer.parseInt(steps[6]), new Color(Integer.parseInt(steps[7])));

                shapes.put(steps[1], newShape);
            }
            /*
            else if(steps[2].equals("polyline")) {
                // Create a new Polyline with the starting point and color
                newShape = new Polyline(Integer.parseInt(steps[3]), Integer.parseInt(steps[4]), new Color(Integer.parseInt(steps[7])));

                // Add additional segments to the polyline
                ((Polyline) newShape).setEnd(Integer.parseInt(steps[5]), Integer.parseInt(steps[6]));

                shapes.put(steps[1], newShape); // Add the new shape to the map
            }
             */
            else if(steps[2].equals("segment")) {
                newShape = new Segment(Integer.parseInt(steps[3]),
                        Integer.parseInt(steps[4]), Integer.parseInt(steps[5]),
                        Integer.parseInt(steps[6]), new Color(Integer.parseInt(steps[7])));
                shapes.put(steps[1], newShape);

            }
        }
        else if(steps[0].equals("move")){
            Shape toMove = shapes.get(steps[1]);
            toMove.moveBy(Integer.parseInt(steps[2]), Integer.parseInt(steps[3]));
            shapes.put(steps[1], toMove);
            System.out.println("Object moved");
        }
        else if(steps[0].equals("delete")){
            shapes.remove(steps[1]);
            System.out.println("Object deleted");
        }
        else if(steps[0].equals("recolor")){
            shapes.get(steps[1]).setColor(new Color(Integer.parseInt(steps[2])));
        }
    }

    public ArrayList<String> getCurrSketch() {
        ArrayList<String> allStringsInstructions = new ArrayList<>();
        if(shapes.isEmpty()) return null;
        for(String key: shapes.keySet()) {
            allStringsInstructions.add("draw "+key+" "+shapes.get(key).toString());
        }
        return  allStringsInstructions;
    }


}
