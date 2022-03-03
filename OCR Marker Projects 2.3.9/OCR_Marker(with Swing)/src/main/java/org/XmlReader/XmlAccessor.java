package org.XmlReader;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.structures.FilePair;
import org.structures.RectArc;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class XmlAccessor {

	//测试方法：输出对应路径的Xml文件内容
	public static void XmlImport(String XmlPath) {
		try {
			File f = new File(XmlPath);
			SAXReader reader = new SAXReader();
			Document doc = reader.read(f);
			Element root = doc.getRootElement();
			Element foo;
			for (Iterator i = root.elementIterator("rectArc"); i.hasNext();) {
				foo = (Element) i.next();
				System.out.println("startX:" + foo.elementText("startX"));
				System.out.println("startY:" + foo.elementText("startY"));
				System.out.println("endX:" + foo.elementText("endX"));
				System.out.println("endY:" + foo.elementText("endY"));
				System.out.println("OCR:" + foo.elementText("OCR"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//利用FilePair中的Xml文件重建
	public static void Xml2RectArcList(int width, int height, FilePair filePair, ArrayList<RectArc> rectArcList){
		if(filePair.hasInitialized) {
			int rax1, ray1, rax2, ray2;
			try {
				File f = filePair.property;
				SAXReader reader = new SAXReader();
				Document doc = reader.read(f);
				Element root = doc.getRootElement();
				Element foo;
				for (Iterator i = root.elementIterator("rectArc"); i.hasNext(); ) {
					foo = (Element) i.next();
					rax1 = (int) (((double) width) * (Double.parseDouble(foo.elementText("startX"))));
					rax2 = (int) (((double) width) * (Double.parseDouble(foo.elementText("endX"))));
					ray1 = (int) (((double) height) * (Double.parseDouble(foo.elementText("startY"))));
					ray2 = (int) (((double) height) * (Double.parseDouble(foo.elementText("endY"))));
					RectArc ra = new RectArc(rax1, ray1, rax2, ray2);
					rectArcList.add(ra);
				}
			} catch (Exception e) {
				e.printStackTrace();

			}
		}
	}

	public static void RectArcList2Xml(int width, int height, FilePair filePair, ArrayList<RectArc> rectArcList){
		try {
			// 1、创建document对象
			Document document = DocumentHelper.createDocument();
			// 2、创建根节点rectArcs
			Element rectArcs = document.addElement("rectArcs");
			// 3、向rectArcs节点添加version属性
			//rss.addAttribute("version", "2.0");
			// 4、生成子节点及子节点内容
			Iterator it = rectArcList.iterator();
			RectArc ra = null;
			while(it.hasNext()) {
				ra = (RectArc)it.next();
				Element rectArc = rectArcs.addElement("rectArc");
				Element startX = rectArc.addElement("startX");
				startX.setText("" + (((double)(ra.X1)) / ((double)width)));
				Element startY = rectArc.addElement("startY");
				startY.setText("" + (((double)(ra.Y1)) / ((double)height)));
				Element endX = rectArc.addElement("endX");
				endX.setText("" + (((double)(ra.X2)) / ((double)width)));
				Element endY = rectArc.addElement("endY");
				endY.setText("" + (((double)(ra.Y2)) / ((double)height)));
				Element OCR = rectArc.addElement("OCR");
				OCR.setText("OCR's result");
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
			filePair.hasInitialized = true;
			System.out.println("生成rss.xml成功");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("生成rss.xml失败");
		}
	}
}
