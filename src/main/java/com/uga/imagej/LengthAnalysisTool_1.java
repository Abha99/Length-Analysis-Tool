//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.uga.imagej;

import fiji.threshold.Auto_Threshold;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.measure.ResultsTable;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import inra.ijpb.morphology.Reconstruction;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import sc.fiji.skeletonize3D.Skeletonize3D_;

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

    ArrayList<ArrayList<Integer>> twoDArray;

    public ResultsTable table = ResultsTable.getResultsTable();

    public LengthAnalysisTool_1() {
    }
    /**
     * This method is used to set up the plugin for ImageJ
     *
     * @param arg arguments passed by user
     * @param imp Input image
     * @return Input image converted to gray scale
     */
    public int setup(String arg, ImagePlus imp) {
        if (arg.equals("about")) {
            return 4096;
        } else {
            this.image = imp;
            return 97;
        }
    }
    /**
     * This method is used to generate the results table
     *
     * @param id   Unique identifier for each cell
     * @param len1 Distance between end points of path traced by cells
     * @param len2 Actual distance of path traced by the cell
     */
    public void createResultsTable(int id, double len1, double len2,double curvature) {
        this.table.incrementCounter();
        this.table.setPrecision(3);
        this.table.addValue("Id", (double)id);
        this.table.addValue("Length", len1);
        this.table.addValue("Length in pixels", len2);
        int expTime = 1;
        double magnificationFactor = 1.365;
        this.table.addValue("Velocity(μm/s)",(len2/ expTime)* magnificationFactor);
        this.table.addValue("Curvature(μm)", curvature* magnificationFactor);
    }


    public ArrayList<ArrayList<Integer>> imgToArray(ImageProcessor ip) {
        twoDArray = new ArrayList<>();
        for (int p1 = 0; p1 < ip.getWidth() - 1; p1++) {
            twoDArray.add(new ArrayList<>());
            for (int p2 = 0; p2 < ip.getHeight() - 1; p2++) {
                twoDArray.get(p1).add(p2, ip.getPixel(p1, p2));
            }
        }
        return twoDArray;
    }
    /**
     * This method is used to run the ImageJ plugin
     *
     * @param ip Image processor of input image
     */
    public void run(ImageProcessor ip) {
        ImageProcessor ip1 = ip.duplicate();
        ip1.gamma(1.15);
        ip1.blurGaussian(1.0);
        ImagePlus image = new ImagePlus("input image", ip1);
        Auto_Threshold auto = new Auto_Threshold();
        auto.exec(image, "Otsu", true, false, true, false, false, false);
        ImageProcessor img = image.getProcessor();
        ImageProcessor result = Reconstruction.killBorders(img);
        Skeletonize3D_ sk = new Skeletonize3D_();
        ImagePlus resultPlus = new ImagePlus("killBorders", result);
        sk.setup("", resultPlus);
        ImageProcessor rp = resultPlus.getProcessor();
        sk.run(rp);
        resultPlus.show();

        this.twoDArray = imgToArray(rp);
        Contours c = new Contours();
        Junctions j = new Junctions();
        j.findJunctions(this.twoDArray);
        ArrayList<ArrayList<Integer>> lines = c.findLines(this.twoDArray);
        j.fixJunctions(lines);
        c.calculateLength(lines);
        c.displayContours(resultPlus,this.twoDArray);
        this.table.show("Results");
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
        URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
        File file = new File(url.toURI());
        System.setProperty("plugins.dir", file.getAbsolutePath());
        new ImageJ();
        ImagePlus image = IJ.openImage("src/Images/cc4405-0001.jpg");
        image.show();
        IJ.runPlugIn(clazz.getName(), "");
    }
}
