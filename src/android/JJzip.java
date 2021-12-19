/*
 * The MIT License (MIT)
 * Copyright (c) 2015 Joel De La Torriente - jjdltc - https://github.com/jjdltc
 * See a full copy of license in the root folder of the project
 */
package com.jjdltc.cordova.plugin.zip;

import android.net.Uri;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;

import java.io.File;

public class JJzip extends CordovaPlugin {
    private static final String TAG = "JJZip";
    private final String ZIP_FILES = "zipFiles";
    private final String ZIP_FOLDER = "zipFolder";
    private final String UNZIP = "unzip";
    
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        switch (action) {
            case ZIP_FILES:
                zipFiles(args, callbackContext);
                break;
            case ZIP_FOLDER:
                zipFolder(args, callbackContext);
                break;
            case UNZIP:
                unzip(args, callbackContext);
                break;
            default:
                return false;
        }

        return true;
    }

    private void zipFiles(final JSONArray args, final CallbackContext callbackContext) {
        try {
            final JSONArray fileUrls = args.getJSONArray(0);
            final String targetPath = Uri.parse(args.getString(1)).getPath();

            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    try(final CompressZip zip = new CompressZip(targetPath)) {
                        for (int i = 0; i < fileUrls.length(); i++) {
                            zip.addFile(new File(Uri.parse(fileUrls.getString(i)).getPath()));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Unhandled exception compressing files: " + e);
                        callbackContext.error(e.getMessage());
                    }
                    callbackContext.success();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Unhandled exception parsing parameters: " + e);
            callbackContext.error(e.getMessage());
        }
    }

    private void zipFolder(final JSONArray args, final CallbackContext callbackContext) {
        try {
            final String folderPath = Uri.parse(args.getString(0)).getPath();
            final String targetPath = Uri.parse(args.getString(1)).getPath();

            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    try(final CompressZip zip = new CompressZip(targetPath)) {
                        zip.addDir(new File(folderPath));
                    } catch (Exception e) {
                        Log.e(TAG, "Unhandled exception compressing files: " + e);
                        callbackContext.error(e.getMessage());
                    }
                    callbackContext.success();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Unhandled exception parsing parameters: " + e);
            callbackContext.error(e.getMessage());
        }
    }

    private void unzip(final JSONArray args, final CallbackContext callbackContext) {
        try {
            final String zipPath = Uri.parse(args.getString(0)).getPath();
            final String targetPath = Uri.parse(args.getString(1)).getPath();

            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    try {
                        new DecompressZip(zipPath).unzip(targetPath);
                    } catch (Exception e) {
                        Log.e(TAG, "Unhandled exception decompressing files: " + e);
                        callbackContext.error(e.getMessage());
                    }
                    callbackContext.success();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Unhandled exception parsing parameters: " + e);
            callbackContext.error(e.getMessage());
        }
    }
}
