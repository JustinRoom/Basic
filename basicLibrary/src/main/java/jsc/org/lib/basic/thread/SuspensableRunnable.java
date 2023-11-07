package jsc.org.lib.basic.thread;

import android.os.Handler;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public abstract class SuspensableRunnable extends HandlerRunnable {

    //非公平阻塞队列
    private final ReentrantLock lock = new ReentrantLock(false);
    private final Condition notPaused;
    private boolean stopped = false;
    private boolean paused = true;

    public SuspensableRunnable(Handler mHandler) {
        super(mHandler);
        notPaused = lock.newCondition();
    }

    public boolean isPaused() {
        return paused;
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
        signal();
    }

    public boolean isStopped() {
        return stopped;
    }

    public void stop() {
        if (!isStopped()) {
            stopped = true;
            pause();
            signal();
        }
    }

    @Override
    public void run() {
        onPreBusiness();
        while (!isStopped()) {
            await();
            if (!isStopped()) {
                onBusiness();
            }
        }
        onEndBusiness();
    }

    private void signal() {
        final ReentrantLock lock = this.lock;
        try {
            lock.lock();
            notPaused.signal();
        } finally {
            lock.unlock();
        }
    }

    private void await() {
        if (!isPaused()) return;
        final ReentrantLock lock = this.lock;
        try {
            lock.lockInterruptibly();
            notPaused.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public abstract void onPreBusiness();

    public abstract void onBusiness();

    public abstract void onEndBusiness();

}
