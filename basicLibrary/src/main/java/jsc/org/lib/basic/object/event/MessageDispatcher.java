package jsc.org.lib.basic.object.event;

import android.content.Context;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class MessageDispatcher {
    private static MessageDispatcher instance = null;
    private Context applicationContext;
    private final Map<String, MessageReceiver> receivers = new HashMap<>();

    private MessageDispatcher() {
    }

    public static MessageDispatcher getInstance() {
        if (instance == null) {
            instance = new MessageDispatcher();
        }
        return instance;
    }

    public void init(Context context) {
        applicationContext = context.getApplicationContext();
    }

    public void reset() {
        receivers.clear();
    }

    public void register(@NonNull Class<?> clazz, @NonNull MessageReceiver receiver) {
        String className = clazz.getName();
        register(className, receiver);
    }

    public void unregister(@NonNull Class<?> clazz) {
        unregister(clazz.getName());
    }

    public void register(@NonNull String className, @NonNull MessageReceiver receiver) {
        if (!receivers.containsKey(className)) {
            receivers.put(className, receiver);
        }
    }

    public void unregister(@NonNull String className) {
        receivers.remove(className);
    }

    public synchronized void dispatchMessage(@NonNull String action, @Nullable Message message) {
        dispatchMessage(null, action, message);
    }

    public synchronized void dispatchMessage(@Nullable String className, @NonNull String action, @Nullable Message message) {
        if (applicationContext == null)
            throw new IllegalArgumentException("please call init(Context) method first.");

        if (className == null || className.length() == 0) {
            Collection<MessageReceiver> collection = receivers.values();
            for (MessageReceiver receiver : collection) {
                receiver.onReceive(applicationContext, action, message);
            }
            return;
        }
        MessageReceiver receiver = receivers.get(className);
        if (receiver != null) {
            receiver.onReceive(applicationContext, action, message);
        }
    }
}
