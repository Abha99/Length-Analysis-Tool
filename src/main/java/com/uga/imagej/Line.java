package com.uga.imagej;


import static java.lang.Math.abs;

/**
 * This class stores information about line/path traced by each cell in the input image.
 */
public class Line {
    int num;
    float[] row;
    float[] col;
    static int idCounter = 0;
    private int id;
    private int frame;
    public Line() {
        this.assignID();
    }
    public Line(float[] x, float[] y) {
        this.assignID();
        this.col = x;
        this.row = y;
        this.num = x.length;
    }

    public float[] getXCoordinates() {
        return this.col;
    }

    public float[] getYCoordinates() {
        return this.row;
    }

    public int getNumber() {
        return this.num;
    }

    public double calculate_len(float x1,float x2,float y1,float y2) {
        return Math.sqrt(Math.pow(abs(x1 - x2), 2.0) + Math.pow(abs(y1 - y2), 2.0));
    }

    private synchronized void assignID() {
        this.id = idCounter++;
    }

    static void resetCounter() {
        idCounter = 0;
    }}
