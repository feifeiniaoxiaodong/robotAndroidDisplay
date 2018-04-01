package com.example.qman.rockpad.utils;

import java.io.*;

/**
 * 图片存取工具
 */
public class ImagUtil {

    public static FileInputStream readImag(String path) throws FileNotFoundException {
        File file=new File(path);
        return new FileInputStream( file);
    }

    public static void saveImage(InputStream in,String targetpath){
        File file=new File(targetpath);
        String path= targetpath.substring(0,targetpath.lastIndexOf('/'));
        if(!file.exists()){
            new File(path).mkdirs();
        }
        FileOutputStream out=null;
        try{
            int len=0;
            out=new FileOutputStream( file);
            byte[] buffer=new byte[2048];
            while( (len=in.read(buffer))!=-1){
                out.write(buffer,0,len);
            }
            out.flush();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(out!=null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
