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
import com.example.qman.rockpad.tools.VoiceSpeaker;
import com.example.qman.rockpad.utils.ActivityUtil;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeakerVerifier;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

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
       // startTime();
    }

    /**
     * 开始自动减时
     */
    private void startTime() {
        if(timer==null){
            timer = new Timer();
        }

        timerTask = new TimerTask() {

            @Override
            public void run() {
                finish();
            }
        };
        timer.schedule(timerTask, 10000);//1000ms执行一次
    }
    /**
     * 停止自动减时
     */
    private void stopTime() {
        if(timer!=null)
            timer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        } else if (str.contains("可口可乐")) {
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
                VoiceSpeaker.getInstance().speak("今天北京有雷阵雨，十八到二十九度，适合运动");
            }
            else
            {
                VoiceSpeaker.getInstance().speak("我只知道今天的天气啊");
            }
        }
        else if (str.contains("你是谁"))
        {
            VoiceSpeaker.getInstance().speak("我是石头啊，我妈妈是董洪义");
        }
        else
        {
            willClose = true;
            finish();
        }
    }


    private void jumpToMap(int p_id)
    {
        Intent intent = new Intent(this, MapOneActivity.class);
        intent.putExtra("product_id", p_id);
        startActivity(intent);
    }
}
