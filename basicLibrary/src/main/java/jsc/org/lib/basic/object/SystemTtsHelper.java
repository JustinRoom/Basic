package jsc.org.lib.basic.object;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.Locale;

/**
 * 系统自带文字转语音引擎
 */
public class SystemTtsHelper {

    private static SystemTtsHelper instance = null;
    private Context mContext;
    private Locale mLanguage;
    private TextToSpeech tts = null;
    private boolean available = false;

    private SystemTtsHelper() {
    }

    public static SystemTtsHelper getInstance() {
        if (instance == null) {
            instance = new SystemTtsHelper();
        }
        return instance;
    }

    public void register(Context context, @NonNull Locale language) {
        if (tts == null) {
            mContext = context;
            mLanguage = language;
            tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        initLanguage();
                    }
                }
            });
        }
    }

    private void initLanguage() {
        //设置播放语言
        int result = tts.setLanguage(mLanguage);
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Toast.makeText(mContext, "不支持", Toast.LENGTH_SHORT).show();
        } else if (result == TextToSpeech.LANG_AVAILABLE) {
            //初始化成功之后才可以播放文字
            //否则会提示“speak failed: not bound to tts engine
            //TextToSpeech.QUEUE_ADD会将加入队列的待播报文字按顺序播放
            //TextToSpeech.QUEUE_FLUSH会替换原有文字
            available = true;
        }
    }

    public void unregister() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }

    /**
     * 加入队列的待播报文字按顺序播放
     * @param txt
     */
    public void addSpeak(String txt) {
        if (tts != null && available && txt != null && txt.trim().length() > 0) {
            tts.speak(txt, TextToSpeech.QUEUE_ADD, null);
        }
    }

    /**
     * 替换原有文字
     * @param txt
     */
    public void flushSpeak(String txt) {
        if (tts != null && available && txt != null && txt.trim().length() > 0) {
            tts.speak(txt, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public void stopSpeak() {
        if (tts != null) {
            tts.stop();
        }
    }
}
