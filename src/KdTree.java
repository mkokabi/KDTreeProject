
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
public class KdTree {

    private class Node {

        // the point
        private Point2D p;
        // the axis-aligned rectangle corresponding to this node 
        private RectHV rect;
        // the left/bottom subtree    
        private Node lb;
        // the right/top subtree     
        private Node rt;
    }

    private Node root;
    private int N;

    // construct an empty set of points
    public KdTree() {
        N = 0;
    }

    // is the set empty?
    public boolean isEmpty() {
        return N == 0;
    }

    // number of points in the set
    public int size() {
        return N;
    }

    // add the point p to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (isEmpty()) {
            root = new Node();
            root.p = p;
            N++;
            return;
        }
        Node prevRoot = root;
        Node root = new Node();
        root = prevRoot;
        int compare = 0;
        while (root != null) {
            compare = p.compareTo(root.p);
            if (compare == -1) {
                if (root.lb == null) {
                    break;
                }
                root = root.lb;
            } else if (compare == 1) {
                if (root.rt == null) {
                    break;
                }
                root = root.rt;
            } else {
                prevRoot = root;
                return;
            }
        }
        N++;
        if (compare == -1) {
            root.lb = new Node();
            root.lb.p = p;
        } else {
            root.rt = new Node();
            root.rt.p = p;
        }
        prevRoot = root;
    }

    // does the set contain the point p?
    public boolean contains(Point2D p) {
        return false;
    }

    // draw all of the points to standard draw
    public void draw() {
        if (isEmpty()) {
            return;
        }
        Node prevRoot = root;
        Node root = new Node();
        root = prevRoot;
        drawSubtree(root);
        prevRoot = root;
    }

    private static void drawSubtree(Node n) {
        n.p.draw();
        System.out.println(n.p.toString());
        if (n.lb != null) {
            drawSubtree(n.lb);
        }
        if (n.rt != null) {
            drawSubtree(n.rt);
        }
    }

    // all points in the set that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect) {
        return new Iterable<Point2D>() {

            @Override
            public Iterator<Point2D> iterator() {
                return new Iterator<Point2D>() {

                    @Override
                    public boolean hasNext() {
                        return false;
                    }

                    @Override
                    public Point2D next() {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }
                };
            }
        };
    }

    // a nearest neighbor in the set to p; null if set is empty
    public Point2D nearest(Point2D p) {
        return new Point2D(0.0, 0.0);
    }

    public static void main(String[] args) {
        KdTree kd = new KdTree();
        double[] xs = new double[1000];
        double[] ys = new double[1000];

        for (int i = 0; i < 10; i++) {
            xs[i] = StdRandom.uniform();
            ys[i] = StdRandom.uniform();
            kd.insert(new Point2D(xs[i], ys[i]));
        }
        System.out.println(kd.size());
        for (int i = 0; i < 2; i++) {
            kd.insert(new Point2D(xs[i], ys[i]));
        }

        System.out.println(kd.size());

        kd.draw();
    }
}
