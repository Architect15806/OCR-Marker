package org.listeners;

import org.frames.MainFrame;

import java.awt.event.*;

public class MyMouseListener implements MouseListener, MouseMotionListener, MouseWheelListener {
    boolean pressedFlag = false;
        //当pressedFlag = false时，表示没有被按下
        //当pressedFlag = true时，表示已经松开


    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //System.out.println("mousePressed");
        if(MainFrame.modeFlag == 1){
            pressedFlag = true;
            MainFrame.picPanel.dragRectX1 = e.getX();
            MainFrame.picPanel.dragRectY1 = e.getY();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //System.out.println("mouseReleased");
        if(MainFrame.modeFlag == 1){
            pressedFlag = false;
            MainFrame.picPanel.paintFinishFlag = true;
            MainFrame.picPanel.newRectArc();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        //System.out.println("mouseDragged");
        if(MainFrame.modeFlag == 1 && pressedFlag){
            MainFrame.picPanel.dragRectX2 = e.getX();
            MainFrame.picPanel.dragRectY2 = e.getY();
            MainFrame.picPanel.repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {


    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

    }
}
