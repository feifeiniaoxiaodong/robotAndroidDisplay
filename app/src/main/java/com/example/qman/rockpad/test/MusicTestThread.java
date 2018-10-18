package com.example.qman.rockpad.test;

import android.content.Context;
import android.content.Intent;

import com.example.qman.rockpad.R;
import com.example.qman.rockpad.service.PlayMusicService;
import com.example.qman.rockpad.tools.FindLocalMusicUrl;

/**
 * 测试音乐
 * Created by wei on 2018/3/25.
 */

public class MusicTestThread implements Runnable {

    private Context context=null;
    public  MusicTestThread(Context context){
        this.context=context;
    }

    @Override
    public void run() {

        String musicdesc="播放小苹果";

        String musicpath= FindLocalMusicUrl.searchLocalMusic(context ,musicdesc);

//        String path=context.getResources().getString(R.string.choubaguai);

        playingmusic(PlayMusicService.PLAY_MUSIC, musicpath);

      /*  playingmusic(PlayMusicService.PAUSE_MUSIC,""); //pause
//
        playingmusic(PlayMusicService.PLAY_MUSIC, path); //restart
//
//
        playingmusic(PlayMusicService.STOP_MUSIC , ""); //stop
//
//
        playingmusic(PlayMusicService.PLAY_MUSIC, path); //restart
//
        playingmusic(PlayMusicService.STOP_MUSIC , ""); //stop*/

    }



    /**
     * 播放sacard中的音乐
     * @param type：操作类型，启动、暂停、停止
     * @param path：音乐文件在sdcard中的地址
     */
    private void playingmusic( int type, String path){
        //启动服务，播放音乐
        Intent intent=new Intent(context,PlayMusicService.class);
        intent.putExtra("music_path",path );
        intent.putExtra("type",type);
        context.startService(intent);
    }

}
