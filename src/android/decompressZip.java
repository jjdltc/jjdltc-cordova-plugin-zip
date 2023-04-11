/*
 * The MIT License (MIT)
 * Copyright (c) 2015 Joel De La Torriente - jjdltc - https://github.com/jjdltc
 * See a full copy of license in the root folder of the project
 */
package com.jjdltc.cordova.plugin.zip;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DecompressZip {
    private static final int BUFFER_SIZE = 2048;

    private final String zipPath;
    private final byte[] buffer = new byte[BUFFER_SIZE];

    public DecompressZip(final String zipPath) {
        this.zipPath = zipPath;
    }
    
    public void unzip(final String targetPath) throws IOException{
        File target = new File(targetPath);
        if (!target.exists()) {
            target.mkdirs();
        }
        
        try (ZipInputStream zipFl= new ZipInputStream(new FileInputStream(this.zipPath))) {
            ZipEntry entry      = zipFl.getNextEntry();

            while (entry != null) {
                String filePath = target.getAbsolutePath() + File.separator + entry.getName();
                File targetDir = new File(filePath.substring(0, filePath.lastIndexOf("/")));

                if (!targetDir.exists()) {
                    targetDir.mkdirs();
                }

                extractFile(zipFl, filePath);
                zipFl.closeEntry();
                entry = zipFl.getNextEntry();
            }
        }
    }

    private void extractFile(ZipInputStream zipFl, String filePath) throws IOException {
        try (BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(filePath))) {
            int read = 0;
            while ((read = zipFl.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
        }
    }
}
