package org.structures;

import java.awt.*;

public class RectArc {
	public int X1;
	public int Y1;
	public int X2;
	public int Y2;
	public Color color;

	public static Color ColorList[] = null;

	public RectArc(int x1, int y1, int x2, int y2) {
		X1 = x1;
		Y1 = y1;
		X2 = x2;
		Y2 = y2;

		if(ColorList == null)
			initColorList();

		color = getRandColor();
	}

	public Color getRandColor(){
		int max = ColorList.length - 1;
		int min = 0;
		long randomNum = System.currentTimeMillis();
		int ran3 = (int) (randomNum % (max - min) + min);
		return ColorList[ran3];
	}

	public void initColorList(){
		ColorList = new Color[10];
		ColorList[0] = new Color(128,138,135);//冷灰
		ColorList[1] = new Color(88,87,86);//象牙黑
		ColorList[2] = new Color(118,128,105);//石板灰
		ColorList[3] = new Color(227,23,13);//镉红
		ColorList[4] = new Color(156,102,31);//砖红
		ColorList[5] = new Color(176,23,31);//印度红
		ColorList[6] = new Color(116,0,0);//黑红
		ColorList[7] = new Color(0,255,0);//绿色
		ColorList[8] = new Color(128,42,42);//棕色
		ColorList[9] = new Color(199,97,20);//土色
	}
}