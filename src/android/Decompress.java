package com.jjdltc.cordova.plugin.zip;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;


public class Decompress {

    public static boolean unzip(File zipFile, File destinationFolder) {
        FileUtil.createFolders(destinationFolder.getPath(), "");

        try {
            FileInputStream fileInputStream = new FileInputStream(zipFile);
            ZipInputStream zipInputStream = new ZipInputStream(fileInputStream);
            ZipEntry ze;
            int zipEntriesCount = 0;
            while ((ze = zipInputStream.getNextEntry()) != null) {
                zipEntriesCount++;

                if (ze.isDirectory()) {
                    FileUtil.createFolders(destinationFolder.getPath(), ze.getName());
                } else {
                    String relativeFilePath = ze.getName();
                    if (relativeFilePath == null || relativeFilePath.isEmpty()) {
                        return false;
                    }

                    if (relativeFilePath.contains("/")) {
                        String folderContainingFile = relativeFilePath.substring(0, relativeFilePath.lastIndexOf("/"));
                        FileUtil.createFolders(destinationFolder.getPath(), folderContainingFile);
                    }

                    FileOutputStream fileOutputStream = new FileOutputStream(new File(destinationFolder, ze.getName()));
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                    byte[] readBytes = new byte[1024];
                    int readSize;
                    while ((readSize = zipInputStream.read(readBytes)) >= 0) {
                        bufferedOutputStream.write(readBytes, 0, readSize);
                    }

                    zipInputStream.closeEntry();
                    bufferedOutputStream.close();
                    fileOutputStream.close();
                }
            }
            zipInputStream.close();

            if (zipEntriesCount > 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private int zipEntriesCount(String path) throws IOException {
        ZipFile zf = new ZipFile(path);
        return zf.size();
    }
}
