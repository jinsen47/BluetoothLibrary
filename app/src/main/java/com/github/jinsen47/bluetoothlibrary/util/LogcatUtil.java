package com.github.jinsen47.bluetoothlibrary.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Jinsen on 15/10/10.
 */
public class LogcatUtil {
    private StringBuffer sb;

    public LogcatUtil(StringBuffer sb) {
        this.sb = sb;
    }

    public void start() {
        try {
            Runtime.getRuntime().exec("logcat -c");

            ArrayList<String> commandLine = new ArrayList<String>();
            commandLine.add("logcat");
            commandLine.add("-d");
            commandLine.add("-v");
            commandLine.add("time");
            commandLine.add("-s");
            commandLine.add("tag:W");
            Process process = Runtime.getRuntime().exec("logcat ");
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(process.getInputStream()), 512);
            String line = bufferedReader.readLine();
                while ( line != null) {
                sb.append(line);
                sb.append("\n");
                }
            } catch ( IOException e) {
        }
    }
}
