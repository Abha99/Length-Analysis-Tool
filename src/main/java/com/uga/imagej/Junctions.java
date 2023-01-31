package com.uga.imagej;

import java.util.ArrayList;

import static java.lang.Math.max;

public class Junctions {
    public ArrayList<Integer> junctions_x = new ArrayList<>();
    public ArrayList<Integer> junctions_y = new ArrayList<>();

    /**
     * This method is used to find the 8 connected neighbours of each pixel.
     *
     * @param twoDArray Two-dimensional array of input image.
     * @param r         x coordinate of pixel.
     * @param c         y coordinate of pixel.
     * @return Returns an array containing pixel intensities of 8 directly connected neighbours of each pixel
     */
    public int[] findNeighbours(ArrayList<ArrayList<Integer>> twoDArray, int r, int c) {

        return new int[]{twoDArray.get(r + 1).get(Math.max(c - 1, 0)),
                twoDArray.get(r + 1).get(c),
                twoDArray.get(r + 1).get(c + 1),
                twoDArray.get(r).get(max(c - 1, 0)),
                twoDArray.get(r).get(c + 1),
                twoDArray.get(Math.max(r - 1, 0)).get(max(c - 1, 0)),
                twoDArray.get(max(r - 1, 0)).get(c),
                twoDArray.get(Math.max(r - 1, 0)).get(c + 1)};
    }

    public void findJunctions(ArrayList<ArrayList<Integer>> twoDArray) {
        int n_count;
        for(int r=1;r<twoDArray.size()-1;r++) {
            for(int c=0;c<twoDArray.get(0).size()-1;c++) {
                n_count=0;
                if(twoDArray.get(r).get(c) !=0) {
                    int[] neighbours = findNeighbours(twoDArray,r,c);
                    for (int neighbour : neighbours) {
                        if (neighbour != 0)
                            n_count++;
                    }
                    if(n_count>2) {
                        junctions_x.add((int)r);
                        junctions_y.add((int)c);
                    }
                }
            }
        }
        fixJunctions(twoDArray);
    }

    public void fixJunctions(ArrayList<ArrayList<Integer>> twoDArray) {
        int[] junc_x = new int[junctions_x.size()];
        int[] junc_y = new int[junctions_y.size()];
        int i=0;
        int j=0;
        int value = 9999;
        for (Integer f : junctions_x) {
            junc_x[i++] = (f != null ? f : 0);
        }
        for (Integer f : junctions_y) {
            junc_y[j++] = (f != null ? f : 0);
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
