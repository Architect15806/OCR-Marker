package org.XmlReader;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.structures.FilePair;
import org.structures.RectArc;

public class RandomXmlWriter {

	public static void RectArcList2Xml(int width, int height, FilePair filePair, ArrayList<RectArc> rectArcList){
		if(!rectArcList.isEmpty()) {
			try {
				// 1、创建document对象
				Document document = DocumentHelper.createDocument();
				// 2、创建根节点rectArcs
				Element rectArcs = document.addElement("rectArcs");
				// 3、向rectArcs节点添加version属性
				//rss.addAttribute("version", "2.0");
				// 4、生成子节点及子节点内容
				Element rectArc = rectArcs.addElement("rectArc");

				Iterator it = rectArcList.iterator();
				RectArc ra = null;
				while (it.hasNext()) {
					ra = (RectArc) it.next();
					Element startX = rectArc.addElement("startX");
					startX.setText("" + (((double) (ra.X1)) / ((double) width)));
					Element startY = rectArc.addElement("startY");
					startY.setText("" + (((double) (ra.Y1)) / ((double) height)));
					Element endX = rectArc.addElement("endX");
					endX.setText("" + (((double) (ra.X2)) / ((double) width)));
					Element endY = rectArc.addElement("endY");
					endY.setText("" + (((double) (ra.Y2)) / ((double) height)));
					Element OCR = rectArc.addElement("OCR");
					endY.setText("OCR's result");
				}

				// 5、设置生成xml的格式
				OutputFormat format = OutputFormat.createPrettyPrint();
				// 设置编码格式
				format.setEncoding("GB2312");


				// 6、生成xml文件
				File file = filePair.property;
				XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
				// 设置是否转义，默认使用转义字符
				writer.setEscapeText(false);
				writer.write(document);
				writer.close();
				System.out.println("生成rss.xml成功");
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("生成rss.xml失败");
			}
		}
	}
}