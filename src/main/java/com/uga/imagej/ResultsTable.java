package com.uga.imagej;

/**
 * Stores Results in tabular format.
 **/
public class ResultsTable {
    public ij.measure.ResultsTable table = ij.measure.ResultsTable.getResultsTable();
    public static float magnification=0.1365F;
    /**
     * This method is used to generate the results table
     *
     * @param id   Unique identifier for each cell
     * @param len1 Distance between end points of path traced by cells
     * @param len2 Actual distance of path traced by the cell
//     * @param velocity Velocity of cells in μm
     * @param len2 Curvature of cell track in μm
     */
    public void createResultsTable(int id, double len1, double len2, double curvature) {
        table.incrementCounter();
        table.setPrecision(3);
        table.addValue("Id", id);
        table.addValue("Displacement (pixels)", len1);
        table.addValue("Length (pixels)", len2);
//        table.addValue("Velocity (μm/sec)", velocity);
        table.addValue("Curvature (μm)",curvature*magnification);

    }
}
