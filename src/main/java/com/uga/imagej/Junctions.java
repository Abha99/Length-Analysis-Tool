package com.uga.imagej;

import java.util.ArrayList;

import static java.lang.Math.max;

/**
 * Stores junctions formed by overlapping cell contours.
 */
public class Junctions {
    public ArrayList<Float> junctions_x = new ArrayList<>(),junctions_y = new ArrayList<>();

    /**
     * Find junctions of overlapping paths.
     *
     * @param twoDArray Two-dimensional array of input image.
     */
    public void findJunctions(ArrayList<ArrayList<Integer>> twoDArray) {
        int n_count;
        for(int r=1;r<twoDArray.size()-1;r++) {
            for(int c=0;c<twoDArray.get(0).size()-1;c++) {
                n_count=0;
                if(twoDArray.get(r).get(c) !=0) {
                    ConnectedNeighbours c1 = new ConnectedNeighbours();
                    int[] neighbours = c1.findNeighbours(twoDArray,r,c);
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


    private int[] getNCount(int[] xpoints,int[] ypoints,ArrayList<ArrayList<Integer>> twoDArray ) {
        int value=9999;
        int count=0,r=0,c=0;
        for (int n = 0; n < xpoints.length; n++) {
            if (twoDArray.get(xpoints[n]).get(ypoints[n]) != value && twoDArray.get(xpoints[n]).get(ypoints[n]) != 0) {
                count++;
                twoDArray.get(xpoints[n]).set(ypoints[n], value);
                r = xpoints[n]; c = ypoints[n];
            }
        }
        return new int[]{count, r, c};
    }

    /**
     * Fix pixels values near junctions.
     *
     * @param twoDArray Two-dimensional array of input image.
     */
    public void fixJunctions(ArrayList<ArrayList<Integer>> twoDArray) {
        float[] junc_x = new float[junctions_x.size()], junc_y = new float[junctions_y.size()];
        int i=0, j=0, value = 9999;
        for (Float f : junctions_x) junc_x[i++] = (f != null ? f : Float.NaN); for (Float f : junctions_y) junc_y[j++] = (f != null ? f : Float.NaN);
        for(i=0;i<junc_x.length;i++) twoDArray.get((int)junc_x[i]).set((int)junc_y[i],value);
        for(i=0;i<junc_x.length;i++) {
            int r=(int)junc_x[i], c=(int)junc_y[i], start_r=r, start_c=c;
            if(twoDArray.get(r).get(c)!=0){
                twoDArray.get(r).set(c,value);
                while(r!=0 && c!=0) {
                    int[] top_x = { r - 1, r - 1, r - 1,r,r}, top_y = { c + 1, c, c - 1,c+1,c-1};
                    int count_top ,count_bot,count_l,count_r;
                    int[] ret = getNCount(top_x,top_y,twoDArray);
                    count_top=ret[0]; r = ret[1];c=ret[2];
                    if(count_top==0) {
                        r=max(r,start_r); c=max(c,start_c);
                        int[] bot_x = {r + 1, r + 1, r + 1,r,r}, bot_y = {c+1,c,c-1,c+1,c-1};
                        ret = getNCount(bot_x,bot_y,twoDArray);
                        count_bot=ret[0]; r = ret[1];c=ret[2];
                        if(count_bot==0)
                        {
                            r=max(r,start_r); c=max(c,start_c);
                            int[] l_x = {r-1,r-1,r-1},l_y = {c-1,c,c+1};
                            ret = getNCount(l_x,l_y,twoDArray);
                            count_l=ret[0]; r = ret[1];c=ret[2];
                            if(count_l==0) {
                                r=max(r,start_r); c=max(c,start_c);
                                int[] r_x = {r+1,r+1,r+1},r_y = {c-1,c,c+1};
                                ret = getNCount(r_x,r_y,twoDArray);
                                count_r=ret[0]; r = ret[1];c=ret[2];
                                if(count_r==0) break;
                            }
                        }
                    }
                }
            }
        }
    }

}
