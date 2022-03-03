package org.XmlReader;

import javafx.scene.paint.Color;
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

	//以下width、height和offset都是实际显示尺寸

	//利用FilePair中的Xml文件重建
	public static void Xml2RectArcList(double width, double height, double offsetX, double offsetY, FilePair filePair, ArrayList<RectArc> rectArcList){
		if(filePair.hasInitialized) {
			double rax1, ray1, rax2, ray2, rgb_r, rgb_g, rgb_b;
			String word;
			Color color;
			try {
				File f = filePair.property;

				SAXReader reader = new SAXReader();
				Document doc = reader.read(f);
				Element root = doc.getRootElement();
				Element foo;
				for (Iterator i = root.elementIterator("rectArc"); i.hasNext(); ) {
					foo = (Element) i.next();
					rax1 = width * (Double.parseDouble(foo.elementText("startX"))) + offsetX;
					rax2 = width * (Double.parseDouble(foo.elementText("endX"))) + offsetX;
					ray1 = height * (Double.parseDouble(foo.elementText("startY"))) + offsetY;
					ray2 = height * (Double.parseDouble(foo.elementText("endY"))) + offsetY;
					word = foo.elementText("OCR");
					rgb_r = Double.parseDouble(foo.elementText("RGB_R"));
					rgb_g = Double.parseDouble(foo.elementText("RGB_G"));
					rgb_b = Double.parseDouble(foo.elementText("RGB_B"));
					color = new Color(rgb_r, rgb_g, rgb_b, 1);
					RectArc ra = new RectArc(rax1, ray1, rax2, ray2, word, color);
					rectArcList.add(ra);
				}
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
	}

	public static void RectArcList2Xml(double width, double height, double offsetX, double offsetY, FilePair filePair, ArrayList<RectArc> rectArcList){
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
				if(ra.isRemoved)
					continue;
				Element rectArc = rectArcs.addElement("rectArc");
				Element startX = rectArc.addElement("startX");
				startX.setText("" + ((ra.X1 - offsetX) / width));
				Element startY = rectArc.addElement("startY");
				startY.setText("" + ((ra.Y1 - offsetY) / height));
				Element endX = rectArc.addElement("endX");
				endX.setText("" + ((ra.X2 - offsetX) / width));
				Element endY = rectArc.addElement("endY");
				endY.setText("" + ((ra.Y2 - offsetY) / height));
				Element OCR = rectArc.addElement("OCR");
				OCR.setText(ra.getWord());
				Element RGB_R = rectArc.addElement("RGB_R");
				RGB_R.setText("" + ra.color.getRed());
				Element RGB_G = rectArc.addElement("RGB_G");
				RGB_G.setText("" + ra.color.getGreen());
				Element RGB_B = rectArc.addElement("RGB_B");
				RGB_B.setText("" + ra.color.getBlue());
				System.out.println("保存成功");

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
