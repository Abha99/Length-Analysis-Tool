package com.uga.imagej;

import ij.IJ;
import ij.gui.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
//import javafx.geometry.Insets;


import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class FrameWindow extends JFrame
        implements WindowListener, ActionListener, AdjustmentListener {
    public FrameWindow(String FrameTitle) {
        super(FrameTitle);
        Label lb1, lb2, lb3, lb4;
        JScrollBar minSlider, maxSlider,brightSlider,contrastSlider;
        Button apply,cancel;

        Font monoFont = new Font("Monospaced", Font.PLAIN, 15);
        Font sanFont = IJ.font12;

        setLayout(new FlowLayout());
        setSize(200,300);
        setLocation(300,100);
        addWindowListener(this);

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints grid = new GridBagConstraints();


        grid.gridx = 0;
        int y = 0;
        grid.gridy = y++;
        grid.fill = GridBagConstraints.BOTH;
        grid.anchor = GridBagConstraints.CENTER;
        add(new Label("0"));
        grid.insets = new Insets(0,10,10,100);
        grid.gridy = y++;
        add(new Label("255"));

        Panel panel = new Panel();
        y=0;
        grid.gridx = 1;
        grid.gridy = y++;
        grid.insets = new Insets(50, 10, 0, 10);
        gridbag.setConstraints(panel, grid);
        panel.setLayout(new BorderLayout());

        lb1=new Label("Minimum");
        lb1.setFont(monoFont);
        minSlider = new  JScrollBar(Scrollbar.HORIZONTAL,0,5,0,255);
        minSlider.setSize(150,20);
        minSlider.setPreferredSize(minSlider.getSize());
        grid.gridy = y++;
        grid.insets = new Insets(50, 10, 0, 10);
        gridbag.setConstraints(minSlider, grid);
        add(minSlider);
        minSlider.addAdjustmentListener(this);
//        minSlider.addKeyListener(ij);
        minSlider.setUnitIncrement(1);
        minSlider.setFocusable(false); // prevents blinking on Windows
        add(lb1, null);

        lb2 = new Label("Maximum");
        lb2.setFont(monoFont);
        maxSlider = new  JScrollBar(Scrollbar.HORIZONTAL,0,5,0,255);
        maxSlider.setPreferredSize(minSlider.getSize());
        grid.gridy = y++;
        grid.insets = new Insets(2, 10, 0, 10);
        gridbag.setConstraints(maxSlider, grid);
        add(maxSlider);
        maxSlider.addAdjustmentListener(this);
//        maxSlider.addKeyListener(ij);
        maxSlider.setUnitIncrement(1);
        maxSlider.setFocusable(false);
        add(lb2, null);

        lb3 = new Label("Brightness");
        lb3.setFont(monoFont);
        brightSlider = new  JScrollBar(Scrollbar.HORIZONTAL,0,5,0,255);
        brightSlider.setPreferredSize(minSlider.getSize());
        grid.gridy = y++;
        grid.insets = new Insets(12, 10, 0, 10);
        gridbag.setConstraints(brightSlider, grid);
        add(brightSlider);
        add(lb3);

        lb4 = new Label("Contrast");
        lb4.setFont(monoFont);
        contrastSlider = new  JScrollBar(Scrollbar.HORIZONTAL,0,5,0,255);
        contrastSlider.setPreferredSize(minSlider.getSize());
        grid.gridy = y++;
        grid.insets = new Insets(2, 10, 0, 10);
        gridbag.setConstraints(contrastSlider, grid);
        add(contrastSlider);
        add(lb4);
        contrastSlider.addAdjustmentListener(this);
//        contrastSlider.addKeyListener(ij);
        contrastSlider.setUnitIncrement(1);
        contrastSlider.setFocusable(false);




        minSlider.setBlockIncrement(5);
        maxSlider.setBlockIncrement(5);
        brightSlider.setBlockIncrement(5);
        contrastSlider.setBlockIncrement(5);



        setSize(200, 400);
        setPreferredSize(getSize());
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);


        minSlider.addAdjustmentListener(this);
//        maxSlider.addAdjustmentListener(this);
//        brightSlider.addAdjustmentListener(this);
//        contrastSlider.addAdjustmentListener(this);

//        add(BorderLayout.SOUTH,bottom);
//        apply = new Button("Apply");
//        bottom.add(apply);
//        cancel = new Button("Cancel");
//        bottom.add(cancel);
        //register button actions
        //apply.addActionListener(this);
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        this.dispose();

    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
   //write button actions
    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {

    }
}
