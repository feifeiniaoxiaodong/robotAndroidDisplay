package com.example.qman.rockpad.tools;

import android.os.Bundle;

import com.example.qman.rockpad.application.StoneRbtApp;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeakerVerifier;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

/**
 * Created by sunshine on 2017/6/24.
 */

public class VoiceSpeaker {
    private SpeechRecognizer mIat;
    //语音合成
    private SpeechSynthesizer mTts;
    private SpeakerVerifier mVerifier;
    private static VoiceSpeaker instance;
    private VoiceSpeaker()
    {
        mTts = SpeechSynthesizer.createSynthesizer(StoneRbtApp.getApplication(), null);
        initTts();
    }
    public static VoiceSpeaker getInstance()
    {
        if (instance == null)
            instance = new VoiceSpeaker();
        return instance;
    }

    public void speak( String str)
    {
        mTts.startSpeaking(" " + str, mSynListener);
    }

    /**
     * 语音播放初始化
     */
    private void initTts() {
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");//设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "100");//设置音量，范围0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
    }

    //合成监听器
    private SynthesizerListener mSynListener = new SynthesizerListener() {
        //会话结束回调接口，没有错误时，error为null
        public void onCompleted(SpeechError error) {
            // 初始化SpeakerVerifier，InitListener为初始化完成后的回调接口
            mVerifier = SpeakerVerifier.createVerifier(StoneRbtApp.getApplication(), new InitListener() {
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

}
