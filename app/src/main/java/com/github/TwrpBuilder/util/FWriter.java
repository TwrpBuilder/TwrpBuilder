package com.github.TwrpBuilder.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class FWriter {

    public FWriter(String name,String data)
    {
        run(name,data,false);
    }

    public FWriter(String name,String data,boolean o)
    {
        run(name,data,true);
    }


    private void run(String name,String data,boolean over) {
        PrintWriter writer;
        try {
            if(over==true)
            {
                writer = new PrintWriter(new FileOutputStream(name, true));
            }else {
                writer = new PrintWriter(new FileOutputStream(name, false));
            }
            writer.println(data);
            writer.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}