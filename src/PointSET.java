
import java.util.Iterator;
import java.util.TreeSet;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Mohsen
 */
public class PointSET {

    private TreeSet<Point2D> points;

    // construct an empty set of points
    public PointSET() {
        points = new TreeSet<Point2D>();
    }

    // is the set empty?
    public boolean isEmpty() {
        return points.isEmpty();
    }

    // number of points in the set 
    public int size() {
        return points.size();
    }

    // add the point p to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (!points.contains(p)) {
            points.add(p);
        }
    }

    // does the set contain the point p?
    public boolean contains(Point2D p) {
        return points.contains(p);
    }

    // draw all of the points to standard draw
    public void draw() {
        for (Point2D point : points) {
            point.draw();
        }
    }

    // all points in the set that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect) {
        final RectHV r = rect;
        return new Iterable<Point2D>() {

            @Override
            public Iterator<Point2D> iterator() {
                Queue<Point2D> inRangePoints = new Queue<>();
                for (Point2D point : points) {
                    if (point.x() > r.xmin() && point.x() < r.xmax()
                            && point.y() > r.ymin() && point.y() < r.ymax()) {
                        inRangePoints.enqueue(point);
                    }
                }
                return inRangePoints.iterator();
            }
        };
    }

    // a nearest neighbor in the set to p; null if set is empty
    public Point2D nearest(Point2D p) {
        if (points.isEmpty()) {
            return null;
        }
        double nearesetDist = Double.MAX_VALUE;
        Point2D champ = points.first();
        for (Point2D point : points) {
            double dist = point.distanceSquaredTo(p);
            if (dist < nearesetDist) {
                nearesetDist = dist;
                champ = point;
            }
        }
        return champ;
    }
    
    public static void main(String args[]) {
        PointSET ps = new PointSET();
        
        double[] xs = new double[1000];
        double[] ys = new double[1000];
        
        for (int i = 0; i < 1000; i++) {
            xs[i] = StdRandom.uniform();
            ys[i] = StdRandom.uniform();            
            ps.insert(new Point2D(xs[i], ys[i]));
        }
        System.out.println(ps.size());
        for (int i = 0; i < 100; i++) {
            ps.insert(new Point2D(xs[i], ys[i]));
        }
        System.out.println(ps.size());
        
    }
}
