/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */


// Code without opencv implementation

package com.uga.imagej;


import fiji.threshold.Auto_Threshold;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.*;
import ij.measure.ResultsTable;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatPolygon;
import ij.process.ImageProcessor;
import ijopencv.ij.ImagePlusMatConverter;
import inra.ijpb.morphology.Reconstruction;
import inra.ijpb.morphology.attrfilt.AreaOpeningQueue;
import inra.ijpb.plugins.Connectivity2D;
import sc.fiji.skeletonize3D.Skeletonize3D_;

import java.awt.*;
import java.util.ArrayList;

import org.bytedeco.javacpp.opencv_core;
import static java.lang.Math.max;

/**
 * This class runs the plugin for automated length analysis of paths traced by cells.
 * This plugin is executed using the ImageJ software which is available
 * on {@see https://imagej.net/software/fiji/}
 *
 * @author Abha Ingle
 * @version 1.0
 * @since 09-01-2022
 */
public class LengthAnalysisTool_1 implements PlugInFilter {
    protected ImagePlus image;
    public double value;
    public String name;
    ArrayList<com.uga.imagej.Line> results;
    Connectivity2D connectivity;
    ArrayList<ArrayList<Integer>> twoDArray;
    ArrayList<Float> junctions_x = new ArrayList<>();
    ArrayList<Float> junctions_y = new ArrayList<>();
    //	float[] junc_x;
//	float[] junc_y;
    public ResultsTable table = ResultsTable.getResultsTable();

    /**
     * This method is used to set up the plugin for ImageJ
     *
     * @param arg arguments passed by user
     * @param imp Input image
     * @return Input image converted to gray scale
     */
    @Override
    public int setup(String arg, ImagePlus imp) {
        if (arg.equals("about")) {
            return DONE;
        }
        image = imp;
        return DOES_8G + DOES_STACKS + SUPPORTS_MASKING;
    }

    /**
     * This method is used to generate the results table
     *
     * @param id   Unique identifier for each cell
     * @param len1 Distance between end points of path traced by cells
     * @param len2 Actual distance of path traced by the cell
     */
    public void createResultsTable(int id, double len1, double len2) {
        table.incrementCounter();
        table.setPrecision(3);
        table.addValue("Id", id);
        table.addValue("Length", len1);
        table.addValue("Length in pixels", len2);
    }

    /**
     * This method is used to draw contours along the cell paths in the image.
     *
     * @param imp Input image
     */
    private void displayContours(ImagePlus imp) {
        imp.setOverlay(null);
        Overlay ovpoly = new Overlay();
        float px, py;
        double len;
        int id = 1;
        // Print contour and boundary
        for (Line result : results) {
            FloatPolygon polyMitte = new FloatPolygon();
            int num_points = result.getNumber();
            float[] row = result.getXCoordinates();
            float[] col = result.getYCoordinates();
            len = result.calculate_len(row[0], row[num_points - 1], col[0], col[num_points - 1]);
            for (int j = 0; j < num_points; j += 1) {
                px = row[j];
                py = col[j];
                int num = 1;
                ArrayList<Object> sum = new ArrayList<>();
                for (int i = j ; i < row.length; i++) {
                    if (px == row[i]) {
                        num++;
                        sum.add(col[i]);
                    } else
                        break;
                }
                System.out.println(sum);
                if (num > 1) {
                    if (j != 0) {
                        float temp = 0;
                        for (Object o : sum) {
                            if (temp < (float) o) {
                                temp = (float) o;
                            }
                        }
                        py = temp;
                    } else {
                        float temp = 999;
                        for (Object o : sum) {
                            if (temp > (float) o) {
                                temp = (float) o;
                            }
                        }
                        py = temp;
                    }
                    col[j] = py;
                    for (int n = 0; n < num; n++)
                        col[j + n < num_points ? j + n : j] = py;
                    j += (num - 1);
                }

                polyMitte.addPoint((px), (py));
            }
            PolygonRoi polyRoiMitte = new PolygonRoi(polyMitte, Roi.FREELINE);
            polyRoiMitte.setStrokeColor(Color.red);
            ovpoly.add(polyRoiMitte);
            int posx = (int) polyMitte.xpoints[polyMitte.npoints / 2];
            int posy = (int) polyMitte.ypoints[polyMitte.npoints / 2];
            double area = polyMitte.getLength(true);
            TextRoi tr = new TextRoi(posx, posy, "" + id);
            tr.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
            tr.setIgnoreClipRect(true);
            tr.setStrokeColor(Color.orange);
            ovpoly.add(tr);
            createResultsTable(id, len, area);
            id++;
        }
        if (ovpoly.size() > 0) {
            imp.setOverlay(ovpoly);
        }
        imp.show();

    }

