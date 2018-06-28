package com.github.TwrpBuilder.util;

import android.support.annotation.NonNull;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class FWriter {

    public FWriter(@NonNull String name, String data)
    {
        run(name, data);
    }


    private void run(@NonNull String name, String data) {
        PrintWriter writer;
        try {
            writer = new PrintWriter(new FileOutputStream(name, false));
            writer.println(data);
            writer.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}