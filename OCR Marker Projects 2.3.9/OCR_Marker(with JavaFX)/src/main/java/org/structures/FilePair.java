package org.structures;

import java.io.File;
import java.io.IOException;

public class FilePair {
	public File picture;
	public File property;
	public String picURL;
	public  String proURL;
	public boolean hasInitialized;
	public String name;

	public FilePair(String picStr, String proStr, String name) {
		this.name = name;
		picURL = picStr;
		proURL = proStr;
		picture = new File(picStr);
		property = new File(proStr);
		hasInitialized = true;
	}

	public FilePair(String picStr, String name){
		this.name = name;
		picture = new File(picStr);
		property = new File(picStr.substring(0, picStr.lastIndexOf(".")) + ".xml");
		try {property.createNewFile();}
		catch (IOException e) {e.printStackTrace();}
		hasInitialized = false;
	}

	@Override
	public String toString() {
		return "FilePair{" +
				picture.getName() +
				", " + property.getName() +
				'}';
	}
}
