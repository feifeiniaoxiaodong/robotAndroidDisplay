package com.example.qman.rockpad.tools;

import android.content.Context;

import com.example.qman.rockpad.R;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

/**
 * 根据语音找出匹配的本地音乐
 */
public class FindLocalMusicUrl {

    /**
     * 找匹配率最高大于0.45的进行播放
     * 没有返回null
     * @param context
     * @param musicdesc
     * @return
     */
    public   static  String searchLocalMusic(Context context,String musicdesc){
        String isFind=null;
       final  String regex= "^\\S+\\.mp3$";

      File file= new File(context.getResources().getString(R.string.musicrootpath));
//        File file= new File(context.getResources().getString(R.string.weiphone));
        String [] musicnames =file.list();
//      String [] musicnames2 =file.list( getFilenameFilter(regex)); //

        if(musicnames==null)  return null;
        int  matchIndex=0;
        double matchRatio=0;

        for(int i=0;i< musicnames.length;i++){
            int count=0 ;
            String musicname =musicnames[i];
            musicname=musicname.substring(0,musicname.indexOf("."));
            int len=musicname.length();
            for( int j=0;j< len;j++){
                if(musicdesc.contains(musicname.charAt(j)+"")){
                    count++;
                }
            }
            double ratio=(double) count/len; //匹配率
            if(ratio>matchRatio){
                matchRatio=ratio;
                matchIndex=i;
            }
        }
        if( matchRatio> 0.45){
           isFind= context.getResources().getString(R.string.musicrootpath)+ musicnames[matchIndex];
//            isFind= context.getResources().getString(R.string.weiphone)+ musicnames[matchIndex];
        }
        return isFind;
    }


    private static  FilenameFilter getFilenameFilter(final String regex){
        return new FilenameFilter() {
            Pattern pattern=Pattern.compile(regex);
            @Override
            public boolean accept(File dir, String name) {
                return pattern.matcher(name).matches();
            }
        };
    }


}
