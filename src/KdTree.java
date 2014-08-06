
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

        private boolean UseX;
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

    private static boolean USE_X = true;
    private static boolean USE_Y = true;

    // add the point p to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (isEmpty()) {
            root = new Node();
            root.p = p;
            root.rect = new RectHV(0, 0, 1, 1);
            root.UseX = true;
            N++;
            return;
        }
        Node prevRoot = root;
        Node node;
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
            } else {
                if (compare == 0) {
                    if (useXorY == USE_X) {
                        if (Double.compare(p.y(), node.p.y()) == 0) {
                            return;
                        }
                    } else {
                        if (Double.compare(p.x(), node.p.x()) == 0) {
                            return;
                        }
                    }
                }
                if (node.rt == null) {
                    break;
                }
                node = node.rt;
            }
            useXorY = !useXorY;
        }
        N++;

        useXorY = !useXorY;
        if (compare == -1) {
            node.lb = new Node();
            node.lb.UseX = useXorY;
            node.lb.p = new Point2D(p.x(), p.y());
            if (useXorY == USE_X) {
                node.lb.rect = new RectHV(node.rect.xmin(), node.rect.ymin(),
                        node.rect.xmax(), node.p.y());
            } else {
                node.lb.rect = new RectHV(node.rect.xmin(), node.rect.ymin(),
                        node.p.x(), node.rect.ymax());
            }
        } else {
            node.rt = new Node();
            node.rt.UseX = useXorY;
            node.rt.p = new Point2D(p.x(), p.y());
            if (useXorY == USE_X) {
                node.rt.rect = new RectHV(node.rect.xmin(), node.p.y(),
                        node.rect.xmax(), node.rect.ymax());
            } else {
                node.rt.rect = new RectHV(node.p.x(), node.rect.ymin(),
                        node.rect.xmax(), node.rect.ymax());
            }
        }
    }

    // does the set contain the point p?
    public boolean contains(Point2D p) {
        Node prevRoot = root;
        Node node;
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
        return found;
    }

    // draw all of the points to standard draw
    public void draw() {
        if (isEmpty()) {
            return;
        }
        boolean useXorY = USE_X;
        Node prevRoot = root;
        Node node;
        node = prevRoot;
        drawSubtree(node, useXorY);
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
                Node node;
                node = prevRoot;
                if (rect.contains(node.p)) {
                    inRangePoints.enqueue(node.p);
                }
                CheckSubtree(node.lb);
                CheckSubtree(node.rt);
                //prevRoot = node;
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

    private Point2D closest;

    // a nearest neighbor in the set to p; null if set is empty
    public Point2D nearest(Point2D p) {
        Node prevRoot = root;
        Node node;
        node = prevRoot;
        closest = node.p;
        int closestCompareToP = Double.compare(closest.x(), p.x());
        if (closestCompareToP == 1) {
            SearchSubtree(p, node.lb);
            SearchSubtree(p, node.rt);
        } else { //if (closestCompareToP == -1) {
            SearchSubtree(p, node.rt);
            SearchSubtree(p, node.lb);
        }
        return closest;
    }

    private void SearchSubtree(Point2D qp, Node n) {
        if (n == null) { // || n.rect.distanceSquaredTo(closest) < n.rect.distanceSquaredTo(qp)) {
            return;
        } else {
            if (qp.distanceSquaredTo(closest) > qp.distanceSquaredTo(n.p)) {
                closest = n.p;
            }
            int closestCompareToP = 0;
            if (n.UseX) {
                closestCompareToP = Double.compare(closest.x(), qp.x());
            } else {
                closestCompareToP = Double.compare(closest.y(), qp.y());
            }
            if (closestCompareToP == 1) {
                SearchSubtree(qp, n.lb);
                SearchSubtree(qp, n.rt);
            } else { 
                SearchSubtree(qp, n.rt);
                SearchSubtree(qp, n.lb);
            }

        }
    }

    public static void main(String[] args) {
        //test1();
        //test2();
        //test4();
        //test5();
        //test6();
        //test7();
        test8();
    }

    private static void test8() {
        In in = new In("circle10.txt");
        KdTree kd = new KdTree();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kd.insert(p);
        }
        kd.draw();
        StdDraw.setPenColor(Color.GREEN);
        StdDraw.setPenRadius(.01);
        Point2D qp = new Point2D(0.21, 0.65);
        Point2D np = kd.nearest(qp);
        System.out.println(np.toString());
        qp.draw();

    }

    private static void test7() {
        int N = 100000;
        for (int j = 1; j < 10; j++) {
            KdTree kd = new KdTree();
            PointSET ps = new PointSET();
            for (int i = 0; i < N; i++) {
                double x = StdRandom.uniform(0, N) / (N * 1.0);
                double y = StdRandom.uniform(0, N) / (N * 1.0);
                ps.insert(new Point2D(x, y));
                kd.insert(new Point2D(x, y));
            }
            System.out.println("ps.size=" + ps.size());
            System.out.println("kd.size=" + kd.size());
            System.out.println("Tesing");
            for (int i = 0; i < N; i++) {
                double x = StdRandom.uniform(0, N) / (N * 1.0);
                double y = StdRandom.uniform(0, N) / (N * 1.0);
                if (ps.contains(new Point2D(x, y))
                        && !ps.contains(new Point2D(x, y))) {
                    System.out.format("****%f, %f", x, y);
                }
            }
        }
    }

    private static void test6() {
        KdTree kd = new KdTree();
        kd.insert(new Point2D(0.5, 0.1));
        kd.insert(new Point2D(0.9, 0.5));
        kd.insert(new Point2D(0.5, 0.9));
        kd.insert(new Point2D(0.1, 0.5));
        kd.draw();
        System.out.println(kd.size());
        System.out.println(kd.contains(new Point2D(0.5, 0.1)));
        System.out.println(kd.contains(new Point2D(0.9, 0.5)));
        System.out.println(kd.contains(new Point2D(0.5, 0.9)));
        System.out.println(kd.contains(new Point2D(0.1, 0.5)));
        for (Point2D p : kd.range(new RectHV(0.0, 0.0, 1.0, 1.0))) {
            System.out.println(p.toString());
        }
        System.out.println("------");
        for (Point2D p : kd.range(new RectHV(0.0, 0.0, 0.5, 0.1))) {
            System.out.println(p.toString());
        }
        System.out.println("------");
        for (Point2D p : kd.range(new RectHV(0.5, 0.1, 0.5, 0.9))) {
            System.out.println(p.toString());
        }
        System.out.println("------");
        for (Point2D p : kd.range(new RectHV(0.5, 0.5, 1.0, 1.0))) {
            System.out.println(p.toString());
        }
        System.out.println("------");
        for (Point2D p : kd.range(new RectHV(0.1, 0.5, 1.0, 1.0))) {
            System.out.println(p.toString());
        }
        System.out.println("------");
        StdDraw.setPenColor(Color.GREEN);
        StdDraw.setPenRadius(.01);

        Point2D qp = new Point2D(0.6, 0.2);
        qp.draw();
        Point2D np = kd.nearest(qp);
        System.out.format("%f,%f\n", np.x(), np.y());
        System.out.println("------");

        qp = new Point2D(0.85, 0.45);
        qp.draw();
        np = kd.nearest(qp);
        System.out.format("%f,%f\n", np.x(), np.y());
        System.out.println("------");

        qp = new Point2D(0.85, 0.55);
        qp.draw();
        np = kd.nearest(qp);
        System.out.format("%f,%f\n", np.x(), np.y());
        System.out.println("------");

        qp = new Point2D(0.55, 0.95);
        qp.draw();
        np = kd.nearest(qp);
        System.out.format("%f,%f\n", np.x(), np.y());
        System.out.println("------");

        qp = new Point2D(0.45, 0.95);
        qp.draw();
        np = kd.nearest(qp);
        System.out.format("%f,%f\n", np.x(), np.y());
        System.out.println("------");

        qp = new Point2D(0.55, 0.49);
        qp.draw();
        np = kd.nearest(qp);
        System.out.format("%f,%f\n", np.x(), np.y());
        System.out.println("------");

        while (true) {

            // the location (x, y) of the mouse
            double x = StdDraw.mouseX();
            double y = StdDraw.mouseY();
            Point2D query = new Point2D(x, y);

            // draw all of the points
            StdDraw.clear();
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(.01);
            kd.draw();

            // draw in blue the nearest neighbor (using kd-tree algorithm)
            StdDraw.setPenColor(StdDraw.BLUE);
            kd.nearest(query).draw();
            StdDraw.show(0);
            StdDraw.show(40);
        }

    }

    private static void test5() {
        KdTree kd = new KdTree();
        double[] ys = new double[10000];
        int Nx = 10;
        int Ny = 10;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                final Point2D p = new Point2D(i / 10.0, j / 10.0);
                System.out.println(p.toString());
                kd.insert(p);
                p.draw();
            }
        }
        kd.draw();
        Iterable<Point2D> range = kd.range(new RectHV(
                0.0, 0.0, 0.4, 0.4));
        for (Point2D p : range) {
            System.out.println(p.toString());
        }
        System.out.println("-------- nearest to 0.0, 0.0");
        Point2D p = kd.nearest(new Point2D(0.0, 0.0));
        System.out.println(p.toString());
        System.out.println("-------- nearest to 0.01, 0.01");
        p = kd.nearest(new Point2D(0.01, 0.01));
        System.out.println(p.toString());
        System.out.println("-------- nearest to 0.9, 0.9");
        p = kd.nearest(new Point2D(0.9, 0.9));
        System.out.println(p.toString());
        System.out.println("-------- nearest to 0.9, 0.0");
        p = kd.nearest(new Point2D(0.9, 0.0));
        System.out.println(p.toString());
    }

    private static void test4() {
        KdTree kd = new KdTree();
        double[] xs = new double[10000];
        double[] ys = new double[10000];

        int N = 10000;
        for (int i = 0; i < N; i++) {
            xs[i] = StdRandom.uniform(N) / (N * 1.0);
            ys[i] = StdRandom.uniform(N) / (N * 1.0);
            kd.insert(new Point2D(xs[i], ys[i]));
        }
        for (int i = 0; i < N; i++) {
            System.out.format("-----%d %f,%f\n", i, xs[i], ys[i]);
            Iterable<Point2D> range = kd.range(new RectHV(
                    xs[i] - 0.0001, ys[i] - 0.0001,
                    xs[i] + 0.0001, ys[i] + 0.0001));
            int x = 0;
            for (Point2D p : range) {
                x++;
            }
            if (x > 1) {
                System.out.println(x);
                System.out.format("%f,%f,%f,%f\n",
                        xs[i] - 0.001, ys[i] - 0.001,
                        xs[i] + 0.001, ys[i] + 0.001);
                for (Point2D p : range) {
                    System.out.println(p.toString());
                }
            }
        }

        System.out.println(kd.size());
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

    private static void swap(double[] ds, int x, int y) {
        double tmp = ds[x];
        ds[x] = ds[y];
        ds[y] = tmp;
    }

    private static void test1() {
        int N = 10000;
        double[] xs = new double[N];
        double[] ys = new double[N];

        for (int i = 0; i < N; i++) {
            xs[i] = i / (N * 1.0);
            ys[i] = i / (N * 1.0);
        }
        for (int i = 0; i < N; i++) {
            int uniform = StdRandom.uniform(0, N);
            swap(xs, i, uniform);
            uniform = StdRandom.uniform(0, N);
            swap(ys, i, uniform);
        }
        for (int j = 0; j < 10000; j++) {
            KdTree kd = new KdTree();
            final int size = j + 1;
            for (int i = 0; i < size; i++) {
                kd.insert(new Point2D(xs[i], ys[i]));
            }
//            System.out.println(size);
//            System.out.println(kd.size());
            if (size != kd.size()) {
                System.out.println("***");
            }
        }
//        for (int i = 0; i < 2000; i++) {
//            kd.insert(new Point2D(xs[i], ys[i]));
//        }
//
//        System.out.println(kd.size());
        //kd.draw();
    }

    private static void test3() {
        KdTree kd = new KdTree();
        double[] xs = new double[10000];
        double[] ys = new double[10000];

        for (int i = 0; i < 1000; i++) {
            xs[i] = StdRandom.uniform(100000) / 100000.0;
            ys[i] = StdRandom.uniform(100000) / 100000.0;
            kd.insert(new Point2D(xs[i], ys[i]));
        }
        System.out.println(kd.size());
        for (int i = 0; i < 200; i++) {
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
