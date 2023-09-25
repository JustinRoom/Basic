package jsc.org.lib.basic.object;

import android.widget.TextView;

public final class UpdateTextRunnable implements Runnable {

    TextView view = null;
    CharSequence text = "";

    public void bindTextView(TextView view) {
        this.view = view;
    }

    public void updateText(CharSequence txt) {
        this.text = txt;
    }

    @Override
    public void run() {
        if (view != null) {
            view.setText(text);
        }
    }
}
