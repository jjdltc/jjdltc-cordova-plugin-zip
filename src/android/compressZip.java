/*
 * The MIT License (MIT)
 * Copyright (c) 2015 Joel De La Torriente - jjdltc - https://github.com/jjdltc
 * See a full copy of license in the root folder of the project
 */
package com.jjdltc.cordova.plugin.zip;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.StringJoiner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.json.JSONObject;

import android.util.Log;

import androidx.annotation.Nullable;

public class CompressZip implements Closeable {
	private static final String TAG = "JJZip:CompressZip";
	private static final int BUFFER_SIZE = 2048;

	private final ZipOutputStream output;
	private final byte[] buffer = new byte[BUFFER_SIZE];

	public CompressZip(final String targetPath) throws FileNotFoundException {
		output = new ZipOutputStream(new FileOutputStream(targetPath));
	}

	public void addDir(File dirObj) throws IOException {
		addDir(dirObj, null);
	}

	public void addDir(File dirObj, @Nullable final String relativePath) throws IOException {
		String zipDirPath = dirObj.getName();
		if (relativePath != null) {
			zipDirPath = relativePath + File.pathSeparator + zipDirPath;
		}

		File[] files = dirObj.listFiles();
		if (files == null) {
			throw new IOException("Could not get files from path: " + dirObj.getAbsolutePath());
		}

		for (File file : files) {
			if (file.isDirectory()) {
				addDir(file, zipDirPath);
			} else {
				addFile(file, zipDirPath);
			}
		}
	}

	public void addFile(File toZip) throws IOException {
		addFile(toZip, null);
	}

	public void addFile(File toZip, @Nullable final String relativePath) throws IOException {
    	Log.d(TAG," Adding To Archive: " + toZip.getAbsolutePath());

		String zipEntryPath = toZip.getName();
		if (relativePath != null) {
			zipEntryPath = relativePath + File.pathSeparator + zipEntryPath;
		}

		try (final FileInputStream in = new FileInputStream(toZip.getAbsolutePath())) {
			output.putNextEntry(new ZipEntry(zipEntryPath));
			int len;
			while ((len = in.read(buffer)) > 0) {
				output.write(buffer, 0, len);
			}
			output.closeEntry();
		}
	}

	@Override
	public void close() throws IOException {
		output.close();
	}
}


