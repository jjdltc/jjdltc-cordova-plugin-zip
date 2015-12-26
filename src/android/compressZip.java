/*
 * The MIT License (MIT)
 * Copyright (c) 2015 Joel De La Torriente - jjdltc - http://www.jjdltc.com/
 * See a full copy of license in the root folder of the project
 */
package com.jjdltc.cordova.plugin.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.json.JSONObject;

import android.util.Log;

public class compressZip {
	
	private String sourceEntry 	= "";
	private String targetPath 	= "";
	private String sourcePath 	= "";
	private String sourceName 	= "";
	private String targetName 	= "";
    private final int BUFFER_SIZE = 2048;
    
	public compressZip(JSONObject options) {
		this.sourceEntry 	= options.optString("sourceEntry");
		this.targetPath 	= options.optString("targetPath");
		this.sourcePath 	= options.optString("sourcePath");
		this.sourceName 	= options.optString("sourceName");
		this.targetName 	= (options.optString("name").isEmpty())?this.sourceName:options.optString("name");
	}
	
	/**
	 * Public access to the main class function
	 * @return 	true if none exception occurs
	 */
	public boolean zip() {
		try {
			this.makeZip(targetPath+this.targetName+".zip", this.sourceEntry);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private void makeZip(String zipFileName, String dir) throws Exception {
	    File dirObj 		= new File(dir);
	    ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
	    Log.d("JJDLTC Test Log", "Making Zip : " + zipFileName);
	    if(dirObj.isDirectory()){
		    this.addDir(dirObj, out);
	    }
	    else{
	    	this.addFile(dirObj, out);
	    }
	    out.close();
    }

	/**
	 * A convenient method to add the elements in a folder, just call the file zip function when is needit
	 * @param dirObj		Path to folder (In file object)
	 * @param out 			Output stream in construction
	 * @throws IOException
	 */
	private void addDir(File dirObj, ZipOutputStream out) throws IOException {
		File[] files = dirObj.listFiles();

	    for (int i = 0; i < files.length; i++) {
	    	if (files[i].isDirectory()) {
		        addDir(files[i], out);
		        continue;
	    	}
	    	else{
	    		this.addFile(files[i], out);
	    	}
	    }
	}
	
	/**
	 * Add the file to the zip archive
	 * @param dirObj		Path to file (In file object)
	 * @param out 			Output stream in construction
	 * @throws IOException
	 */	
	private void addFile(File toZip, ZipOutputStream out) throws IOException{
		byte[] tmpBuf = new byte[this.BUFFER_SIZE];
		
    	FileInputStream in = new FileInputStream(toZip.getAbsolutePath());
    	Log.d("JJDLTC Test Log "," Adding To Archive: " + toZip.getAbsolutePath());
    	String zipEntryPath = toZip.getAbsolutePath().replace(this.sourcePath, "");
    	out.putNextEntry(new ZipEntry(zipEntryPath));
    	int len;
    	while ((len = in.read(tmpBuf)) > 0) {
    		out.write(tmpBuf, 0, len);
    	}
    	out.closeEntry();
    	in.close();		
	}
}


