package com.uga.imagej;

import java.awt.geom.Point2D;

/**
 * Stores Curvature of each point along BSpline curve.
 */
public class Point extends Point2D.Double {
    // Curvature values for a given point along a BSpline curve
    public double k;
    protected int sign;
    /**
     * Constructs a Point object for each coordinate on BSpline
     *
     * @param x x-coordinate of the point.
     * @param y y-coordinate of the point.
     * @param k Curvature of the point
     */
    public Point(float x, float y, float k) {
        super(x, y);
        if (k >= 0) {
            this.sign = 1;
        } else {
            this.sign = -1;
        }
        this.k = Math.abs(k);
    }

}
