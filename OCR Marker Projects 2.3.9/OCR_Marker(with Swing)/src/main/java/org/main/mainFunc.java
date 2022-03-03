package org.main;

import org.frames.MainFrame;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;

public class mainFunc {
    public static void main(String[] args) {

        try {BeautyEyeLNFHelper.launchBeautyEyeLNF();} catch(Exception e){}

        MainFrame mainFrame = new MainFrame();
        //XmlAccessor.XmlImport("D:\\Workspaces\\test.xml");
    }
}
