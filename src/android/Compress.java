package com.jjdltc.cordova.plugin.zip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Compress {

    private static final int BUFFER = 2048;

    public static boolean zip(File sourceFolder, File zipFile, List<String> skippDirectoriesName, List<String> skippFilesName) throws IOException {
        BufferedInputStream origin;

        zipFile.getParentFile().mkdirs();
        zipFile.createNewFile();

        FileOutputStream dest = new FileOutputStream(zipFile);
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

        if (sourceFolder.isDirectory()) {
            zipSubFolder(out, sourceFolder, sourceFolder.getParent().length(), skippDirectoriesName, skippFilesName);
        } else {
            byte data[] = new byte[BUFFER];

            FileInputStream fi = new FileInputStream(sourceFolder);
            origin = new BufferedInputStream(fi, BUFFER);

            ZipEntry entry = new ZipEntry(getLastPathComponent(sourceFolder.getPath()));
            out.putNextEntry(entry);

            int count;
            while ((count = origin.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
            }
        }
        out.close();

        return true;
    }

    private static void zipSubFolder(ZipOutputStream out, File sourceFolder, int basePathLength, List<String> skippDirectoriesName, List<String> skippFilesName) throws IOException {
        File[] fileList = sourceFolder.listFiles();
        BufferedInputStream origin;

        if (fileList != null) {
            for (File file : fileList) {
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

                if (file.isDirectory()) {
                    zipSubFolder(out, file, basePathLength, skippDirectoriesName, skippFilesName);
                } else {
                    byte data[] = new byte[BUFFER];

                    String unmodifiedFilePath = file.getPath();
                    String relativePath = unmodifiedFilePath.substring(basePathLength);
                    relativePath = relativePath.substring(relativePath.indexOf("/", 1), relativePath.length());

                    FileInputStream fi = new FileInputStream(unmodifiedFilePath);
                    origin = new BufferedInputStream(fi, BUFFER);

                    ZipEntry entry = new ZipEntry(relativePath);
                    out.putNextEntry(entry);

                    int count;
                    while ((count = origin.read(data, 0, BUFFER)) != -1) {
                        out.write(data, 0, count);
                    }

                    origin.close();
                }
            }
        }
    }

    private static String getLastPathComponent(String sourceFolderPath) {
        String[] segments = sourceFolderPath.split("/");
        String lastPathComponent = segments[segments.length - 1];
        return lastPathComponent;
    }


}
