package jsc.org.lib.basic.object;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.IOException;

public class MediaRecorderHelper {
    private static MediaRecorderHelper instance = null;
    private MediaRecorder mRecorder = null;
    private boolean start = false;

    private MediaRecorderHelper() {

    }

    public static MediaRecorderHelper getInstance() {
        if (instance == null) {
            instance = new MediaRecorderHelper();
        }
        return instance;
    }

    /**
     * @param mCamera
     * @param videoWidth
     * @param videoHeight
     * @param videoFrameRate       30
     * @param videoEncodingBitRate 3 * 1024 * 1024
     * @param orientationHint      90
     * @param maxDuration          毫秒
     */
    public void initCamera1(
            @NonNull Camera mCamera,
            int videoWidth,
            int videoHeight,
            int videoFrameRate,
            int videoEncodingBitRate,
            int orientationHint,
            int maxDuration,
            String outputFilePath) {
        mRecorder = new MediaRecorder();
        // 这两项需要放在setOutputFormat之前
        mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mCamera.unlock();
        mRecorder.setCamera(mCamera);
        mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        // Set output file format
        //输出格式 mp4
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

        // 这两项需要放在setOutputFormat之后
        //音频编码格式
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        //视频编码格式
        mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);

        //视频分辨率
        mRecorder.setVideoSize(videoWidth, videoHeight);
        //帧速率
        mRecorder.setVideoFrameRate(videoFrameRate);
        //视频清晰度
        mRecorder.setVideoEncodingBitRate(videoEncodingBitRate);
        //输出视频播放的方向提示
        mRecorder.setOrientationHint(orientationHint);
        //设置记录会话的最大持续时间（毫秒）
        mRecorder.setMaxDuration(maxDuration);

        mRecorder.setOutputFile(outputFilePath);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initCamera2(CamcorderProfile profile,
                            String outputFilePath,
                            int orientationHint) {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        if (profile != null) {
            mRecorder.setProfile(profile);
        }
        mRecorder.setOrientationHint(orientationHint);
        mRecorder.setOutputFile(outputFilePath);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startRecord() {
        if (mRecorder != null && !start) {
            start = true;
            mRecorder.start();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Surface getCamera2Surface() {
        return mRecorder == null ? null : mRecorder.getSurface();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void resume() {
        if (mRecorder != null) {
            mRecorder.resume();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void pause() {
        if (mRecorder != null) {
            mRecorder.pause();
        }
    }

    public void stopRecord() {
        try {
            if (mRecorder != null) {
                mRecorder.stop();
                mRecorder.reset();
                mRecorder.release();
                mRecorder = null;
            }
            start = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
