package jsc.org.lib.basic.thread;

import android.os.Handler;

import androidx.annotation.NonNull;

import java.util.concurrent.ArrayBlockingQueue;

public abstract class QueueRunnable<E> extends HandlerRunnable {

    protected ArrayBlockingQueue<E> queue = null;
    private boolean paused = false;

    public QueueRunnable(Handler mHandler, int capacity) {
        super(mHandler);
        queue = new ArrayBlockingQueue<>(capacity);
    }

    @NonNull
    public ArrayBlockingQueue<E> getQueue() {
        return queue;
    }

    public boolean isPaused() {
        return paused;
    }

    public void pause(boolean clearQueue) {
        //clear all elements
        paused = true;
        if (clearQueue) {
            queue.clear();
        }
    }

    public void resume() {
        paused = false;
    }

    public void cancel() {
        super.cancel();
        queue.clear();
        E item = provideInvalidItem();
        if (item != null) {
            queue.offer(item);
        }
    }

    public void offer(E item) {
        if (!isPaused() && !isCanceled()) {
            queue.offer(item);
        }
    }

    public boolean haveRemainingCapacity() {
        return queue.remainingCapacity() > 0;
    }

    @Override
    public void run() {
        onPreBusiness();
        while (!isCanceled()) {
            try {
                onBusiness(queue.take());
            } catch (InterruptedException ignore) {

            }
        }
        onEndBusiness();
    }

    /**
     * Auto called by {@link #cancel()}.
     * Putting an invalid item into the queue to unblock it.
     *
     * @return an invalid item
     */
    public abstract E provideInvalidItem();

    public abstract void onPreBusiness();

    public abstract void onBusiness(E item);

    public abstract void onEndBusiness();

}
