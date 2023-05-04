package com.uga.imagej;

import ij.ImagePlus;
import ij.gui.*;
import java.awt.*;
import ij.process.FloatPolygon;
import java.util.ArrayList;


import static java.lang.Math.abs;
import static java.lang.Math.max;
import ij.gui.Overlay;
import ij.gui.PolygonRoi;
import ij.gui.TextRoi;

/**
 * Identify and draw the paths traversed by the cells in an image.
 */
public class Contours  {
    private int label = 1,temp_r = 0,prev_r = 0,prev_c = 0,curr_r = 0,curr_c = 0,n_count,n = 0;
    public ArrayList<FloatPolygon> paths;

    /**
     * Returns the paths traversed by the cells in an image
     * using the connected component labelling algorithm
     *
     * @param twoDArray Two-dimensional array of input image.
     */
    public ArrayList<ArrayList<Integer>> findLines(ArrayList<ArrayList<Integer>> twoDArray) {
        int height = twoDArray.size();
        int width = twoDArray.get(0).size();
        int[][] temp_arr = new int[height][width];
        for (int i = 0; i < temp_arr.length - 1; i++) {
            for (int j = 0; j < temp_arr[0].length; j++) {
                temp_arr[i][j] = 0;
            }
        }
        ArrayList<Integer> labels = new ArrayList<>();
        int label = 1;
        int temp_r = 0;
        int prev_r = 0;
        int prev_c = 0;
        int c;
        int r;
        for (r = 1; r < twoDArray.size() - 1; r++) {
            for (c = 1; c < twoDArray.get(0).size() - 1; c++) {
                if (twoDArray.get(r).get(c) == 255 && (temp_arr[r][c] == label || temp_arr[r][c] == 0)) {
                    temp_arr[r][c] = label;
                    if (!labels.contains(label)) {
                        labels.add(label);
                        temp_r = r;
                        prev_r = r;
                        prev_c = c;
                    }
                    int[] neighbours_x = {r - 1, r - 1, r - 1, r, r, r + 1, r + 1, r + 1};
                    int[] neighbours_y = {c + 1, c, c - 1, c + 1, c - 1, c + 1, c, c - 1};
                    int n_count = 0;
                    int curr_r = 0;
                    int curr_c = 0;
                    for (int n1 = 0; n1 < neighbours_x.length; n1++) {
                        if (twoDArray.get(neighbours_x[n1]).get(neighbours_y[n1]) != 0)
                            n_count++;
                    }

                    for (int n = 0; n < neighbours_x.length; n++) {
                        if (twoDArray.get(neighbours_x[n]).get(neighbours_y[n]) != 0) {
                            curr_r = neighbours_x[n];
                            curr_c = neighbours_y[n];
                        }
                        if (twoDArray.get(neighbours_x[n]).get(neighbours_y[n]) == 255 && (neighbours_x[n] != prev_r || neighbours_y[n] != prev_c)) {
                            temp_arr[curr_r][curr_c] = label;
                            prev_r = r;
                            prev_c = c;
                            if (neighbours_x[n] >= prev_r || (neighbours_y[n] <= prev_c || neighbours_y[n] >= prev_c)) {
                                r = neighbours_x[n];
                                c = neighbours_y[n] - 1;
                                break;
                            }

                        }

                    }
                    if (n_count > 2) {
                        temp_arr[r][c] = temp_arr[r - 1][c];
                    } else if (n_count == 1 && (curr_r == prev_r && curr_c == prev_c)) {
                        label++;
                        r = temp_r;
                        break;
                    } else if (n_count == 0) {
                        label++;
                        r = temp_r - 1;
                        break;

                    }
                }

            }
        }


        ArrayList<ArrayList<Integer>> final_arr = new ArrayList<>();
        for (int[] ints : temp_arr) {
            ArrayList<Integer> list = new ArrayList<>();
            for (int i : ints) {
                list.add(i);
            }
            final_arr.add(list);
        }
        return final_arr;
    }

