package jsc.org.lib.basic.object;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;

public final class CountDownImpl<V extends android.widget.TextView> {
    private ValueAnimator animator = null;
    private CountDownListener<V> countDownListener = null;
    private int key;
    private V view = null;
    private String formatText = null;
    private boolean canceled = false;

    public CountDownImpl<V> key(int key) {
        this.key = key;
        return this;
    }

    public CountDownImpl<V> view(V view) {
        this.view = view;
        return this;
    }

    public CountDownImpl<V> text(String formatText) {
        this.formatText = formatText;
        return this;
    }

    public CountDownImpl<V> listener(CountDownListener<V> countDownListener) {
        this.countDownListener = countDownListener;
        return this;
    }

    public void countDown(int seconds) {
        countDown(seconds, 0);
    }

    public void countDown(int seconds, long startDelay) {
        if (view != null && formatText == null) {
            return;
        }
        canceled = false;
        if (animator == null) {
            animator = ValueAnimator.ofInt(seconds, 0);
            animator.setInterpolator(new LinearInterpolator());
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (!canceled) {
                        if (view != null) {
                            view.setText(String.format(java.util.Locale.US, formatText, 0));
                        }
                        if (countDownListener != null) {
                            countDownListener.finished(key, view);
                        }
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    canceled = true;
                    if (countDownListener != null) {
                        countDownListener.cancel(key, view);
                    }
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animator.addUpdateListener(animation -> {
                int value = (Integer) animation.getAnimatedValue();
                if (view != null) {
                    view.setText(String.format(java.util.Locale.US, formatText, value));
                }
                if (countDownListener != null) {
                    countDownListener.down(key, view, value);
                }
            });
            animator.setDuration(seconds * 1000L);
        }
        if (view != null) {
            view.setText(String.format(java.util.Locale.US, formatText, seconds));
        }
        if (startDelay > 0) {
            animator.setStartDelay(startDelay);
        } else {
            animator.start();
        }
    }

    public void cancelCountDownAnim() {
        if (animator != null && animator.isRunning()) {
            canceled = true;
            animator.cancel();
        }
    }

    public interface CountDownListener<V> {

        void down(int key, V v, int value);

        void cancel(int key, V v);

        void finished(int key, V v);
    }
}