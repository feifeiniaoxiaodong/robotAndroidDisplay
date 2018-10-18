package com.example.qman.rockpad.test;

import android.content.Context;

import com.example.qman.rockpad.tools.FindLocalMusicUrl;
import com.example.qman.rockpad.utils.HttpUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DownloadMusic  implements  Runnable{
    Context context=null;
    public DownloadMusic(Context context){
        this.context=context;
    }

    String musicurl="";
//    @Override
    public void run2() {

        HttpUtil.sendOkHttpRequest(musicurl, new Callback() {
           @Override
            public void onResponse(Call call, Response response) throws IOException {
                response.body().byteStream();

            }
            @Override
            public void onFailure(Call call, IOException e) {

            }
        });
    }

    @Override
    public void run() {

        String musicdesc="播放小苹果";

        String musicpath= FindLocalMusicUrl.searchLocalMusic(context ,musicdesc);



    }




}