    /**
     * Removes the overlapping paths in the image.
     *
     * @param twoDArray Two-dimensional array of input image.
     * @return List of unique labels without overlaps.
     */
    public ArrayList<Object> removeOverlaps(ArrayList<ArrayList<Integer>> twoDArray) {
        ArrayList<Object> unique_labels = new ArrayList<>();
        ArrayList<Object> overlaps = new ArrayList<>();
        int n, i, j ;
        paths = new ArrayList<>();
        for (i = 0; i < twoDArray.size() - 1; i++) {
            for (j = 0; j < twoDArray.get(0).size() - 1; j++) {
                int[] neighbours = new int[0];
                if (twoDArray.get(i).get(j) != 0 && twoDArray.get(i).get(j) != 9999)
                    neighbours = findNeighbours(twoDArray, i, j);
                for (n = 0; n < neighbours.length; n++) {
                    if (neighbours[n] == 9999 && !overlaps.contains(twoDArray.get(i).get(j))) {
                        overlaps.add(twoDArray.get(i).get(j));
                        break;
                    }
                }
                if (twoDArray.get(i).get(j) != 0 && !unique_labels.contains(twoDArray.get(i).get(j)) && twoDArray.get(i).get(j) != 9999)
                    unique_labels.add(twoDArray.get(i).get(j));
            }
        }
//        System.out.println(overlaps);
        unique_labels.removeAll(overlaps);
        return unique_labels;
    }

    /**
     * Returns the 8 connected neighbours of each pixel.
     *
     * @param twoDArray Two-dimensional array of input image.
     * @param r         x coordinate of pixel.
     * @param c         y coordinate of pixel.
     * @return Array containing pixel intensities of 8 directly connected neighbours of each pixel
     */
    public int[] findNeighbours(ArrayList<ArrayList<Integer>> twoDArray, int r, int c) {

        return new int[]{twoDArray.get(r + 1).get(max(c - 1, 0)),
                twoDArray.get(r + 1).get(c),
                twoDArray.get(r + 1).get(c + 1),
                twoDArray.get(r).get(max(c - 1, 0)),
                twoDArray.get(r).get(c + 1),
                twoDArray.get(max(r - 1, 0)).get(max(c - 1, 0)),
                twoDArray.get(max(r - 1, 0)).get(c),
                twoDArray.get(max(r - 1, 0)).get(c + 1)};
    }

    /**
     * Returns number of 8-connected neighbours having same label as input label.
     *
     * @param r x coordinate of pixel.
     * @param c y coordinate of pixel.
     * @param label label of current pixel.
     * @param twoDArray Two-dimensional array of input image.
     * @return Number of pixels with same label as input.
     */
    public int getNeighbourCount(int r, int c, int label, ArrayList<ArrayList<Integer>> twoDArray) {
        int[] neighbours_x = {r - 1, r - 1, r - 1, r, r, r + 1, r + 1, r + 1};
        int[] neighbours_y = {c + 1, c, c - 1, c - 1, c + 1, c + 1, c, c - 1};
        for (n = 0; n < neighbours_x.length; n++) {
            if (twoDArray.get(neighbours_x[n]).get(neighbours_y[n]) == label)
                n_count++;
        }
        return n_count;
    }

    /**
     * Filters cell paths based on min/max lengths.
     *
     * @param num_points number of pixels
     * @param polyLine Floatpolygon of path traversed by cell.
     */
    public void filterLines(int num_points, FloatPolygon polyLine) {
            if (num_points >= 15 && num_points <= 300)
                paths.add(polyLine);
    }

