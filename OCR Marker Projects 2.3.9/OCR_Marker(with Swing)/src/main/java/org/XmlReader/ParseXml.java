package org.XmlReader;

import java.io.*;
import java.util.*;
import org.dom4j.*;
import org.dom4j.io.*;

public class ParseXml {
	public static void main(String arge[]) {
		long lasting = System.currentTimeMillis();
		try {
			File f = new File("D:\\Workspaces\\test.xml");
			SAXReader reader = new SAXReader();
			Document doc = reader.read(f);
			Element root = doc.getRootElement();
			Element foo;
			for (Iterator i = root.elementIterator("VALUE"); i.hasNext();) {
				foo = (Element) i.next();
				System.out.print("车牌号码:" + foo.elementText("NO"));
				System.out.println("车主地址:" + foo.elementText("ADDR"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}