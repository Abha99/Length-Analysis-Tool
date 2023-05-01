package com.uga.imagej;

import ij.process.FloatPolygon;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;


class BSpline {
    private ArrayList<Point> coordinates;
    public static final int LEVELS = 7;
    public static final int NPOINTS = ((int) Math.pow(2, LEVELS) + 1);
    protected static double scale = 1;
    int noCtrlPts ;

    /**
     * Calculates curvature of BSpline using curvature formula from
     * Sederberg's text on Computer Aided Graphic Design.
     *
     * @param point List of points on curve.
     * @param noCtrlPts  Number of points on curve.
     * @return Curvature of curve.
     */
    private double computeCurvature(Point2D[] point,int noCtrlPts ) {
      
        if (point.length < 3) {
            return 0;
        }
        double a = point[0].distance(point[1]);
        double dx = point[1].getX() - point[0].getX();
        double dy = point[1].getY() - point[0].getY();
        double h = (dx * (point[0].getY() - point[2].getY()) - dy * (point[0].getX() - point[2].getX())) / Math.sqrt(dx * dx + dy * dy);
        int n1 = noCtrlPts  - 1;
        return ((n1 - 1) / (n1 * 1.0)) * (h / (a * a)) * (1 / scale);
    }

    /**
     * Interpolating curve from given control points.
     *
     * @param coordinates List of points on curve.
     * @param controlPoints Array of points on curve.
     * @param noCtrlPts  Number of points on curve.
     * @param depth Levels of recursion.
     * @param t0 First point of interval.
     * @param t2 Second point of interval.
     */
    private void computeCtrlPnts(ArrayList<Point> coordinates, Point2D[] controlPoints, int noCtrlPts , int depth, double t0, double t2) {
        if (depth == 0) return;
        double t1 = (t0 + t2) / 2;
        double tau = (t1 - t0) / (t2 - t0);
        Point2D[][] points = new Point2D[noCtrlPts][noCtrlPts];
        for (int i = 0; i < noCtrlPts ; i++) {
            points[i][0] = controlPoints[i];
        }
        for (int j = 1; j < noCtrlPts ; j++) {
            for (int i = 0; i < noCtrlPts  - j; i++) {
                points[i][j] = new Point2D.Double(
                        (points[i][j - 1]).getX() * (1 - tau) + tau * (points[i + 1][j - 1]).getX(),
                        (points[i][j - 1]).getY() * (1 - tau) + tau * (points[i + 1][j - 1]).getY());
            }
        }
        Point2D[] firstHalfPoints = new Point2D[noCtrlPts ];
        Point2D[] secondHalfPoints = new Point2D[noCtrlPts ];
        for (int j = 0; j < noCtrlPts ; j++) {
            firstHalfPoints[j] = points[0][j];
            secondHalfPoints[j] = points[j][noCtrlPts  - j - 1];
        }
        computeCtrlPnts(coordinates, firstHalfPoints, noCtrlPts, depth - 1, t0, t1);
        coordinates.add(new Point((float) points[0][noCtrlPts - 1].getX(), (float) points[0][noCtrlPts - 1].getY(),
                (float) computeCurvature(secondHalfPoints,noCtrlPts)));
        computeCtrlPnts(coordinates, secondHalfPoints, noCtrlPts, depth - 1, t1, t2);
    }


    /**
     * Computes average curvature of curve.
     * @return  average curvature.
     */
    public double computeAvgCurvature() {
        double total = 0, avg ;
        for (Point point : coordinates) total += point.k;
        avg = total / coordinates.size();
        return Math.round(avg * 10000000) / 10000000.0;
    }

    public FloatPolygon drawspline() {
        FloatPolygon spline = new FloatPolygon();
        for(Point c:coordinates) {
            spline.addPoint(c.x, c.y);
        }
        return spline;
    }

    /**
     * Convert Floatpolygon to control points and draw BSpline curve.
     * @param contour Path
     */
    public void bsplineCurvature(FloatPolygon contour) {
        List<Point2D> cpList = new ArrayList<>();
        for (int i = 0; i < contour.npoints; i++) {
            cpList.add(new Point2D.Float(contour.xpoints[i], contour.ypoints[i]));
        }
        noCtrlPts = contour.npoints;
        coordinates = new ArrayList<>(NPOINTS);
        Point2D[] ctrlPts = new Point2D[noCtrlPts];
        for (int i = 0; i < cpList.size(); i++) {
            ctrlPts[i] = cpList.get(i);
        }
        coordinates.add(new Point((float) ctrlPts[0].getX(), (float) ctrlPts[0].getY(), (float) computeCurvature(ctrlPts,noCtrlPts )));
        computeCtrlPnts(coordinates, ctrlPts, noCtrlPts , LEVELS, 0, 1);
        coordinates.add(new Point((float)ctrlPts[noCtrlPts  - 1].getX(),  (float)ctrlPts[noCtrlPts  - 1].getY(), (float) computeCurvature(ctrlPts,noCtrlPts )));

    }

}//end class Spline2d
