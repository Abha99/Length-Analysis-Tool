package com.uga.imagej;

import java.util.ArrayList;

import static java.lang.Math.max;
/**
 * Stores 8-connected neighbours of each pixel
 **/
public class ConnectedNeighbours {
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
        int n_count=0;
        int[] neighbours_x = {r - 1, r - 1, r - 1, r, r, r + 1, r + 1, r + 1};
        int[] neighbours_y = {c + 1, c, c - 1, c - 1, c + 1, c + 1, c, c - 1};
        for (int n = 0; n < neighbours_x.length; n++) {
            if (twoDArray.get(neighbours_x[n]).get(neighbours_y[n]) == label)
                n_count++;
        }
        return n_count;
    }
}
