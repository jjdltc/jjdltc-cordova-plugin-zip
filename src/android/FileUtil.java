package com.jjdltc.cordova.plugin.zip;

import java.io.File;

/**
 * Created by swayangjit on 10/3/19.
 */
public class FileUtil {
  public static void createFolders(String loc, String dir) {
    File f = new File(loc, dir);
    if (!f.isDirectory()) {
      f.mkdirs();
    }

  }
}
