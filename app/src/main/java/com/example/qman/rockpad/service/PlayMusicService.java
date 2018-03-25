package com.example.qman.rockpad.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;

/**
 * Created by sunshine on 2017/7/16.
 * 一种服务只有一个实例
 */
public class PlayMusicService extends Service {

    public static final int PLAY_MUSIC = 1;
    public static final int PAUSE_MUSIC = 2;
    public static final int STOP_MUSIC = 3;
    //用于播放音乐等媒体资源
    private MediaPlayer mediaPlayer=null;
    //标志判断播放歌曲是否是停止之后重新播放，还是继续播放
    private boolean isStop = true;

    /**
     * onBind，返回一个IBinder，可以与Activity交互
     * 这是Bind Service的生命周期方法
     *
     * @param intent
     * @return
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //在此方法中服务被创建
    @Override
    public void onCreate() {
        super.onCreate();
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            //为播放器添加播放完成时的监听器
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    //发送广播到MainActivity,目前未实现
                    Intent intent = new Intent();
                    intent.setAction("com.music.complete");
                    sendBroadcast(intent);
                }
            });
        }
    }

    /**
     * 在此方法中，可以执行相关逻辑，如耗时操作
     *
     * @param intent  :由Activity传递给service的信息，存在intent中
     * @param flags   ：规定的额外信息
     * @param startId ：开启服务时，如果有规定id，则传入startid
     * @return 返回值规定此startservice是哪种类型，粘性的还是非粘性的
     * START_STICKY:粘性的，遇到异常停止后重新启动，并且intent=null
     * START_NOT_STICKY:非粘性，遇到异常停止不会重启
     * START_REDELIVER_INTENT:粘性的，重新启动，并且将Context传递的信息intent传递
     * 此方法是唯一的可以执行很多次的方法
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getIntExtra("type", -1)) {
            case PLAY_MUSIC:
                if (isStop) {
//                    int music_id = intent.getIntExtra("music_id",-1);
                    String music_path=intent.getStringExtra("music_path");

                    if (music_path !=null && !"".equals(music_path.trim())) {
                        //重置mediaplayer
                        mediaPlayer.reset();
                        //将需要播放的资源与之绑定
//                        mediaPlayer = MediaPlayer.create(this, music_id);
                       try {
                            mediaPlayer.setDataSource(music_path);//从sacard读取歌曲进行播放
                            mediaPlayer.prepareAsync();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //开始播放
                        mediaPlayer.start();
                        //是否循环播放
                        mediaPlayer.setLooping(false);
                        isStop = false;
                    }
                } else if (!isStop && mediaPlayer.isPlaying() && mediaPlayer != null) {
                    mediaPlayer.start();
                }
                break;
            case PAUSE_MUSIC:
                //播放器不为空，并且正在播放
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
                break;
            case STOP_MUSIC:
                if (mediaPlayer != null) {
                    //停止之后要开始播放音乐，停止音乐播放之后为release掉MediaPlayer
                    mediaPlayer.stop();
                    isStop = true;
                }
                break;
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }

}
