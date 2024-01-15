package jsc.org.lib.basic.object;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import jsc.org.lib.basic.thread.HandlerRunnable;

public class CopyFileRunnable extends HandlerRunnable {

    private File from = null;
    private File to = null;

    public CopyFileRunnable(Handler mHandler, File from, File to) {
        super(mHandler);
        this.from = from;
        this.to = to;
    }

    @Override
    public void run() {
        FileChannel in = null;
        FileChannel out = null;
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        long progress = 0;
        long total = 0;
        try {
            inStream = new FileInputStream(from);
            outStream = new FileOutputStream(to.isDirectory() ? new File(to, from.getName()) : to);
            in = inStream.getChannel();
            total = in.size();
            out = outStream.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(4096);
            int count = 0;
            while (!isCanceled() && in.read(buffer) != -1) {
                buffer.flip();
                progress += out.write(buffer);
                buffer.clear();
                count++;
                if (count >= 25) {
                    Bundle data = new Bundle();
                    data.putLong("progress", progress);
                    data.putLong("total", total);
                    Message msg = Message.obtain();
                    msg.what = 0x5000;
                    msg.setData(data);
                    sendMessage(msg);
                    count = 0;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
                if (in != null) {
                    in.close();
                }
                if (outStream != null) {
                    outStream.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            boolean success = progress == total;
            Bundle data = new Bundle();
            data.putBoolean("success", success);
            data.putString("filePath", to.isDirectory() ? new File(to, from.getName()).getPath() : to.getPath());
            Message msg = Message.obtain();
            msg.what = success ? 0x5002 : 0x5001;
            msg.setData(data);
            sendMessage(msg);
        }
    }
}