    /**
     * This method is used to find the 8 connected neighbours of each pixel.
     *
     * @param twoDArray Two-dimensional array of input image.
     * @param r         x coordinate of pixel.
     * @param c         y coordinate of pixel.
     * @return Returns an array containing pixel intensities of 8 directly connected neighbours of each pixel
     */
    public int[] find_neighbours(ArrayList<ArrayList<Integer>> twoDArray, int r, int c) {

        return new int[]{twoDArray.get(r + 1).get(Math.max(c - 1, 0)),
                twoDArray.get(r + 1).get(c),
                twoDArray.get(r + 1).get(c + 1),
                twoDArray.get(r).get(max(c - 1, 0)),
                twoDArray.get(r).get(c + 1),
                twoDArray.get(Math.max(r - 1, 0)).get(max(c - 1, 0)),
                twoDArray.get(max(r - 1, 0)).get(c),
                twoDArray.get(Math.max(r - 1, 0)).get(c + 1)};
    }

    /**
     * This method is used to calculate the length of actual path traversed by the cell.
     *
     * @param twoDArray Two-dimensional array of input image.
     */
    public void calculate_area(ArrayList<ArrayList<Integer>> twoDArray) {
        ArrayList<Object> unique_labels = new ArrayList<>();
        ArrayList<Object> overlaps = new ArrayList<>();
        int num_points;
        int n_count;
        int n;

        int i;
        int j;
//		float[] junc_x = new float[junctions_x.size()];
//		float[] junc_y = new float[junctions_y.size()];
//		for (Float f : junctions_x)
//			junc_x[i++] = (f != null ? f : Float.NaN); // Or whatever default you want.
//		for (Float f : junctions_y)
//			junc_y[j++] = (f != null ? f : Float.NaN); // Or whatever default you want.

        results = new ArrayList<>();
        for (i = 0; i < twoDArray.size() - 1; i++) {
            for (j = 0; j < twoDArray.get(0).size() - 1; j++) {
                int[] neighbours = new int[0];
                if (twoDArray.get(i).get(j) != 0 && twoDArray.get(i).get(j) != 9999)
                    neighbours = find_neighbours(twoDArray, i, j);
                for (n = 0; n < neighbours.length; n++) {
                    if (neighbours[n] == 9999 && !overlaps.contains(twoDArray.get(i).get(j))) {
                        overlaps.add(twoDArray.get(i).get(j));
                        break;
                    }
                }
                if (twoDArray.get(i).get(j) != 0 && !unique_labels.contains(twoDArray.get(i).get(j)) && twoDArray.get(i).get(j) != 9999) {
                    unique_labels.add(twoDArray.get(i).get(j));
                }


            }
        }
        unique_labels.removeAll(overlaps);
        com.uga.imagej.Line line;

        int prev_r = 0;
        int prev_c = 0;
        int curr_r = 0;
        int curr_c = 0;
        int c;

        for (Object unique_label : unique_labels) {
            num_points = 0;


            ArrayList<Float> x_coordinates = new ArrayList<>();
            ArrayList<Float> y_coordinates = new ArrayList<>();
            for (int r = 1; r < twoDArray.size() - 1; r++) {
                for (c = 1; c < twoDArray.get(0).size() - 1; c++) {
                    n_count = 0;
                    //(int) unique_labels.get(l)
                    if (twoDArray.get(r).get(c) == (int) unique_label) {
                        int[] neighbours_x = {r - 1, r - 1, r - 1, r, r, r + 1, r + 1, r + 1};
                        int[] neighbours_y = {c + 1, c, c - 1, c - 1, c + 1, c + 1, c, c - 1};
                        if (num_points == 0) {

                            prev_r = r;
                            prev_c = c;
                            for (n = 0; n < neighbours_x.length; n++) {
                                if (twoDArray.get(neighbours_x[n]).get(neighbours_y[n]) == (int) unique_label) {
                                    n_count++;
                                }
                            }
                            if (n_count >= 2) {
                                break;
                            }
                        }
                        num_points++;
                        n_count = 0;
                        for (n = 0; n < neighbours_x.length; n++) {
                            if (twoDArray.get(neighbours_x[n]).get(neighbours_y[n]) == (int) unique_label && (neighbours_x[n] >= prev_r || (neighbours_y[n] >= prev_c || neighbours_y[n] <= prev_c))) {

                                prev_r = r;
                                prev_c = c;
                                curr_r = neighbours_x[n];
                                curr_c = neighbours_y[n];
                                n_count++;
                                x_coordinates.add((float) neighbours_x[n]);
                                y_coordinates.add((float) neighbours_y[n]);
                                r = neighbours_x[n];
                                c = neighbours_y[n] - 1;
                                twoDArray.get(prev_r).set(prev_c, 255);
                                break;
                            }

                        }
                        if (n_count == 1 && (curr_r == prev_r && curr_c == prev_c)) {
                            break;
                        } else if (n_count == 0) {
                            break;
                        }
                    }
//					}

                }
            }

            final float[] row = new float[x_coordinates.size()];
            int index1 = 0;
            for (final Float value : x_coordinates) {
                row[index1++] = value;
            }
            final float[] col = new float[y_coordinates.size()];
            int index2 = 0;
            for (final Float value : y_coordinates) {
                col[index2++] = value;
            }

            if (row.length != 0 && col.length != 0) {
                int start_x = (int) row[0];
                int start_y = (int) col[0];
                int end_x = (int) row[row.length - 1];
                int end_y = (int) col[col.length - 1];
                int[] start_neighbours = find_neighbours(twoDArray, start_x, start_y);
                int[] end_neighbours = find_neighbours(twoDArray, end_x, end_y);
                for (int s = 0; s < start_neighbours.length; s++) {
                    if (start_neighbours[s] == 9999)
                        num_points = 0;
                    else if (end_neighbours[s] == 9999)
                        num_points = 0;

                }
            }

            if (num_points >= 10 && num_points <= 200) {
                line = new Line(row, col);
                results.add(line);
                x_coordinates.clear();
                y_coordinates.clear();

            }
        }
    }
    public void find_junctions(ArrayList<ArrayList<Integer>> twoDArray) {
        int n_count;
        for(int r=1;r<twoDArray.size()-1;r++) {
            for(int c=0;c<twoDArray.get(0).size()-1;c++) {
                n_count=0;
                if(twoDArray.get(r).get(c) !=0) {
                    int[] neighbours = find_neighbours(twoDArray,r,c);
                    for (int neighbour : neighbours) {
                        if (neighbour != 0)
                            n_count++;
                    }
                    if(n_count>2) {
                        junctions_x.add((float)r);
                        junctions_y.add((float)c);

                    }

                }

            }
        }
    }
    public void fix_junctions(ArrayList<ArrayList<Integer>> twoDArray) {
        float[] junc_x = new float[junctions_x.size()];
        float[] junc_y = new float[junctions_y.size()];
        int i=0;
        int j=0;
        int value = 9999;
        for (Float f : junctions_x) {
            junc_x[i++] = (f != null ? f : Float.NaN);
        }
        for (Float f : junctions_y) {
            junc_y[j++] = (f != null ? f : Float.NaN);
        }

        for(i=0;i<junc_x.length;i++) {
            twoDArray.get((int)junc_x[i]).set((int)junc_y[i],value);
        }

        for(i=0;i<junc_x.length;i++) {
            int r=(int)junc_x[i];
            int c=(int)junc_y[i];
            int start_r=r;
            int start_c=c;

            if(twoDArray.get(r).get(c)!=0){
                twoDArray.get(r).set(c,value);
                while(r!=0 && c!=0) {
                    int[] top_x = { r - 1, r - 1, r - 1,r,r};
                    int[] top_y = { c + 1, c, c - 1,c+1,c-1};

                    int count_top = 0;
                    int count_bot=0;
                    int count_l=0;
                    int count_r=0;
                    int l;
                    for (int n = 0; n < top_x.length; n++) {
                        if (twoDArray.get(top_x[n]).get(top_y[n]) != value && twoDArray.get(top_x[n]).get(top_y[n]) != 0) {
                            count_top++;
                            twoDArray.get(top_x[n]).set(top_y[n], value);
                            r = top_x[n];
                            c = top_y[n];
                        }
                    }
                    if(count_top==0) {
                        r=max(r,start_r);
                        c=max(c,start_c);
                        int[] bot_x = {r + 1, r + 1, r + 1,r,r};
                        int[] bot_y = {c+1,c,c-1,c+1,c-1};
                        for (int n = 0; n < bot_x.length; n++) {
                            if (twoDArray.get(bot_x[n]).get(bot_y[n]) != value && twoDArray.get(bot_x[n]).get(bot_y[n]) != 0) {
                                count_bot++;
                                twoDArray.get(bot_x[n]).set(bot_y[n], value);
                                r = bot_x[n];
                                c = bot_y[n];
                            }

                        }
                        if(count_bot==0)
                        {
                            r=max(r,start_r);
                            c=max(c,start_c);
                            int[] l_x = {r-1,r-1,r-1};
                            int[] l_y = {c-1,c,c+1};
                            for(l=0;l<l_x.length;l++) {
                                if (twoDArray.get(l_x[l]).get(l_y[l]) != value && twoDArray.get(l_x[l]).get(l_y[l]) != 0) {
                                    count_l++;
                                    twoDArray.get(l_x[l]).set(l_y[l], value);
                                    r = l_x[l];
                                    c = l_y[l];
                                }
                            }
                            if(count_l==0) {
                                r=max(r,start_r);
                                c=max(c,start_c);
                                int[] r_x = {r+1,r+1,r+1};
                                int[] r_y = {c-1,c,c+1};
                                for(l=0;l<r_x.length;l++) {
                                    if (twoDArray.get(r_x[l]).get(r_y[l]) != value && twoDArray.get(r_x[l]).get(r_y[l]) != 0) {
                                        count_r++;
                                        twoDArray.get(r_x[l]).set(r_y[l], value);
                                        r = r_x[l];
                                        c = r_y[l];
                                    }
                                }
                                if(count_r==0)
                                    break;
                            }

                        }

                    }
                }
            }
        }

    }

