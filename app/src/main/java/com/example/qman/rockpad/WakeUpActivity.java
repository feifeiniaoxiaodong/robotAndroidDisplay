package com.example.qman.rockpad;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.qman.rockpad.ifkytekUtil.JsonParser;
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

import stonectr.serial.SerialController;

public class WakeUpActivity extends Activity implements View.OnClickListener {
    private Button wakeup_button = null;

    private SpeechRecognizer mIat;
    //语音合成
    private SpeechSynthesizer mTts;
    private SpeakerVerifier mVerifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wakeup);
        wakeup_button = (Button) findViewById(R.id.wakeup_button);
        wakeup_button.setOnClickListener(this);

        mIat = SpeechRecognizer.createRecognizer(this, null);
        mTts = SpeechSynthesizer.createSynthesizer(this, null);
        initTts();
        initIat();
        mIat.startListening(mRecognizerListener);
       // mTts.startSpeaking(" 我在听您说呢", mSynListener);
    }

    @Override
    public void onClick(View v) {
       // ActivityUtil.toastShow(this,"点我干嘛？");
        this.finish();
    }


    //合成监听器
    private SynthesizerListener mSynListener = new SynthesizerListener() {
        //会话结束回调接口，没有错误时，error为null
        public void onCompleted(SpeechError error) {
            // 初始化SpeakerVerifier，InitListener为初始化完成后的回调接口
            mVerifier = SpeakerVerifier.createVerifier(WakeUpActivity.this, new InitListener() {
                @Override
                public void onInit(int errorCode) {
                    if (ErrorCode.SUCCESS == errorCode) {
                    } else {
                    }
                }
            });
            mVerifier.setParameter(SpeechConstant.PARAMS, null);
        }
        //缓冲进度回调
        //percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在文本中结束位置，info为附加信息。
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
        }
        public void onSpeakBegin() {
        }
        //暂停播放
        public void onSpeakPaused() {
        }
        //播放进度回调
        //percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在文本中结束位置.
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
        }
        //恢复播放回调接口
        public void onSpeakResumed() {
        }
        //会话事件回调接口
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
        }
    };

    /**
     * 语音播放初始化
     */
    private void initTts() {
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");//设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "100");//设置音量，范围0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
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
        }

        @Override
        public void onError(SpeechError error) {
            Log.e("onError1: ", error.getPlainDescription(true));
            Toast.makeText(WakeUpActivity.this,"您没有说话，可能是录音机权限被禁，需要您打开应用的录音权限", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onEndOfSpeech() {
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            String text = JsonParser.parseIatResult(results.getResultString());
            Log.d("voice", text);
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

    public void resolve(String str)
    {

        if (str == null || str.length() == 0 || str.equals("。") || str.equals("！") || str.equals("？")) {
            return;
        }
        if (str.contains(BODY_FORWARD[0])
                || str.contains(BODY_FORWARD[1])
                || str.contains(BODY_FORWARD[2])) {
            if (str.contains("不") || str.contains("别") || str.contains("甭") || str.contains("禁止")){
                return;
            }
            //    if (str.contains("米"))
            SerialController.getInstance().sendForward();

        } else if (str.contains(BODY_BACKWARD[0])
                || str.contains(BODY_BACKWARD[1])
                ) {
            if (str.contains("不") || str.contains("别") || str.contains("甭") || str.contains("禁止"))
                return;
            SerialController.getInstance().sendBackward();
        } else if (str.contains(BODY_LEFT[0])
                || str.contains(BODY_LEFT[1])) {
            if (str.contains("不") || str.contains("别") || str.contains("甭") || str.contains("禁止"))
                return;
            SerialController.getInstance().sendLeft();
        } else if (str.contains(BODY_RIGHT[0])
                || str.contains(BODY_RIGHT[1])) {
            if (str.contains("不") || str.contains("别") || str.contains("甭") || str.contains("禁止"))
                return;
            SerialController.getInstance().sendRight();
        }
    }

}
