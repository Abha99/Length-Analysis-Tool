package com.uga.imagej;

import ij.process.FloatPolygon;

import java.util.ArrayList;

/**
 * Performs contour correction.
 **/
public class ContourCorrection {
    /**
     * Adjust the ends of paths traversed by cells.
     *
     * @param path path
     * @param twoDArray Two-dimensional array of input image.
     * @return Floatpolygon with adjusted end points.
     */
    public FloatPolygon adjustEndPoints(FloatPolygon path, ArrayList<ArrayList<Integer>> twoDArray) {
        float px, py;
        int num_points = path.npoints;
        FloatPolygon contours = new FloatPolygon();
        float[] row = path.xpoints,col = path.ypoints;
        int r = (int) row[0],c = (int) col[0];
        int[] neighbours_x = {r, r, r - 1, r - 1},neighbours_y = {c - 1, c, c - 1, c};
        for (int s = 0; s < neighbours_x.length; s++)
            if (twoDArray.get(neighbours_x[s]).get(neighbours_y[s]) == 255) contours.addPoint(neighbours_x[s], neighbours_y[s]);
        for (int j = 0; j < num_points; j += 1) {
            px = row[j];
            py = col[j];
            int num = 1;
            ArrayList<Object> sum = new ArrayList<>();
            for (int i = j; i < row.length; i++) {
                if (px == row[i]) {
                    num++;
                    sum.add(col[i]);
                } else break;
            }
            if (num > 1) {
                float temp;
                if (j != 0) {
                    temp = 0;
                    for (Object o : sum) if (temp < (float) o) temp = (float) o;
                } else {
                    temp = 999;
                    for (Object o : sum) if (temp > (float) o) temp = (float) o;
                }
                py = temp;
                col[j] = py;
                for (int n = 0; n < num; n++) col[j + n < num_points ? j + n : j] = py;
                j += (num - 1);
            }
            contours.addPoint((px), (py));
        }
        contours.addPoint(row[num_points - 1], col[num_points - 1]);
        return contours;
    }


}
