
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
 
    // add the point p to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (isEmpty()) {
            root = new Node();
            root.p = p;
            root.rect = new RectHV(0, 0, 1, 1);
            N++;
            return;
        }
        Node prevRoot = root;
        Node node = new Node();
        node = prevRoot;
        int compare = 0;
        boolean useXorY = USE_X;
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
                node.lb.rect = new RectHV(node.rect.xmin(), node.rect.ymin(), 
                        node.rect.xmax(), node.p.y());
            } else {
                node.lb.rect = new RectHV(node.rect.xmin(), node.rect.ymin(), 
                        node.p.x(), node.rect.ymax());
            }
        } else {
            node.rt = new Node();
            node.rt.p = p;
            if (useXorY == USE_X) {
                node.rt.rect = new RectHV(node.rect.xmin(), node.p.y(), 
                        node.rect.xmax(), node.rect.ymax());
            } else {
                node.rt.rect = new RectHV(node.p.x(), node.rect.ymin(), 
                        node.rect.xmax(), node.rect.ymax());
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
        boolean useXorY = USE_X;
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
            useXorY = !useXorY;
        }
        prevRoot = node;
        return found;
    }

    // draw all of the points to standard draw
    public void draw() {
        if (isEmpty()) {
            return;
        }
        boolean useXorY = USE_X;
        Node prevRoot = root;
        Node node = new Node();
        node = prevRoot;
        drawSubtree(node, useXorY);
        prevRoot = node;
    }

    private static void drawSubtree(Node n, boolean useXorY) {
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.setPenRadius(.01);
        n.rect.draw();
        if (n.lb != null) {
            drawSubtree(n.lb, !useXorY);
        }
        if (n.rt != null) {
            drawSubtree(n.rt, !useXorY);
        }
        StdDraw.setPenRadius(.01);
        if (useXorY == USE_X) {
            StdDraw.setPenColor(Color.RED);
            StdDraw.line(n.p.x(), n.rect.ymin(), n.p.x(), n.rect.ymax());
        } else {
            StdDraw.setPenColor(Color.BLUE);
            StdDraw.line(n.rect.xmin(), n.p.y(), n.rect.xmax(), n.p.y());
        }
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.setPenRadius(.02);
        n.p.draw();
        //System.out.println(n.p.toString());
    }

    // all points in the set that are inside the rectangle
    public Iterable<Point2D> range(RectHV r) {
        final RectHV rect = r;
        final Queue<Point2D> inRangePoints = new Queue<>();
        return new Iterable<Point2D>() {

            @Override
            public Iterator<Point2D> iterator() {
                Node prevRoot = root;
                Node node = new Node();
                node = prevRoot;
                CheckSubtree(node.lb);
                CheckSubtree(node.rt);
                prevRoot = node;
                return inRangePoints.iterator();
            }

            private void CheckSubtree(Node n) {
                if (n == null || !n.rect.intersects(rect)) {
                    return;
                }
                if (rect.contains(n.p)) {
                    inRangePoints.enqueue(n.p);
                }
                CheckSubtree(n.lb);
                CheckSubtree(n.rt);
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
        System.out.println(kd.contains(new Point2D(0.4, 0.7)));
        //final RectHV rectHV = new RectHV(0.45, 0.35, 0.55, 0.45);
        final RectHV rectHV = new RectHV(0.15, 0.25, 0.55, 0.45);
        StdDraw.setPenColor(Color.GREEN);
        rectHV.draw();
        for (Point2D p : kd.range(rectHV)) {
            System.out.println(p.toString());
        }
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
