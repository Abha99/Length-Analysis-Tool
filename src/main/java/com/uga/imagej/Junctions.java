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
                    Contours c1 = new Contours();
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

}
