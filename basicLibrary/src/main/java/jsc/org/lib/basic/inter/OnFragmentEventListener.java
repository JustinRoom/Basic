package jsc.org.lib.basic.inter;

import android.os.Bundle;

import androidx.annotation.Nullable;

public interface OnFragmentEventListener {
    void onEvent(int key, @Nullable Bundle data);
}
