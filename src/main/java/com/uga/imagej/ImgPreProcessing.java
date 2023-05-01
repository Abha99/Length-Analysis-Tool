package com.uga.imagej;

import fiji.threshold.Auto_Threshold;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import inra.ijpb.morphology.Reconstruction;
import sc.fiji.skeletonize3D.Skeletonize3D_;
import java.util.ArrayList;

/**
 * Performs basic image processing and converts image to 2D array.
 **/
public class ImgPreProcessing {
    /**
     * Pre Processing Image.
     * @param input Input image.
     * @return processed image.
     */
    public ImageProcessor preProcessing(ImageProcessor input,ImagePlus image) {
        ImageProcessor ip1 = input.duplicate();
        //Gaussian blur
        ip1.gamma(1.15);
        ip1.blurGaussian(1);
        Auto_Threshold auto = new Auto_Threshold();
        auto.exec(image, "Otsu", true, false, true, false, false, false);
        ImageProcessor img = image.getProcessor();
        //Remove cells near borders
        ImageProcessor result = Reconstruction.killBorders(img);
        //Skeletonize
        Skeletonize3D_ sk = new Skeletonize3D_();
        ImagePlus resultPlus;
        resultPlus = new ImagePlus("killBorders", result);
        sk.setup("", resultPlus);
        ImageProcessor rp = resultPlus.getProcessor();
        sk.run(rp);
        return rp;

    }
    public ArrayList<ArrayList<Integer>> imgToArray(ImageProcessor ip, ArrayList<ArrayList<Integer>> twoDArray) {
        twoDArray = new ArrayList<>();
        for (int p1 = 0; p1 < ip.getWidth() - 1; p1++) {
            twoDArray.add(new ArrayList<>());
            for (int p2 = 0; p2 < ip.getHeight() - 1; p2++) {
                twoDArray.get(p1).add(p2, ip.getPixel(p1, p2));
            }
        }
        return twoDArray;
    }
}
