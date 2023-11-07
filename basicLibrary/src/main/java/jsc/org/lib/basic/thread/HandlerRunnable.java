package jsc.org.lib.basic.thread;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public abstract class HandlerRunnable implements Runnable {

    private Handler mHandler = null;
    private Bundle arguments = null;
    private boolean canceled = false;

    public void releaseSources() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    public HandlerRunnable(Handler mHandler) {
        this.mHandler = mHandler;
    }

    public Bundle getArguments() {
        return arguments;
    }

    public void setArguments(Bundle arguments) {
        this.arguments = arguments;
    }

    public void cancel() {
        canceled = true;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void start() {
        new Thread(this).start();
    }

    public final Message obtain(int what, Bundle data) {
        Message message = Message.obtain();
        message.what = what;
        message.setData(data);
        return message;
    }

    public final void sendEmptyMessage(int what) {
        if (mHandler != null) {
            mHandler.sendEmptyMessage(what);
        }
    }

    public final void sendMessage(Message message) {
        if (mHandler != null) {
            mHandler.sendMessage(message);
        }
    }

    public final void postRunnable(Runnable r, long delay) {
        if (mHandler != null && r != null) {
            mHandler.postDelayed(r, delay);
        }
    }

    public final void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignore) {

        }
    }
}
