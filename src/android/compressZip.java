/*
 * The MIT License (MIT)
 * Copyright (c) 2015 Joel De La Torriente - jjdltc - https://github.com/jjdltc
 * See a full copy of license in the root folder of the project
 */
package com.jjdltc.cordova.plugin.zip;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class compressZip {

    private final int BUFFER_SIZE = 2048;
    private String sourceEntry = "";
    private String targetPath = "";
    private String sourcePath = "";
    private String sourceName = "";
    private String targetName = "";
    private String source = "";
    private String zipFile = "";
    private List<String> directoriesToBeSkipped;
    private List<String> filesToBeSkipped;

    public compressZip(JSONArray args) {
         source       = args.optString(0).replace("file://", "");
        JSONObject extraOptObj  = args.optJSONObject(1);
         zipFile   = extraOptObj.optString("target").replace("file://", "");
        try {
            this.directoriesToBeSkipped =  toList(args.optJSONArray(2));
            this.filesToBeSkipped = toList(args.optJSONArray(3));
        } catch (JSONException e) {
            e.printStackTrace();
        };


    }

    private static String getLastPathComponent(String sourceFolderPath) {
        String[] segments = sourceFolderPath.split("/");
        String lastPathComponent = segments[segments.length - 1];
        return lastPathComponent;
    }

    /**
     * Public access to the main class function
     *
     * @return true if none exception occurs
     */
    public boolean zip() {
        try {
            Compress.zip(new File(source),new File(zipFile),directoriesToBeSkipped,filesToBeSkipped);
//            this.makeZip(targetPath + this.targetName + ".ecar", this.sourceEntry, new ArrayList<>(), new ArrayList<>());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void makeZip(String zipFileName, String dir, List<String> skippDirectoriesName, List<String> skippFilesName) throws Exception {
        File dirObj = new File(dir);
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
        Log.d("JJDLTC Test Log", "Making Zip : " + zipFileName);
        if (dirObj.isDirectory()) {
            this.addDir(dirObj, out, dirObj.getParent().length(), skippDirectoriesName, skippFilesName);
        } else {
            this.addFile(dirObj, out);
        }
        out.close();
    }

    /**
     * A convenient method to add the elements in a folder, just call the file zip function when is needit
     *
     * @param dirObj Path to folder (In file object)
     * @param out    Output stream in construction
     * @throws IOException
     */
    private void addDir(File file, ZipOutputStream out, int basePathLength, List<String> skippDirectoriesName, List<String> skippFilesName) throws IOException {
        File[] files = file.listFiles();

        for (int i = 0; i < files.length; i++) {
            //skip manifest.json file from exploded sdcard content
            if (!(skippDirectoriesName == null || skippDirectoriesName.isEmpty())
                    && skippDirectoriesName.contains(file.getName())) {
                continue;
            }
            if (!(skippFilesName == null || skippFilesName.isEmpty())
                    && !(file.getPath() == null || file.getPath().length() == 0)) {
                boolean skip = false;
                for (String fileName : skippFilesName) {
                    if (file.getPath().contains(fileName)) {
                        skip = true;
                        break;
                    }
                }
                if (skip) {
                    continue;
                }
            }
            if (files[i].isDirectory()) {
                addDir(files[i], out, basePathLength, skippDirectoriesName, skippFilesName);
                continue;
            } else {
                this.addFile(files[i], out);
            }
        }
    }

    /**
     * Add the file to the zip archive
     *
     * @param dirObj Path to file (In file object)
     * @param out    Output stream in construction
     * @throws IOException
     */
    private void addFile(File toZip, ZipOutputStream out) throws IOException {
        byte[] tmpBuf = new byte[this.BUFFER_SIZE];

        FileInputStream in = new FileInputStream(toZip.getAbsolutePath());
        Log.d("JJDLTC Test Log ", " Adding To Archive: " + toZip.getAbsolutePath());
        out.putNextEntry(new ZipEntry(getLastPathComponent(toZip.getPath())));
        int len;
        while ((len = in.read(tmpBuf)) > 0) {
            out.write(tmpBuf, 0, len);
        }
        out.closeEntry();
        in.close();
    }

    private List toList(JSONArray array) throws JSONException {
        int length = array.length();
        List<String> values = new ArrayList();
        for (int i = 0; i < length; i++) {
            values.add(array.getString(i));
        }
        return values;
    }
}


