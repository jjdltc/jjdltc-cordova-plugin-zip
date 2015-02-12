package org.jjdltc.cordova.plugin.zip;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class decompressZip {

	private String sourcePath 	= "";
	private String targetPath 	= "";
    private static final int BUFFER_SIZE = 2048;

	public decompressZip(JSONObject opts) {
		this.sourcePath = opts.optString("source");
		this.targetPath = opts.optString("target");
	}
	
	public boolean unZip(){
		boolean result = false;
		try {
			result = this.doUnZip(this.targetPath);
		} catch (IOException e) {
			result = false;
		}
		return result;
	}
	
    /**
     * Extracts a zip file to a given path
     * @param actualTargetPath 	Path to un-zip
     * @throws IOException
     */	
	public boolean doUnZip(String actualTargetPath) throws IOException{
		File target = new File(actualTargetPath);
		if (!target.exists()) {
			target.mkdir();
        }
        
		ZipInputStream zipFl= new ZipInputStream(new FileInputStream(this.sourcePath));
		ZipEntry entry 		= zipFl.getNextEntry();
        
        while (entry != null) {
            String filePath = actualTargetPath + File.separator + entry.getName();
            if (entry.isDirectory()) {
                File path = new File(filePath);
                path.mkdir();
            } else {
                extractFile(zipFl, filePath);
            }
            zipFl.closeEntry();
            entry = zipFl.getNextEntry();
        }
        zipFl.close();
		return true;
	}

    /**
     * Extracts a file
     * @param zipIn
     * @param filePath
     * @throws IOException
     */
    private void extractFile(ZipInputStream zipFl, String filePath) throws IOException {
        BufferedOutputStream buffer = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipFl.read(bytesIn)) != -1) {
        	buffer.write(bytesIn, 0, read);
        }
        buffer.close();
    }
}
