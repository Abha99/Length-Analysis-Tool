/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */


// Code without opencv implementation

package com.uga.imagej;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.gui.GenericDialog;
import ij.measure.ResultsTable;
import ij.plugin.filter.ExtendedPlugInFilter;
import ij.plugin.filter.PlugInFilter;
import ij.plugin.filter.PlugInFilterRunner;
import ij.process.ImageProcessor;
import java.util.ArrayList;


/**
 * This class runs the plugin for automated length analysis of paths traced by cells.
 * This plugin is executed using the ImageJ software which is available
 * on {@see https://imagej.net/software/fiji/}
 *
 * @author Abha Ingle
 * @version 1.0
 * @since 09-01-2022
 */
public class Main implements PlugInFilter {
    protected ImagePlus image;
    public double value;
    public String name;
    ArrayList<ArrayList<Integer>> twoDArray;
    public static int min=10,max=200,expTime=1; //min max in pixels, time in seconds
    public static float magnification=0.1365F; //micro meter

    public ResultsTable table = ResultsTable.getResultsTable();
    int brightnessValue=-1, contrastValue=-1;

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
        // Abort macro if no image is open
        if (imp == null) {
            IJ.error("No image open");
            return DONE;
        }
//        readSettings();
        return DOES_8G + DOES_STACKS + SUPPORTS_MASKING;
    }


    /**
     * This method is used to run the ImageJ plugin
     *
     * @param ip Image processor of input image
     */
    @Override
    public void run(ImageProcessor ip) {

        ImageProcessor ip1 = ip.duplicate();
        ImagePlus input = new ImagePlus("Input Image",ip1);
        input.show();
        ImgPreProcessing pp = new ImgPreProcessing();
        ImageProcessor pre_processed = pp.preProcessing(ip1,image);
        ImagePlus resultImg = new ImagePlus("Result image",pre_processed);
        resultImg.show();
        twoDArray = pp.imgToArray(pre_processed,twoDArray);
        Junctions j = new Junctions();
        j.findJunctions(twoDArray);
        Contours c = new Contours();
        ArrayList<ArrayList<Integer>> lines = c.findLines(twoDArray);
        j.fixJunctions(lines);
        c.calculateLength(lines,min,max);
        c.displayContours(resultImg,lines,magnification,expTime);
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
        Class<?> clazz = Main.class;
        java.net.URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
        java.io.File file = new java.io.File(url.toURI());
        System.setProperty("plugins.dir", file.getAbsolutePath());
        new ImageJ();
        ImagePlus image = IJ.openImage("src/g1-0005.jpg");
        image.show();
        IJ.runPlugIn(clazz.getName(), "");

    }
    //    /**
//     * Read settings.
//     */
//    private void readSettings() {
//        min= (int) Prefs.get("Main.min", min);
//        max = (int) Prefs.get("Main.max", max);
//        magnification = (float) Prefs.get("Main.magnification", magnification);
//        expTime = (int) Prefs.get("Main.expTime", expTime);
//
//    }
//    /*
//     * (non-Javadoc)
//     *
//     * @see ij.plugin.filter.ExtendedPlugInFilter#setNPasses(int)
//     */
//    @Override
//    public void setNPasses(int nPasses) {
//        IJ.showProgress(nPasses, image.getNSlices());
//    }

    //    ImageProcessor changeContrast(ImageProcessor ip, int contrast) {
//        ip.multiply(contrast / 50.0);
//        return ip;
//    }
//    ImageProcessor changeBrightness(ImageProcessor ip,int brightness) {
//        ip.multiply(1.0 / Math.exp(-brightness / 100.0));
//        return ip;
//    }

    /*
     * (non-Javadoc)
     *
     * @see ij.plugin.filter.ExtendedPlugInFilter#showDialog(ij.ImagePlus,
     * java.lang.String, ij.plugin.filter.PlugInFilterRunner)
     */
//    @Override
//    public int showDialog(ImagePlus image, String command, PlugInFilterRunner pfr) {
//        ImageProcessor ip = image.getProcessor();
//        GenericDialog gd = new GenericDialog("Adjust Image");
//        gd.addMessage("Adjust Brightness and Contrast");
//        gd.addSlider("Brightness", 0, 255, 128);
//        brightnessValue = (int) gd.getNextNumber();
//        gd.addSlider("Contrast", 0, 255, 128);
//        contrastValue = (int) gd.getNextNumber();
//        gd.addMessage("----------------------------------------------------------------------------");
//        gd.addMessage("Length(pixels)");
//        gd.addNumericField("Min:", 0, 0);
//        gd.addNumericField("Max:", 200, 0);
//
//        gd.addMessage("----------------------------------------------------------------------------");
//        gd.addMessage("Magnification and Exposure time");
//        gd.addNumericField("Magnification:", 1.365, 3);
//        gd.addNumericField("Exposure Time:", 1, 0);
//        min = (int)gd.getNextNumber();
//        max = (int) gd.getNextNumber();
//        magnification = (float)gd.getNextNumber();
//        expTime = (int) gd.getNextNumber();
//        gd.showDialog();
//        if(gd.wasOKed()) {
//            if(brightnessValue != 128)
//                ip = changeBrightness(ip,brightnessValue);
//            if(contrastValue != 128)
//                ip = changeContrast(ip,contrastValue);
//        }
////        saveSettings();
//        int labels = IJ.setupDialog(image, DOES_8G + DOES_STACKS + SUPPORTS_MASKING);
////        doStack = (labels != DOES_8G + FINAL_PROCESSING + PARALLELIZE_STACKS);
//
//        return labels;
//    }
//
//    private void saveSettings() {
//        Prefs.set("Main.min", min);
//        Prefs.set("Main.max", max);
//        Prefs.set("Main.magnification", magnification);
//        Prefs.set("Main.expTime", expTime);
//    }
}



