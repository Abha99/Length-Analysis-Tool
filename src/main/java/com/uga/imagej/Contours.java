package com.uga.imagej;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.*;
import ij.process.FloatPolygon;
import ij.process.ImageProcessor;
import ijopencv.ij.ImagePlusMatConverter;
import ijopencv.opencv.MatImagePlusConverter;
import ijopencv.opencv.MatVectorListPolygonRoiConverter;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_imgproc;
import org.opencv.core.*;
import java.awt.*;
import java.util.ArrayList;
import static java.lang.Math.abs;

public class Contours {

    public ArrayList<PolygonRoi> findContours(ImageProcessor ip) {
        ImagePlus imp = new ImagePlus("Input Image",ip);
        MatVectorListPolygonRoiConverter pc = new MatVectorListPolygonRoiConverter();
        ImagePlusMatConverter ic = new ImagePlusMatConverter();
        MatImagePlusConverter mip = new MatImagePlusConverter();
        opencv_core.Mat src = ic.convert(imp, opencv_core.Mat.class);
        opencv_core.MatVector contours = new opencv_core.MatVector();
        opencv_imgproc.findContours(src, contours, 3, 2);
        ArrayList<PolygonRoi> contoursROI = new ArrayList();
        contoursROI = (ArrayList)pc.convert(contours, contoursROI.getClass());
//        RoiManager rm = new RoiManager();
//        rm.setVisible(true);
//        Iterator var8 = contoursROI.iterator();
//
//        while(var8.hasNext()) {
//            PolygonRoi contoursROI1 = (PolygonRoi)var8.next();
//            rm.add(imp, contoursROI1, 0);
//        }
        return contoursROI;
    }
    public void drawContours( ImagePlus imp, ArrayList<PolygonRoi> contoursROI) {
        imp.setOverlay(null);
        Overlay ovpoly = new Overlay();
        int id = 1;
        double actual_length =0;
        double straight_length = 0;
        for(int i=0;i<contoursROI.size();i++){
            FloatPolygon contour = contoursROI.get(i).getFloatPolygon();
            contoursROI.get(i).setStrokeColor(Color.red);
            contoursROI.get(i).setStrokeWidth(0.5);
            if(contour.npoints > 5) {
                ovpoly.add( contoursROI.get(i));
                int posx = (int) contour.xpoints[contour.npoints / 2];
                int posy = (int) contour.ypoints[contour.npoints / 2];
                TextRoi tr = new TextRoi(posx, posy, "" + id);
                tr.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
                tr.setIgnoreClipRect(true);
                tr.setStrokeColor(Color.orange);
                ovpoly.add(tr);
                actual_length = getContourLength(contour);
                straight_length = getStraightLength(contour);
                LengthAnalysisTool_1 l1 = new LengthAnalysisTool_1();
                l1.createResultsTable(id,straight_length,actual_length);
                id++;
            }
        }
        if (ovpoly.size() > 0) {
            imp.setOverlay(ovpoly);
        }
        imp.show();
    }

    public void removeOverlappingContours(ArrayList<PolygonRoi> contours, ArrayList xpoints, ArrayList ypoints) {
        for(int i=0;i<contours.size();i++) {
            for(int j=0;j< xpoints.size();j++) {
                if(contours.get(i).contains((int)xpoints.get(j),(int)ypoints.get(j))) {
                    contours.remove(contours.get(i));
                    System.out.println("Contour at x="+xpoints.get(j)+" y="+ypoints.get(j)+" removed");
                }
            }
        }
    }

    public double getContourLength(FloatPolygon contour) {
        return contour.getLength(true);
    }

    public double getStraightLength(FloatPolygon contour) {
        return contour.getLength(false);
    }




}
