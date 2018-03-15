package com.example.qman.rockpad;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qman.rockpad.ifkytekUtil.JsonParser;
import com.example.qman.rockpad.service.PlayMusicService;
import com.example.qman.rockpad.tools.VoiceSpeaker;
import com.example.qman.rockpad.utils.TimerUtil;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;

import java.util.Timer;
import java.util.TimerTask;

import stonectr.serial.SerialController;

public class WakeUpActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton wakeup_button = null;
    private TextView info ;
    private SpeechRecognizer mIat;
    private boolean isListening = false;
    private boolean willClose = true;
    private Timer timer = null;//计时器
    private TimerTask timerTask = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wakeup);
        wakeup_button = (ImageButton) findViewById(R.id.wakeup_button);
        wakeup_button.setOnClickListener(this);
        info = (TextView)findViewById(R.id.wakeup_info);
        //10秒后自动退回
    }

    @Override
    protected void onResume() {
        super.onResume();
        TimerUtil.stopTime();
        TimerUtil.startTime(WakeUpActivity.this,  5000);
        info.setText("请问您需要什么帮助？");
        mIat = SpeechRecognizer.createRecognizer(this, null);
        initIat();
        if (isListening)
        {
            mIat.stopListening();
        }
        mIat.startListening(mRecognizerListener);
        // VoiceSpeaker.getInstance().speak("有什么可以帮到您");
    }

    @Override
    public void onClick(View v) {
        // ActivityUtil.toastShow(this,"点我干嘛？");
        this.finish();
    }


    /**
     * 语义监听初始化
     */
    private void initIat() {
        mIat.setParameter(SpeechConstant.DOMAIN, "iat");
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");
    }

    /**
     * 语义识别的监听器
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            //    showTip("开始说话");
            Toast.makeText(WakeUpActivity.this, "请开始说话", Toast.LENGTH_SHORT).show();
            isListening = true;
        }
        @Override
        public void onError(SpeechError error) {
            Log.e("onError1: ", error.getPlainDescription(true));
            isListening = false;
            Toast.makeText(WakeUpActivity.this, "您没有说话，可能是录音机权限被禁，需要您打开应用的录音权限", Toast.LENGTH_LONG).show();
            willClose = true;
            finish();
        }
        @Override
        public void onEndOfSpeech() {
            isListening = false;
        }
        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            String text = JsonParser.parseIatResult(results.getResultString());
            Log.d("voice", text);
            isListening = false;
            resolve(text);
            mIat.destroy();
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            Log.d("", "返回音频数据：" + data.length);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

        }
    };


    public static final String[] BODY_FORWARD = {"往前", "向前", "前进"};
    public static final String[] BODY_BACKWARD = {"后退", "向后", "往后"};
    public static final String[] BODY_LEFT = {"左转", "向左"};
    public static final String[] BODY_RIGHT = {"右转", "向右", "看右边"};

    public void resolve(String str) {

        willClose = false;
        if (str == null || str.length() == 0 || str.equals("。") || str.equals("！") || str.equals("？")) {
            return;
        }
        // if(str.contains("你好") || str.contains("您好"))
        {
           // VoiceSpeaker.getInstance().speak("您好");
        }
        if (str.contains(BODY_FORWARD[0]) || str.contains(BODY_FORWARD[1]) || str.contains(BODY_FORWARD[2])) {
            if (str.contains("不") || str.contains("别") || str.contains("甭") || str.contains("禁止")) {
                return;
            }
            //    if (str.contains("米"))
            SerialController.getInstance().sendForward();

        } else if (str.contains(BODY_BACKWARD[0]) || str.contains(BODY_BACKWARD[1])) {
            if (str.contains("不") || str.contains("别") || str.contains("甭") || str.contains("禁止"))
                return;
            SerialController.getInstance().sendBackward();
        } else if (str.contains(BODY_LEFT[0]) || str.contains(BODY_LEFT[1])) {
            if (str.contains("不") || str.contains("别") || str.contains("甭") || str.contains("禁止"))
                return;
            SerialController.getInstance().sendLeft();
        } else if (str.contains(BODY_RIGHT[0]) || str.contains(BODY_RIGHT[1])) {
            if (str.contains("不") || str.contains("别") || str.contains("甭") || str.contains("禁止"))
                return;
            SerialController.getInstance().sendRight();
        }
        else if (str.contains("打开") || str.contains("关闭"))
        {
            boolean isTurnOn = false;
            if (str.contains("打开"))
            {
                isTurnOn = true;
            }
            if (str.contains("灯"))
            {
                if (str.contains("客厅"))
                {
                    info.setText(info.getText().toString() + "\n即将为您"+(isTurnOn ? " 打开 " : " 关闭 ")+"客厅灯 ");
                }
                else if (str.contains("餐厅"))
                {
                    info.setText(info.getText().toString() + "\n即将为您"+(isTurnOn ? " 打开 " : " 关闭 ")+"餐厅大灯 ");
                }
                else if (str.contains("厨房"))
                {
                    info.setText(info.getText().toString() + "\n即将为您"+(isTurnOn ? " 打开 " : " 关闭 ")+"厨房灯 ");
                }
                else if (str.contains("卧室"))
                {
                    if (str.contains("台灯"))
                        info.setText(info.getText().toString() + "\n即将为您"+(isTurnOn ? " 打开 " : " 关闭 ")+"卧室台灯 ");
                    else
                        info.setText(info.getText().toString() + "\n即将为您"+(isTurnOn ? " 打开 " : " 关闭 ")+"卧室灯 ");
                }
                else if (str.contains("书房"))
                {
                    info.setText(info.getText().toString() + "\n即将为您"+(isTurnOn ? " 打开 " : " 关闭 ")+"书房顶灯 ");
                }
                else if (str.contains("卫生间"))
                {
                    info.setText(info.getText().toString() + "\n即将为您"+(isTurnOn ? " 打开 " : " 关闭 ")+"卫生间灯 ");
                }
                else if (str.contains("所有"))
                {
                    info.setText(info.getText().toString() + "\n即将为您"+(isTurnOn ? " 打开 " : " 关闭 ")+"所有灯 ");
                }
            }
            else if (str.contains("窗帘"))
            {
                info.setText(info.getText().toString() + "\n即将为您"+(isTurnOn ? " 打开 " : " 关闭 ")+"窗帘 ");
            }
            else if (str.contains("空调"))
            {
                info.setText(info.getText().toString() + "\n即将为您"+(isTurnOn ? " 打开 " : " 关闭 ")+"空调 ");
            }
            else if (str.contains("加湿器"))
            {
                info.setText(info.getText().toString() + "\n即将为您"+(isTurnOn ? " 打开 " : " 关闭 ")+"加湿器 ");
            }
            else if (str.contains("电视机"))
            {
                info.setText(info.getText().toString() + "\n即将为您"+(isTurnOn ? " 打开 " : " 关闭 ")+"电视机 ");
            }
            else if (str.contains("音乐"))
            {
                playingmusic(PlayMusicService.STOP_MUSIC, -1);
            }
            TimerUtil.startTime(WakeUpActivity.this,  4000);
        }


        else if(str.contains("高兴") || str.contains("见到你"))
        {
            VoiceSpeaker.getInstance().speak("欢迎光临伊利集团");
        }
        else if (str.contains("可口可乐")) {
            info.setText(info.getText().toString() + "\n正在为您查询\"可口可乐\"信息信息");
            jumpToMap(2);
        }else if (str.contains("健力宝")) {
            info.setText(info.getText().toString() + "\n正在为您查询\"健力宝\"信息信息");
            jumpToMap(1);
        }else if (str.contains("百岁山")) {
            info.setText(info.getText().toString() + "\n正在为您查询\"百岁山\"信息信息");
            jumpToMap(4);
        }else if (str.contains("维他")) {
            info.setText(info.getText().toString() + "\n正在为您查询\"维他柠檬茶\"信息信息");
            jumpToMap(5);
        }
        else if (str.contains("农夫山泉")) {
            info.setText(info.getText().toString() + "\n正在为您查询\"农夫山泉\"信息信息");
            jumpToMap(9);
        }else if (str.contains("绿茶")) {
            info.setText(info.getText().toString() + "\n正在为您查询\"绿茶\"信息信息");
            jumpToMap(6);
        }else if (str.contains("小明")||str.contains("小茗")||str.contains("小明同学")) {
            info.setText(info.getText().toString() + "\n正在为您查询\"小茗同学\"信息信息");
            jumpToMap(7);
        }else if (str.contains("怡宝")||str.contains("医保")) {
            info.setText(info.getText().toString() + "\n正在为您查询\"怡宝\"信息信息");
            jumpToMap(8);
        }else if (str.contains("名人")||str.contains("名仁")||str.contains("名人矿泉水")) {
            info.setText(info.getText().toString() + "\n正在为您查询\"名仁\"信息信息");
            jumpToMap(3);
        }else if (str.contains("碳酸饮料")) {
            info.setText(info.getText().toString() + "\n正在为您查询\"碳酸饮料\"信息信息");
            jumpToMap(10);
        }else if (str.contains("茶")) {
            info.setText(info.getText().toString() + "\n正在为您查询\"茶\"信息信息");
            jumpToMap(11);
        }else if (str.contains("纯净水")||str.contains("矿泉水")) {
            info.setText(info.getText().toString() + "\n正在为您查询\"纯净水\"信息信息");
            jumpToMap(12);
        } else if (str.contains("曲奇饼干")) {
            info.setText(info.getText().toString() + "\n正在为您查询\"曲奇饼干\"信息信息");
            jumpToMap(14);
        }
        else if (str.contains("垃圾袋")) {
            info.setText(info.getText().toString() + "\n正在为您查询\"垃圾袋\"信息信息");
            jumpToMap(15);
        }
        else if (str.contains("纸杯")) {
            info.setText(info.getText().toString() + "\n正在为您查询\"纸杯\"信息信息");
            jumpToMap(16);
        }
        else if (str.contains("厕所") || str.contains("卫生间")) {
            info.setText(info.getText().toString() + "\n即将为你显示\"卫生间\"信息");
            jumpToMap(17);
        }
        else if (str.contains("收银台") ) {
            info.setText(info.getText().toString() + "\n即将为你显示\"收银台\"信息");
            jumpToMap(18);
        }
        else if (str.contains("天气"))
        {
            if (str.contains("今天"))
            {
                VoiceSpeaker.getInstance().speak("今天天气好像不太好，我觉得有点冷哎");
            }
            else
            {
                VoiceSpeaker.getInstance().speak("不好意思，我记性不太好，我只知道今天的天气啊");
            }
        }
        else if (str.contains("你是谁"))
        {
            VoiceSpeaker.getInstance().speak("我是石头啊，不过你也可以叫我石头石头");
        }

        else if (str.contains("简介") || str.contains("伊利信条")|| str.contains("信条")) {

            info.setText(info.getText().toString() + "\n即将为您介绍伊利");
            playingmusic(PlayMusicService.PLAY_MUSIC,R.raw.music_yilixintiao);
        }
        else if (str.contains("愿景") || str.contains("伊利愿景")|| str.contains("愿望")) {
            info.setText(info.getText().toString() + "\n即将为您介绍伊利愿景");
            playingmusic(PlayMusicService.PLAY_MUSIC,R.raw.music_yiliyuanjing);
        }
        else if (str.contains("精神") || str.contains("伊利精神")) {
            info.setText(info.getText().toString() + "\n即将为您介绍伊利精神");
            playingmusic(PlayMusicService.PLAY_MUSIC,R.raw.music_yilijingshen);
        }
        else if (str.contains("价值观") || str.contains("核心")|| str.contains("核心价值观")) {
            info.setText(info.getText().toString() + "\n即将为您介绍伊利核心价值观");
            playingmusic(PlayMusicService.PLAY_MUSIC,R.raw.music_hexinjiazhiguan);
        }
        else if (str.contains("潘总") || str.contains("金句")|| str.contains("潘总金句")) {
            info.setText(info.getText().toString() + "\n即将为您 播放潘总金句");
            playingmusic(PlayMusicService.PLAY_MUSIC,R.raw.music_panzongjinju);
        }

        else if (str.contains("播放"))
        {

            if (str.contains("鸿雁"))
            {
                info.setText(info.getText().toString() + "\n正在为您播放 鸿雁 ");
                playingmusic(PlayMusicService.PLAY_MUSIC, R.raw.music_hongyan);
            }
            else if (str.contains("丑八怪"))
            {
                info.setText(info.getText().toString() + "\n正在为您播放 丑八怪 ");
                playingmusic(PlayMusicService.PLAY_MUSIC, R.raw.music_choubaguai);
            }
            else if (str.contains("小苹果"))
            {
                info.setText(info.getText().toString() + "\n正在为您播放 小苹果 ");
                playingmusic(PlayMusicService.PLAY_MUSIC, R.raw.music_xiaopingguo);
            }
            else if (str.contains("音乐"))
            {
                info.setText(info.getText().toString() + "\n正在为您播放 音乐 ");
                playingmusic(PlayMusicService.PLAY_MUSIC, R.raw.music_fenshoukuaile);
            }
            else if(str.contains("信条"))
            {
                info.setText(info.getText().toString()+"\n正在为您播放 伊利信条 ");
                playingmusic(PlayMusicService.PLAY_MUSIC,R.raw.music_yilixintiao);
            }
            else if(str.contains("伊利之歌") ||str.contains("之歌")||str.contains("歌"))
            {
                info.setText(info.getText().toString()+"\n正在为您播放 伊利之歌 ");
                playingmusic(PlayMusicService.PLAY_MUSIC,R.raw.music_songofyili);
            }
            else if (str.contains("潘总") || str.contains("金句")|| str.contains("潘总金句")) {
                info.setText(info.getText().toString() + "\n即将为您 播放潘总金句");
                playingmusic(PlayMusicService.PLAY_MUSIC,R.raw.music_panzongjinju);
            }
        }
        else
        {
            willClose = true;
            finish();
        }
    }

    private void playingmusic(int type, int music_id) {
        //启动服务，播放音乐
        Intent intent=new Intent(this,PlayMusicService.class);
        intent.putExtra("music_id",music_id);
        intent.putExtra("type",type);
        startService(intent);
    }

    private void jumpToMap(int p_id)
    {
        TimerUtil.stopTime();
        Intent intent = new Intent(this, MapOneActivity.class);
        intent.putExtra("product_id", p_id);
        startActivity(intent);
    }
}