    /**
     * This method used the connected component labelling algorithm to find the paths
     * traversed by the cells in an image and assign them unique id's.
     *
     * @param twoDArray Two-dimensional array of input image.
     */
    public void find_lines(ArrayList<ArrayList<Integer>> twoDArray) {

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

        fix_junctions(final_arr);
        calculate_area(final_arr);

    }

    /**
     * This method is used to run the ImageJ plugin
     *
     * @param ip Image processor of input image
     */
    @Override
    public void run(ImageProcessor ip) {

        ImageProcessor ip1 = ip.duplicate();
        //test code -start
        ImagePlusMatConverter cv = new ImagePlusMatConverter();
        opencv_core.Mat input = cv.toMat(ip1);

        //test code - end

        ip1.gamma(1.15);
        ip1.blurGaussian(1);
        ImagePlus image = new ImagePlus("input image", ip1);

        Auto_Threshold auto = new Auto_Threshold();
        auto.exec(image, "Otsu", true, false, true, false, false, false);
        ImageProcessor img = image.getProcessor();
        ImageProcessor result = Reconstruction.killBorders(img);
        Skeletonize3D_ sk = new Skeletonize3D_();
        ImagePlus resultPlus;
        resultPlus = new ImagePlus("killBorders", result);
        sk.setup("", resultPlus);
        ImageProcessor rp = resultPlus.getProcessor();
        sk.run(rp);
        resultPlus.show();
        AreaOpeningQueue algo = new AreaOpeningQueue();
        connectivity = Connectivity2D.C8;
        algo.setConnectivity(connectivity.getValue());
        ImageProcessor fl = algo.process(rp, 150);
        double maxDiff = 0.0;
        for (int i = 0; i < fl.getPixelCount(); ++i) {
            float diff = Math.abs(fl.getf(i) - rp.getf(i));
            fl.setf(i, diff);
            maxDiff = Math.max(diff, maxDiff);
        }
        fl.setMinAndMax(0.0, maxDiff);
        twoDArray = new ArrayList<>();

        for (int p1 = 0; p1 < rp.getWidth() - 1; p1++) {
            twoDArray.add(new ArrayList<>());
            for (int p2 = 0; p2 < rp.getHeight() - 1; p2++) {
                twoDArray.get(p1).add(p2, rp.getPixel(p1, p2));
            }
        }

        find_junctions(twoDArray);
        find_lines(twoDArray);

        displayContours(resultPlus);
        table.show("Results");

    }

    /**
     * This is the main method which starts ImageJ and
     * calls the run method to execute the length analysis tool plugin.
     *
     * @param args Unused
     * @throws Exception Runtime exception
     */
    public static void main(String[] args) throws Exception {
        Class<?> clazz = LengthAnalysisTool_1.class;
        java.net.URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
        java.io.File file = new java.io.File(url.toURI());
        System.setProperty("plugins.dir", file.getAbsolutePath());
        new ImageJ();
        ImagePlus image = IJ.openImage("C:/Users/ASUS/Desktop/abha_RA/Path_1s_red/mst1_plate2-0005.jpg");
        image.show();

        // run the plugin
        IJ.runPlugIn(clazz.getName(), "");

    }

}
