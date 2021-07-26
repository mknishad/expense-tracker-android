package com.monir.expensetracker.util;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileCreator {

  public static File createFile() {
    FileOutputStream outStream = null;
    File image_file = getDirc();
    File picFile = null;

    if (!image_file.exists() && !image_file.mkdirs()) {
      //Toast.makeText(getApplicationContext(), "Can not create directory to save image",Toast.LENGTH_SHORT).show();
      //return;
    } else {
      String date = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
      String yearFolder = new SimpleDateFormat("yyyy").format(new Date());
      yearFolder = image_file.getAbsolutePath() + "/" + yearFolder;
      createFolder(yearFolder);
      String monthFolder = new SimpleDateFormat("MMM").format(new Date()).toUpperCase();
      String tmf = yearFolder + "/" + monthFolder;
      createFolder(tmf);
      String photoFile = "expense_" + date + ".jpeg";
      String fileName = tmf + "/" + photoFile;
      picFile = new File(fileName);
    }

    return picFile;
  }

  private static void createFolder(String path) {
    File yFile = new File(path);
    if (!yFile.exists()) {
      if (yFile.mkdir()) {
        //System.out.println(path+" create");
      }
    }
  }

  private static File getDirc() {
    File dirc = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
    return new File(dirc, "expense");
  }
}
