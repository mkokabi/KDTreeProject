
import java.awt.Color;
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

    private static class Node {

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

    static boolean USE_X = true;
    static boolean USE_Y = false;
    private boolean useXorY;

    // add the point p to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (isEmpty()) {
            root = new Node();
            root.p = p;
            root.rect = new RectHV(p.x(), 0, p.x(), 1);
            N++;
            return;
        }
        Node prevRoot = root;
        Node node = new Node();
        node = prevRoot;
        int compare = 0;
        useXorY = USE_X;
        while (node != null) {
            if (useXorY == USE_X) {
                compare = Double.compare(p.x(), node.p.x());
            } else {
                compare = Double.compare(p.y(), node.p.y());
            }
            if (compare == -1) {
                if (node.lb == null) {
                    break;
                }
                node = node.lb;
            } else if (compare == 1) {
                if (node.rt == null) {
                    break;
                }
                node = node.rt;
            } else {
                prevRoot = node;
                return;
            }
            //System.out.println(p.toString());
            //System.out.println(useXorY);
            useXorY = !useXorY;
        }
        N++;

        useXorY = !useXorY;
        if (compare == -1) {
            node.lb = new Node();
            node.lb.p = p;
            if (useXorY == USE_X) {
                node.lb.rect = new RectHV(p.x(), 0, p.x(), node.rect.ymin());
            } else {
                node.lb.rect = new RectHV(0, p.y(), node.rect.xmin(), p.y());
            }
        } else {
            node.rt = new Node();
            node.rt.p = p;
            if (useXorY == USE_X) {
                node.rt.rect = new RectHV(p.x(), node.rect.ymax(), p.x(), 1);
            } else {
                node.rt.rect = new RectHV(node.rect.xmax(), p.y(), 1, p.y());
            }
        }
        prevRoot = node;
    }

    // does the set contain the point p?
    public boolean contains(Point2D p) {
        Node prevRoot = root;
        Node node = new Node();
        node = prevRoot;
        int compare = 0;
        boolean found = false;
        useXorY = USE_X;
        while (node != null) {
            if (useXorY == USE_X) {
                compare = Double.compare(p.x(), node.p.x());
            } else {
                compare = Double.compare(p.y(), node.p.y());
            }
            if (compare == -1) {
                if (node.lb == null) {
                    break;
                }
                node = node.lb;
            } else if (compare == 1) {
                if (node.rt == null) {
                    break;
                }
                node = node.rt;
            } else {
                prevRoot = node;
                found = true;
                break;
            }
        }
        prevRoot = node;
        return found;
    }

    // draw all of the points to standard draw
    public void draw() {
        if (isEmpty()) {
            return;
        }
        Node prevRoot = root;
        Node node = new Node();
        node = prevRoot;
        drawSubtree(node);
        prevRoot = node;
    }

    private static void drawSubtree(Node n) {
        if (n.rect.width() == 0) {
            StdDraw.setPenColor(Color.RED);
        } else {
            StdDraw.setPenColor(Color.BLUE);
        }
        StdDraw.line(n.rect.xmin(), n.rect.ymin(), n.rect.xmax(), n.rect.ymax());
        //n.p.draw();
        //System.out.println(n.p.toString());
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
                Queue<Point2D> inRangePoints = new Queue<>();

                return inRangePoints.iterator();
            }
        };
    }

    // a nearest neighbor in the set to p; null if set is empty
    public Point2D nearest(Point2D p) {
        return new Point2D(0.0, 0.0);
    }

    public static void main(String[] args) {
        //test1();
        test2();
    }

    private static void test2() {
        KdTree kd = new KdTree();
        kd.insert(new Point2D(0.7, 0.2));
        kd.insert(new Point2D(0.5, 0.4));
        kd.insert(new Point2D(0.2, 0.3));
        kd.insert(new Point2D(0.4, 0.7));
        kd.insert(new Point2D(0.9, 0.6));
        kd.draw();
    }

    private static void test1() {
        KdTree kd = new KdTree();
        double[] xs = new double[10000];
        double[] ys = new double[10000];

        for (int i = 0; i < 100; i++) {
            xs[i] = StdRandom.uniform();
            ys[i] = StdRandom.uniform();
            kd.insert(new Point2D(xs[i], ys[i]));
        }
        System.out.println(kd.size());
        for (int i = 0; i < 20; i++) {
            kd.insert(new Point2D(xs[i], ys[i]));
        }

        System.out.println(kd.size());
        boolean found = false;
        for (int i = 0; i < 10; i++) {
            int j = StdRandom.uniform(10);
            found = kd.contains(new Point2D((xs[j]), ys[j]));
            if (!found) {
                System.out.println(found);
            }
        }

        kd.draw();
    }
}
