package jsc.org.lib.basic.object.event;

import android.content.Context;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface MessageReceiver {
    void onReceive(@NonNull Context appContext, @NonNull String action, @Nullable Message msg);
}
