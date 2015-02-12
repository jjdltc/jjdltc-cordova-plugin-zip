/*
 * The MIT License (MIT)
 * Copyright (c) 2015 Joel De La Torriente - jjdltc - http://www.jjdltc.com/
 * See a full copy of license in the root folder of the project
 */
package org.jjdltc.cordova.plugin.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.json.JSONObject;

public class compressZip {
	
	private String sourceEntry 	= "";
	private String targetPath 	= "";
	private String sourcePath 	= "";
    private final int BUFFER_SIZE = 2048;
    
	public compressZip(JSONObject options) {
		this.sourceEntry 	= options.optString("sourceEntry");
		this.targetPath 	= options.optString("targetPath");
		this.sourcePath 	= options.optString("sourcePath");
	}
	
	public boolean zip() {
		try {
			this.makeZip(targetPath+"CompressedGood.zip", this.sourceEntry);
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private void makeZip(String zipFileName, String dir) throws Exception {
	    File dirObj = new File(dir);
	    ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
	    System.out.println("Creating : " + zipFileName);
	    if(dirObj.isDirectory()){
		    this.addDir(dirObj, out);
	    }
	    else{
	    	this.addFile(dirObj, out);
	    }
	    out.close();
    }

	private void addDir(File dirObj, ZipOutputStream out) throws IOException {
		File[] files = dirObj.listFiles();
//	    byte[] tmpBuf = new byte[this.BUFFER_SIZE];

	    for (int i = 0; i < files.length; i++) {
	    	if (files[i].isDirectory()) {
		        addDir(files[i], out);
		        continue;
	    	}
	    	else{
	    		this.addFile(files[i], out);
	    	}
//	    	FileInputStream in = new FileInputStream(files[i].getAbsolutePath());
//	    	System.out.println(" Adding: " + files[i].getAbsolutePath());
//	    	out.putNextEntry(new ZipEntry(files[i].getAbsolutePath().replace(this.sourcePath, "")));
//	    	int len;
//	    	while ((len = in.read(tmpBuf)) > 0) {
//	    		out.write(tmpBuf, 0, len);
//	    	}
//	    	out.closeEntry();
//	    	in.close();
	    }
	}
	
	private void addFile(File toZip, ZipOutputStream out) throws IOException{
		byte[] tmpBuf = new byte[this.BUFFER_SIZE];
		
    	FileInputStream in = new FileInputStream(toZip.getAbsolutePath());
    	System.out.println(" Adding: " + toZip.getAbsolutePath());
    	out.putNextEntry(new ZipEntry(toZip.getAbsolutePath().replace(this.sourcePath, "")));
    	int len;
    	while ((len = in.read(tmpBuf)) > 0) {
    		out.write(tmpBuf, 0, len);
    	}
    	out.closeEntry();
    	in.close();		
	}
}