    /**
     * Calculates the length of actual path traversed by the cell.
     *
     * @param twoDArray Two-dimensional array of input image.
     */
    public void calculateLength(ArrayList<ArrayList<Integer>> twoDArray) {
        ArrayList<Object> unique_labels = removeOverlaps(twoDArray);
        for (Object unique_label : unique_labels) {
            int num_points = 0;
            FloatPolygon polyLine = new FloatPolygon();
            for (int r = 1; r < twoDArray.size() - 1; r++)
                for (int c = 1; c < twoDArray.get(0).size() - 1; c++) {
                    int[] neighbours_x = {r - 1, r - 1, r - 1, r, r, r + 1, r + 1, r + 1},neighbours_y = {c + 1, c, c - 1, c - 1, c + 1, c + 1, c, c - 1};
                    n_count = 0;
                    if (twoDArray.get(r).get(c) == (int) unique_label) {
                        if (num_points == 0) {
                            prev_r = r;prev_c = c;
                            n_count = getNeighbourCount(r, c, (int) unique_label, twoDArray);
                            if (n_count >= 2) break;
                        }
                        num_points++;
                        n_count = 0;
                        for (n = 0; n < neighbours_x.length; n++)
                            if (twoDArray.get(neighbours_x[n]).get(neighbours_y[n]) == (int) unique_label && (neighbours_x[n] >= prev_r || (neighbours_y[n] >= prev_c || neighbours_y[n] <= prev_c))) {
                                prev_r = r;prev_c = c;
                                curr_r = neighbours_x[n];curr_c = neighbours_y[n];
                                n_count++;
                                polyLine.addPoint((float) neighbours_x[n], (float) neighbours_y[n]);
                                r = neighbours_x[n]; c = neighbours_y[n] - 1;
                                twoDArray.get(prev_r).set(prev_c, 255);
                                break;
                            }
                        if (n_count == 1 && (curr_r == prev_r && curr_c == prev_c)) break;
                        else if (n_count == 0) break;
                    }
                }
            filterLines(num_points,polyLine);
        }
    }

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

    /**
     * Calculates the straight length between the 2 end points of the path.
     *
     * @param x1 x-coordinate of 1st pixel.
     * @param x2 x-coordinate of last pixel.
     * @param y1 y-coordinate of 1st pixel.
     * @param y2 y-coordinate of last pixel.
     * @return straight length of path.
     */
    public double calculateLen(float x1, float x2, float y1, float y2) {
        return Math.sqrt(Math.pow(abs(x1 - x2), 2.0) + Math.pow(abs(y1 - y2), 2.0));
    }

    /**
     * Draw contours along the cell paths in the image.
     *
     * @param imp Input image
     */
    public void displayContours(ImagePlus imp, ArrayList<ArrayList<Integer>> twoDArray) {
        imp.setOverlay(null);
        Overlay ovpoly = new Overlay();
        int id = 1;
        for (FloatPolygon path : paths) {
            BSpline sp = new BSpline();
            FloatPolygon contours = adjustEndPoints(path, twoDArray);
            double len = calculateLen(contours.xpoints[0], contours.xpoints[contours.npoints - 1], contours.ypoints[0], contours.ypoints[contours.npoints - 1]);
            double area = contours.getLength(true);
            sp.bsplineCurvature(contours);
            double curvature = sp.computeAvgCurvature();
            PolygonRoi polyRoiMitte = new PolygonRoi(contours, Roi.POLYLINE);
            polyRoiMitte.setStrokeColor(Color.red);
            ovpoly.add(polyRoiMitte);
            TextRoi tr = new TextRoi((int) contours.xpoints[contours.npoints / 2], (int) contours.ypoints[contours.npoints / 2], "" + id);
            tr.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
            tr.setIgnoreClipRect(true);
            tr.setStrokeColor(Color.orange);
            ovpoly.add(tr);
            LengthAnalysisTool_1 l1 = new LengthAnalysisTool_1();
            l1.createResultsTable(id, len, area, curvature);
            id++;
        }
        if (ovpoly.size() > 0)
            imp.setOverlay(ovpoly);
        imp.show();
    }
}
