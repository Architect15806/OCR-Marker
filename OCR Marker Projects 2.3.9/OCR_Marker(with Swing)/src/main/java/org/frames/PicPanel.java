package org.frames;

import org.XmlReader.XmlAccessor;
import org.listeners.MyMouseListener;
import org.structures.FilePair;
import org.structures.RectArc;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

public class PicPanel extends JPanel {
    public BufferedImage pic;

    public ArrayList<RectArc> rectArcList = null;

    public FilePair filePair;

    public int dragRectX1 = 0;
    public int dragRectY1 = 0;
    public int dragRectX2 = 0;
    public int dragRectY2 = 0;
        //拖动框的坐标

    public boolean paintFinishFlag = false;
        //当拖动结束时被改为true


    //构造函数，产生监听，装载图像和Xml数据
    public PicPanel(MyMouseListener myMouseListener, int width, int height, BufferedImage pic, FilePair filePair) {
        this.addMouseListener(myMouseListener);
        this.addMouseMotionListener(myMouseListener);

        this.setLayout(new BorderLayout());
        rectArcList = new ArrayList<>();

        this.pic = pic;
        this.filePair = filePair;

        XmlAccessor.Xml2RectArcList(width, height, this.filePair, this.rectArcList);

        System.out.println("Picture Size : (" + width + ", " + height + ")");

    }

    //重建本对象，延续监听使用，重新装载图像及Xml数据
    public void resetPicPanel(int width, int height, BufferedImage pic, FilePair filePair) {

        XmlAccessor.RectArcList2Xml(width, height, this.filePair, this.rectArcList);

        rectArcList.clear();
        this.pic = pic;
        this.filePair = filePair;

        XmlAccessor.Xml2RectArcList(width, height, this.filePair, this.rectArcList);

        System.out.println("Picture Size : (" + width + ", " + height + ")");
        this.repaint();

    }

    //绘制目标图片
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        //下面这行是为了背景图片可以跟随窗口自行调整大小，可以自己设置成固定大小
        g.drawImage(pic, 0, 0,this.getWidth(), this.getHeight(), this);
    }

    //绘制函数
    public void paint(Graphics g) {
        // 必须先调用父类的paint方法
        super.paint(g);

        //从rectFrameList中绘制已有的矩形框
        Iterator<RectArc> it = rectArcList.iterator();
        while(it.hasNext()){
            paintRectArc(it.next(), g);
        }

        paintRectArc(dragRectX1, dragRectY1, dragRectX2, dragRectY2, g);
    }

    //接收新的矩形框，添加到rectFrameList
    public void newRectArc(){
        RectArc rect = new RectArc(dragRectX1, dragRectY1, dragRectX2, dragRectY2);
        rectArcList.add(rect);
        dragRectX1 = 0;
        dragRectY1 = 0;
        dragRectX2 = 0;
        dragRectY2 = 0;
    }


    //绘制矩形框，用左上和右下点的坐标
    private void paintRectArc(int X1, int Y1, int X2, int Y2, Graphics g){
        if((X1 != X2) && (Y1 != Y2)) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(3.0f));
            g2d.drawLine(X1, Y1, X2, Y1);
            g2d.drawLine(X1, Y2, X2, Y2);
            g2d.drawLine(X1, Y1, X1, Y2);
            g2d.drawLine(X2, Y1, X2, Y2);
        }
    }

    //绘制矩形框，用RectArc
    private void paintRectArc(RectArc rect, Graphics g){
        Graphics2D g2d = (Graphics2D)g;
        g2d.setColor(rect.color);
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.drawLine(rect.X1, rect.Y1, rect.X2, rect.Y1);
        g2d.drawLine(rect.X1, rect.Y2, rect.X2, rect.Y2);
        g2d.drawLine(rect.X1, rect.Y1, rect.X1, rect.Y2);
        g2d.drawLine(rect.X2, rect.Y1, rect.X2, rect.Y2);
    }

}
