package com.huangxiaobo.crawler.common;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by hxb on 2018/4/1.
 */
public class FileUtils {

  public static void readToBuffer(StringBuffer buffer, String filePath) throws IOException {
    InputStream is = new FileInputStream(filePath);
    String line;
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    line = reader.readLine();
    while (line != null) {
      buffer.append(line);
      buffer.append("\n");
      line = reader.readLine();
    }
    reader.close();
    is.close();
  }
}
