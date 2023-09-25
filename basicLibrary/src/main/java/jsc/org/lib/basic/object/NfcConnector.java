package jsc.org.lib.basic.object;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

public final class NfcConnector {

    private Activity activity;
    private NfcAdapter mNfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFiltersArray;
    private String[][] techListsArray;
    private OnNfcBCallback callback = null;

    public void init(Activity context, OnNfcBCallback callback, boolean toSettingIfNotEnable) {
        if (this.activity != null) return;
        this.activity = context;
        this.callback = callback;
        mNfcAdapter = NfcAdapter.getDefaultAdapter(context);
        if (mNfcAdapter == null) {
            Toast.makeText(context.getApplicationContext(), "设备不支持NFC功能。", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(context, 0,
                    new Intent(context.getApplicationContext(), context.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                    PendingIntent.FLAG_MUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(context, 0,
                    new Intent(context.getApplicationContext(), context.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        }
        IntentFilter tech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        tech.addCategory(Intent.CATEGORY_DEFAULT);
        intentFiltersArray = new IntentFilter[]{tech};
        techListsArray = new String[][]{new String[]{NfcB.class.getName()}, new String[]{NfcA.class.getName()}};
        if (!mNfcAdapter.isEnabled()) {
            Toast.makeText(context, "NFC功能未开启。", Toast.LENGTH_SHORT).show();
            if (toSettingIfNotEnable) {
                new AlertDialog.Builder(context)
                        .setMessage("检测到NFC未开启。跳转到NFC设置？")
                        .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                                activity.startActivity(intent);
                            }
                        }).show();
            }
        }
    }

    public static boolean isSupported(Context context) {
        return NfcAdapter.getDefaultAdapter(context) != null;
    }

    public static boolean isEnable(Context context) {
        return NfcAdapter.getDefaultAdapter(context) != null && NfcAdapter.getDefaultAdapter(context).isEnabled();
    }

    public void onNewIntent(Intent intent) {
        if (callback != null) {
            callback.callback(intent);
        }
    }

    public void onResume() {
        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(activity, pendingIntent, intentFiltersArray, techListsArray);
        }
    }

    public void onPause() {
        if (activity.isFinishing() && mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(activity);
        }
    }

    public interface OnNfcBCallback {
        /**
         * Tag tag = nfcIntent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
         * NfcB nfcB = NfcB.get(tag);
         *
         * @param nfcIntent nfc intent
         */
        void callback(Intent nfcIntent);
    }
}
